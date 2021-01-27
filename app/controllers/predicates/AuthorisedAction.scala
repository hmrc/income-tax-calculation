/*
 * Copyright 2021 HM Revenue & Customs
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

import common.{EnrolmentIdentifiers, EnrolmentKeys}
import config.AppConfig
import javax.inject.Inject
import play.api.Logger
import play.api.mvc.Results.{Forbidden, Unauthorized}
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.{affinityGroup, allEnrolments}
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import models.User

import scala.concurrent.{ExecutionContext, Future}

class AuthorisedAction @Inject()(
                                  appConfig: AppConfig
                                )(
                                  implicit val authConnector: AuthConnector,
                                  defaultActionBuilder: DefaultActionBuilder,
                                  val cc: ControllerComponents
                                ) extends AuthorisedFunctions {


  lazy val logger: Logger = Logger.apply(this.getClass)
  implicit val executionContext: ExecutionContext = cc.executionContext


  def async(mtdItId: String)(block: User[AnyContent] => Future[Result]): Action[AnyContent] = defaultActionBuilder.async { implicit request =>
    implicit lazy val headerCarrier: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers)
    authorised.retrieve(allEnrolments and affinityGroup) {
      case enrolments ~ Some(AffinityGroup.Agent) =>
        checkAuthorisation(block, enrolments, mtdItId, isAgent = true)(request, headerCarrier)
      case enrolments ~ _ =>
        checkAuthorisation(block, enrolments, mtdItId)(request, headerCarrier)
    } recover {
      case _: NoActiveSession =>
        logger.debug("AgentPredicate][authoriseAsAgent] - No active session. Redirecting to Unauthorised")
        Unauthorized("")
      case _: AuthorisationException =>
        logger.debug(s"[AgentPredicate][authoriseAsAgent] - Agent does not have delegated authority for Client.")
        Unauthorized("")
    }
  }


  def checkAuthorisation(block: User[AnyContent] => Future[Result], enrolments: Enrolments, mtdItId: String, isAgent: Boolean = false)
                        (implicit request: Request[AnyContent], hc: HeaderCarrier): Future[Result] = {

    val neededKey = if (isAgent) EnrolmentKeys.Agent else EnrolmentKeys.Individual
    val neededIdentifier = if (isAgent) EnrolmentIdentifiers.agentReference else EnrolmentIdentifiers.individualId

    enrolmentGetIdentifierValue(neededKey, neededIdentifier, enrolments).fold(
      Future.successful(Unauthorized(""))
    ) { userId =>
      if (isAgent) agentAuthentication(mtdItId, block) else individualAuthentication(block, enrolments, userId)
    }

  }

  private[predicates] def agentAuthentication(mtdItId: String, block: User[AnyContent] => Future[Result])
                                             (implicit request: Request[AnyContent], hc: HeaderCarrier): Future[Result] = {

    val agentDelegatedAuthRuleKey = "mtd-it-auth"

    val agentAuthPredicate: String => Enrolment = identifierId =>
      Enrolment(EnrolmentKeys.Individual)
        .withIdentifier(EnrolmentIdentifiers.individualId, identifierId)
        .withDelegatedAuthRule(agentDelegatedAuthRuleKey)
    authorised(agentAuthPredicate(mtdItId)).retrieve(allEnrolments) { enrolments =>
      enrolmentGetIdentifierValue(EnrolmentKeys.Agent, EnrolmentIdentifiers.agentReference, enrolments) match {
        case Some(arn) =>
          block(User(mtdItId, Some(arn)))
        case None =>
          logger.debug("[AuthorisedAction][CheckAuthorisation] Agent with no HMRC-AS-AGENT enrolment. Rendering unauthorised view.")
          Future.successful(Forbidden(""))
      }
    } recover {
      case _: NoActiveSession =>
        logger.debug("[AgentPredicate][authoriseAsAgent] - No active session. Redirecting to Unauthorised")
        Unauthorized("")
      case ex: AuthorisationException =>
        logger.debug(s"[AgentPredicate][authoriseAsAgent] - Agent does not have delegated authority for Client.")
        Unauthorized("")
    }
  }

  private[predicates] def individualAuthentication(block: User[AnyContent] => Future[Result], enrolments: Enrolments, mtditid: String)
                                                  (implicit request: Request[AnyContent], hc: HeaderCarrier): Future[Result] = {
    enrolments.enrolments.collectFirst {
      case Enrolment(EnrolmentKeys.Individual, enrolmentIdentifiers, _, _)
        if enrolmentIdentifiers.exists(identifier => identifier.key == EnrolmentIdentifiers.individualId && identifier.value == mtditid) =>
        block(User(mtditid, None))
    } getOrElse {
      logger.warn("[AuthorisedAction][IndividualAuthentication] Non-agent with an invalid MTDITID.")
      Future.successful(Forbidden(""))
    }
  }

  private[predicates] def enrolmentGetIdentifierValue(
                                                       checkedKey: String,
                                                       checkedIdentifier: String,
                                                       enrolments: Enrolments
                                                     ): Option[String] = enrolments.enrolments.collectFirst {
    case Enrolment(`checkedKey`, enrolmentIdentifiers, _, _) => enrolmentIdentifiers.collectFirst {
      case EnrolmentIdentifier(`checkedIdentifier`, identifierValue) => identifierValue
    }
  }.flatten

}
