/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.predicates


import common.{EnrolmentIdentifiers, EnrolmentKeys, DelegatedAuthRules}
import config.AppConfig

import javax.inject.Inject
import models.User
import play.api.Logging
import play.api.mvc.Results.Unauthorized
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.{affinityGroup, allEnrolments, confidenceLevel}
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class AuthorisedAction @Inject()()(implicit val authConnector: AuthConnector,
                                   defaultActionBuilder: DefaultActionBuilder,
                                   val cc: ControllerComponents, appConfig: AppConfig) extends AuthorisedFunctions with Logging {

  implicit val executionContext: ExecutionContext = cc.executionContext

  val unauthorized: Future[Result] = Future(Unauthorized)

  def async(block: User[AnyContent] => Future[Result]): Action[AnyContent] = defaultActionBuilder.async { implicit request =>

    implicit lazy val headerCarrier: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

    request.headers.get("mtditid").fold {
      logger.warn("[AuthorisedAction][async] - No MTDITID in the header. Returning unauthorised.")
      unauthorized
    }(
      mtdItId =>
        authorised().retrieve(affinityGroup) {
          case Some(AffinityGroup.Agent) => agentAuthentication(block, mtdItId)(request, headerCarrier)
          case _ => individualAuthentication(block, mtdItId)(request, headerCarrier)
        } recover {
          case _: NoActiveSession =>
            logger.info(s"[AuthorisedAction][async] - No active session.")
            Unauthorized
          case _: AuthorisationException =>
            logger.info(s"[AuthorisedAction][async] - User failed to authenticate")
            Unauthorized
        }
    )
  }

  lazy val minimumConfidenceLevel: Int = ConfidenceLevel.fromInt(appConfig.confidenceLevel) match {
    case Success(value) => value.level
    case Failure(ex) => ConfidenceLevel.L250.level
  }

  private[predicates] def individualAuthentication[A](block: User[A] => Future[Result], requestMtdItId: String)
                                                     (implicit request: Request[A], hc: HeaderCarrier): Future[Result] = {
    authorised().retrieve(allEnrolments and confidenceLevel) {
      case enrolments ~ userConfidence if userConfidence.level >= minimumConfidenceLevel =>
        val optionalMtdItId: Option[String] = enrolmentGetIdentifierValue(EnrolmentKeys.Individual, EnrolmentIdentifiers.individualId, enrolments)
        val optionalNino: Option[String] = enrolmentGetIdentifierValue(EnrolmentKeys.nino, EnrolmentIdentifiers.nino, enrolments)

        (optionalMtdItId, optionalNino) match {
          case (Some(authMTDITID), Some(_)) =>
            enrolments.enrolments.collectFirst {
              case Enrolment(EnrolmentKeys.Individual, enrolmentIdentifiers, _, _)
                if enrolmentIdentifiers.exists(identifier => identifier.key == EnrolmentIdentifiers.individualId && identifier.value == requestMtdItId) =>
                block(User(requestMtdItId, None))
            } getOrElse {
              logger.info(s"[AuthorisedAction][individualAuthentication] Non-agent with an invalid MTDITID. " +
                s"MTDITID in auth matches MTDITID in request: ${authMTDITID == requestMtdItId}")
              unauthorized
            }
          case (_, None) =>
            logger.info(s"[AuthorisedAction][individualAuthentication] - User has no nino.")
            unauthorized
          case (None, _) =>
            logger.info(s"[AuthorisedAction][individualAuthentication] - User has no MTD IT enrolment.")
            unauthorized
        }
      case _ =>
        logger.info(s"[AuthorisedAction][individualAuthentication] User has confidence level below ${appConfig.confidenceLevel}.")
        unauthorized
    }
  }

  private[predicates] def agentAuthPredicate(mtdId: String): Predicate =
    Enrolment(EnrolmentKeys.Individual)
      .withIdentifier(EnrolmentIdentifiers.individualId, mtdId)
      .withDelegatedAuthRule(DelegatedAuthRules.agentDelegatedAuthRule)

  private[predicates] def secondaryAgentPredicate(mtdId: String): Predicate =
    Enrolment(EnrolmentKeys.SupportingAgent)
      .withIdentifier(EnrolmentIdentifiers.individualId, mtdId)
      .withDelegatedAuthRule(DelegatedAuthRules.supportingAgentDelegatedAuthRule)

  private[predicates] def agentAuthentication[A](block: User[A] => Future[Result], mtdItId: String)
                                                (implicit request: Request[A], hc: HeaderCarrier): Future[Result] = {

    authorised(agentAuthPredicate(mtdItId))
      .retrieve(allEnrolments) {
        populateAgent(block, mtdItId, _)
      }.recoverWith(agentRecovery(block, mtdItId))
  }

  private def agentRecovery[A](block: User[A] => Future[Result],
                               mtdItId: String)
                              (implicit request: Request[A], hc: HeaderCarrier): PartialFunction[Throwable, Future[Result]] = {
    case _: NoActiveSession =>
      logger.info(s"[AuthorisedAction][agentAuthentication] - No active session.")
      unauthorized
    case _: AuthorisationException =>
      if (appConfig.emaSupportingAgentsEnabled) {
        authorised(secondaryAgentPredicate(mtdItId))
          .retrieve(allEnrolments) {
            populateAgent(block, mtdItId, _)
          }.recoverWith {
          case _ =>
            logger.info(s"[AuthorisedAction][agentAuthentication] - Agent does not have secondary delegated authority for Client.")
            unauthorized
        }
      } else {
        logger.info(s"[AuthorisedAction][agentAuthentication] - Agent does not have delegated authority for Client.")
        unauthorized
      }
  }

  private def populateAgent[A](block: User[A] => Future[Result],
                               mtdItId: String,
                               enrolments: Enrolments)(implicit request: Request[A]): Future[Result] = {
    enrolmentGetIdentifierValue(EnrolmentKeys.Agent, EnrolmentIdentifiers.agentReference, enrolments) match {
      case Some(arn) =>
        block(User(mtdItId, Some(arn)))
      case None =>
        logger.info("[AuthorisedAction][agentAuthentication] Agent with no HMRC-AS-AGENT enrolment.")
        unauthorized
    }
  }

  private[predicates] def enrolmentGetIdentifierValue(checkedKey: String,
                                                      checkedIdentifier: String,
                                                      enrolments: Enrolments): Option[String] = enrolments.enrolments.collectFirst {
    case Enrolment(`checkedKey`, enrolmentIdentifiers, _, _) => enrolmentIdentifiers.collectFirst {
      case EnrolmentIdentifier(`checkedIdentifier`, identifierValue) => identifierValue
    }
  }.flatten

}
