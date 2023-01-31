/*
 * Copyright 2023 HM Revenue & Customs
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
import models.User
import play.api.http.Status._
import play.api.mvc.Results._
import play.api.mvc.{AnyContent, Result}
import testUtils.TestSuite
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.syntax.retrieved.authSyntaxForRetrieved
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier, Enrolments, _}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AuthorisedActionSpec extends TestSuite {

  val auth: AuthorisedAction = authorisedAction

  ".enrolmentGetIdentifierValue" should {

    "return the value for a given identifier" in {
      val returnValue = "anIdentifierValue"
      val returnValueAgent = "anAgentIdentifierValue"

      val enrolments = Enrolments(Set(
        Enrolment(EnrolmentKeys.Individual, Seq(EnrolmentIdentifier(EnrolmentIdentifiers.individualId, returnValue)), "Activated"),
        Enrolment(EnrolmentKeys.Agent, Seq(EnrolmentIdentifier(EnrolmentIdentifiers.agentReference, returnValueAgent)), "Activated")
      ))

      auth.enrolmentGetIdentifierValue(EnrolmentKeys.Individual, EnrolmentIdentifiers.individualId, enrolments) mustBe Some(returnValue)
      auth.enrolmentGetIdentifierValue(EnrolmentKeys.Agent, EnrolmentIdentifiers.agentReference, enrolments) mustBe Some(returnValueAgent)
    }
    "return a None" when {
      val key = "someKey"
      val identifierKey = "anIdentifier"
      val returnValue = "anIdentifierValue"

      val enrolments = Enrolments(Set(Enrolment(key, Seq(EnrolmentIdentifier(identifierKey, returnValue)), "someState")))


      "the given identifier cannot be found" in {
        auth.enrolmentGetIdentifierValue(key, "someOtherIdentifier", enrolments) mustBe None
      }

      "the given key cannot be found" in {
        auth.enrolmentGetIdentifierValue("someOtherKey", identifierKey, enrolments) mustBe None
      }

    }

    ".individualAuthentication" should {

      "perform the block action" when {

        "the correct enrolment and nino exist" which {
          val block: User[AnyContent] => Future[Result] = user => Future.successful(Ok(user.mtditid))
          val mtditid = "AAAAAA"
          val enrolments = Enrolments(Set(Enrolment(
            EnrolmentKeys.Individual,
            Seq(EnrolmentIdentifier(EnrolmentIdentifiers.individualId, mtditid)), "Activated"),
            Enrolment(
              EnrolmentKeys.nino,
              Seq(EnrolmentIdentifier(EnrolmentIdentifiers.nino, mtditid)), "Activated")
          ))

          lazy val result: Future[Result] = {
            (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, Retrievals.allEnrolments and Retrievals.confidenceLevel, *, *)
              .returning(Future.successful(enrolments and confidenceLevel))
            auth.individualAuthentication(block, mtditid)(fakeRequest, emptyHeaderCarrier)
          }

          "returns an OK status" in {
            status(result) mustBe OK
          }

          "returns a body of the mtditid" in {
            bodyOf(result) mustBe mtditid
          }
        }

      }

      "return a UNAUTHORIZED" when {

        "the correct enrolment is missing" which {
          val block: User[AnyContent] => Future[Result] = user => Future.successful(Ok(user.mtditid))
          val mtditid = "AAAAAA"
          val enrolments = Enrolments(Set(Enrolment("notAnIndividualOops", Seq(EnrolmentIdentifier(EnrolmentIdentifiers.individualId, mtditid)), "Activated")))

          lazy val result: Future[Result] = {
            (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, Retrievals.allEnrolments and Retrievals.confidenceLevel, *, *)
              .returning(Future.successful(enrolments and ConfidenceLevel.L200))
            auth.individualAuthentication(block, mtditid)(fakeRequest, emptyHeaderCarrier)
          }

          "returns a UNAUTHORIZED" in {
            status(result) mustBe UNAUTHORIZED
          }
        }
      }

      "the correct enrolment and nino exist but the request is for a different id" which {
        val block: User[AnyContent] => Future[Result] = user => Future.successful(Ok(user.mtditid))
        val mtditid = "AAAAAA"
        val enrolments = Enrolments(Set(Enrolment(
          EnrolmentKeys.Individual,
          Seq(EnrolmentIdentifier(EnrolmentIdentifiers.individualId, "123456")), "Activated"),
          Enrolment(
            EnrolmentKeys.nino,
            Seq(EnrolmentIdentifier(EnrolmentIdentifiers.nino, mtditid)), "Activated")
        ))

        lazy val result: Future[Result] = {
          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, Retrievals.allEnrolments and Retrievals.confidenceLevel, *, *)
            .returning(Future.successful(enrolments and ConfidenceLevel.L200))
          auth.individualAuthentication(block, mtditid)(fakeRequest, emptyHeaderCarrier)
        }

        "returns an UNAUTHORIZED status" in {
          status(result) mustBe UNAUTHORIZED
        }
      }
      "the correct enrolment and nino exist but low CL" which {
        val block: User[AnyContent] => Future[Result] = user => Future.successful(Ok(user.mtditid))
        val mtditid = "AAAAAA"
        val enrolments = Enrolments(Set(Enrolment(
          EnrolmentKeys.Individual,
          Seq(EnrolmentIdentifier(EnrolmentIdentifiers.individualId, mtditid)), "Activated"),
          Enrolment(
            EnrolmentKeys.nino,
            Seq(EnrolmentIdentifier(EnrolmentIdentifiers.nino, mtditid)), "Activated")
        ))

        lazy val result: Future[Result] = {
          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, Retrievals.allEnrolments and Retrievals.confidenceLevel, *, *)
            .returning(Future.successful(enrolments and ConfidenceLevel.L50))
          auth.individualAuthentication(block, mtditid)(fakeRequest, emptyHeaderCarrier)
        }

        "returns an UNAUTHORIZED status" in {
          status(result) mustBe UNAUTHORIZED
        }
      }

      "the correct enrolment exist but no nino" which {
        val block: User[AnyContent] => Future[Result] = user => Future.successful(Ok(user.mtditid))
        val mtditid = "AAAAAA"
        val enrolments = Enrolments(Set(Enrolment(
          EnrolmentKeys.Individual,
          Seq(EnrolmentIdentifier(EnrolmentIdentifiers.individualId, mtditid)), "Activated")
        ))

        lazy val result: Future[Result] = {
          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, Retrievals.allEnrolments and Retrievals.confidenceLevel, *, *)
            .returning(Future.successful(enrolments and ConfidenceLevel.L200))
          auth.individualAuthentication(block, mtditid)(fakeRequest, emptyHeaderCarrier)
        }

        "returns an 401 status" in {
          status(result) mustBe UNAUTHORIZED
        }
      }
      "the correct nino exist but no enrolment" which {
        val block: User[AnyContent] => Future[Result] = user => Future.successful(Ok(user.mtditid))
        val id = "AAAAAA"
        val enrolments = Enrolments(Set(Enrolment(
          EnrolmentKeys.nino,
          Seq(EnrolmentIdentifier(EnrolmentIdentifiers.nino, id)), "Activated")
        ))

        lazy val result: Future[Result] = {
          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, Retrievals.allEnrolments and Retrievals.confidenceLevel, *, *)
            .returning(Future.successful(enrolments and ConfidenceLevel.L200))
          auth.individualAuthentication(block, id)(fakeRequest, emptyHeaderCarrier)
        }

        "returns an 401 status" in {
          status(result) mustBe UNAUTHORIZED
        }
      }
    }
    ".agentAuthenticated" should {

      val block: User[AnyContent] => Future[Result] = user => Future.successful(Ok(s"${user.mtditid} ${user.arn.get}"))

      "perform the block action" when {

        "the agent is authorised for the given user" which {

          val enrolments = Enrolments(Set(
            Enrolment(EnrolmentKeys.Individual, Seq(EnrolmentIdentifier(EnrolmentIdentifiers.individualId, "1234567890")), "Activated"),
            Enrolment(EnrolmentKeys.Agent, Seq(EnrolmentIdentifier(EnrolmentIdentifiers.agentReference, "0987654321")), "Activated")
          ))

          lazy val result = {
            (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, Retrievals.allEnrolments, *, *)
              .returning(Future.successful(enrolments))

            auth.agentAuthentication(block, "1234567890")(fakeRequestWithMtditid, emptyHeaderCarrier)
          }

          "has a status of OK" in {
            status(result) mustBe OK
          }

          "has the correct body" in {
            bodyOf(result) mustBe "1234567890 0987654321"
          }
        }
      }

      "return an Unauthorised" when {

        "the authorisation service returns an AuthorisationException exception" in {
          object AuthException extends AuthorisationException("Some reason")

          lazy val result = {
            mockAuthReturnException(AuthException)
            auth.agentAuthentication(block, "1234567890")(fakeRequestWithMtditid, emptyHeaderCarrier)
          }
          status(result) mustBe UNAUTHORIZED
        }

      }

      "return an Unauthorised" when {

        "the authorisation service returns a NoActiveSession exception" in {
          object NoActiveSession extends NoActiveSession("Some reason")

          lazy val result = {
            mockAuthReturnException(NoActiveSession)
            auth.agentAuthentication(block, "1234567890")(fakeRequestWithMtditid, emptyHeaderCarrier)
          }

          status(result) mustBe UNAUTHORIZED
        }
      }
      "return a UNAUTHORIZED" when {

        "the user does not have an enrolment for the agent" in {
          val enrolments = Enrolments(Set(
            Enrolment(EnrolmentKeys.Individual, Seq(EnrolmentIdentifier(EnrolmentIdentifiers.individualId, "1234567890")), "Activated")
          ))

          lazy val result = {
            (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, Retrievals.allEnrolments, *, *)
              .returning(Future.successful(enrolments))
            auth.agentAuthentication(block, "1234567890")(fakeRequestWithMtditid, emptyHeaderCarrier)
          }

          status(result) mustBe UNAUTHORIZED
        }
      }
    }

    ".async" should {
      lazy val block: User[AnyContent] => Future[Result] = user =>
        Future.successful(Ok(s"mtditid: ${user.mtditid}${user.arn.fold("")(arn => " arn: " + arn)}"))

      "perform the block action" when {

        "the user is successfully verified as an agent" which {

          lazy val result = {
            mockAuthAsAgent()
            auth.async(block)(fakeRequestWithMtditid)
          }

          "should return an OK(200) status" in {

            status(result) mustBe OK
            bodyOf(result) mustBe "mtditid: 1234567890 arn: 0987654321"
          }
        }

        "the user is successfully verified as an individual" in {

          lazy val result = {
            mockAuth()
            auth.async(block)(fakeRequestWithMtditid)
          }

          status(result) mustBe OK
          bodyOf(result) mustBe "mtditid: 1234567890"
        }
      }

      "return an Unauthorised" when {

        "the authorisation service returns an AuthorisationException exception" in {
          object AuthException extends AuthorisationException("Some reason")

          lazy val result = {
            mockAuthReturnException(AuthException)
            auth.async(block)
          }
          status(result(fakeRequestWithMtditid)) mustBe UNAUTHORIZED
        }

      }

      "return an Unauthorised" when {

        "the authorisation service returns a NoActiveSession exception" in {
          object NoActiveSession extends NoActiveSession("Some reason")

          lazy val result = {
            mockAuthReturnException(NoActiveSession)
            auth.async(block)
          }

          status(result(fakeRequestWithMtditid)) mustBe UNAUTHORIZED
        }

        "the request does not contain an mtditid header" in {

          lazy val result = auth.async(block)

          status(result(fakeRequest)) mustBe UNAUTHORIZED
        }
      }

    }
  }
}
