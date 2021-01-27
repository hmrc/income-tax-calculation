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
import models.User
import play.api.http.Status._
import play.api.mvc.Results._
import play.api.mvc.{AnyContent, Result}
import testUtils.TestSuite
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
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

        "the correct enrolment exist" which {
          val block: User[AnyContent] => Future[Result] = user => Future.successful(Ok(user.mtditid))
          val mtditid = "AAAAAA"
          val enrolments = Enrolments(Set(Enrolment(
            EnrolmentKeys.Individual,
            Seq(EnrolmentIdentifier(EnrolmentIdentifiers.individualId, mtditid)), "Activated")
          ))

          lazy val result: Future[Result] = auth.individualAuthentication(block, enrolments, mtditid)(fakeRequest, emptyHeaderCarrier)

          "returns an OK status" in {
            status(result) mustBe OK
          }

          "returns a body of the mtditid" in {
            bodyOf(result) mustBe mtditid
          }
        }

      }

      "return a forbidden" when {

        "the correct enrolment is missing" which {
          val block: User[AnyContent] => Future[Result] = user => Future.successful(Ok(user.mtditid))
          val mtditid = "AAAAAA"
          val enrolments = Enrolments(Set(Enrolment("notAnIndividualOops", Seq(EnrolmentIdentifier(EnrolmentIdentifiers.individualId, mtditid)), "Activated")))

          lazy val result: Future[Result] = auth.individualAuthentication(block, enrolments, mtditid)(fakeRequest, emptyHeaderCarrier)

          "returns a forbidden" in {
            status(result) mustBe FORBIDDEN
          }
        }
      }
    }
    ".agentAuthenticated" should {

      val block: User[AnyContent] => Future[Result] = user => Future.successful(Ok(s"${user.mtditid} ${user.arn.get}"))
      val arn = "0987654321"

      "perform the block action" when {

        "the agent is authorised for the given user" which {

          val enrolments = Enrolments(Set(
            Enrolment(EnrolmentKeys.Individual, Seq(EnrolmentIdentifier(EnrolmentIdentifiers.individualId, "1234567890")), "Activated"),
            Enrolment(EnrolmentKeys.Agent, Seq(EnrolmentIdentifier(EnrolmentIdentifiers.agentReference, "0987654321")), "Activated")
          ))

          lazy val result = {
            (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *, *)
              .returning(Future.successful(enrolments))

            auth.agentAuthentication("1234567890", block)(fakeRequestWithMtditid, emptyHeaderCarrier)
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
            auth.agentAuthentication("1234567890", block)(fakeRequestWithMtditid, emptyHeaderCarrier)
          }
          status(result) mustBe UNAUTHORIZED
        }

      }

      "return an Unauthorised" when {

        "the authorisation service returns a NoActiveSession exception" in {
          object NoActiveSession extends NoActiveSession("Some reason")

          lazy val result = {
            mockAuthReturnException(NoActiveSession)
            auth.agentAuthentication("1234567890", block)(fakeRequestWithMtditid, emptyHeaderCarrier)
          }

          status(result) mustBe UNAUTHORIZED
        }
      }
      "return a Forbidden" when {

        "the user does not have an enrolment for the agent" in {
          val enrolments = Enrolments(Set(
            Enrolment(EnrolmentKeys.Individual, Seq(EnrolmentIdentifier(EnrolmentIdentifiers.individualId, "1234567890")), "Activated")
          ))

          lazy val result = {
            (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *, *)
              .returning(Future.successful(enrolments))
            auth.agentAuthentication("1234567890", block)(fakeRequestWithMtditid, emptyHeaderCarrier)
          }

          status(result) mustBe FORBIDDEN
        }
      }
    }

    ".checkAuthorisation" should {

      lazy val block: User[AnyContent] => Future[Result] = user =>
        Future.successful(Ok(s"mtditid: ${user.mtditid}${user.arn.fold("")(arn => " arn: " + arn)}"))

      lazy val enrolments = Enrolments(Set(
        Enrolment(EnrolmentKeys.Individual, Seq(EnrolmentIdentifier(EnrolmentIdentifiers.individualId, "1234567890")), "Activated"),
        Enrolment(EnrolmentKeys.Agent, Seq(EnrolmentIdentifier(EnrolmentIdentifiers.agentReference, "1234567890")), "Activated")
      ))

      "perform the block action" when {

        "the user is authenticated as an individual" which {
          lazy val result = auth.checkAuthorisation(block, enrolments, "1234567890")

          "returns an OK (200) status" in {
            status(result) mustBe OK
          }

          "returns the correct body" in {
            bodyOf(result) mustBe "mtditid: 1234567890"
          }
        }

        "the user is authenticated as an agent" which {
          lazy val result = {

            (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *, *)
              .returning(Future.successful(enrolments))

            auth.checkAuthorisation(block, enrolments, "1234567890", isAgent = true)(fakeRequestWithMtditid, emptyHeaderCarrier)
          }

          "returns an OK (200) status" in {
            status(result) mustBe OK
          }

          "returns the correct body" in {
            bodyOf(result) mustBe "mtditid: 1234567890 arn: 1234567890"
          }
        }
      }

      "return an Unauthorised" when {

        "the enrolments do not contain an MTDITID for a user" in {
          lazy val result = auth.checkAuthorisation(block, Enrolments(Set()), "1234567890")

          status(result) mustBe UNAUTHORIZED
        }

        "the enrolments do not contain an AgentReferenceNumber for an agent" in {
          lazy val result = auth.checkAuthorisation(block, Enrolments(Set()), "1234567890", isAgent = true)

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
            auth.async("1234567890")(block)(fakeRequest)
          }

          "should return an OK(200) status" in {

            status(result) mustBe OK
            bodyOf(result) mustBe "mtditid: 1234567890 arn: 0987654321"
          }
        }

        "the user is successfully verified as an individual" in {

          lazy val result = {
            mockAuth()
            auth.async("1234567890")(block)(fakeRequest)
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
            auth.async("1234567890")( block)
          }
          status(result(fakeRequest)) mustBe UNAUTHORIZED
        }

      }

      "return an Unauthorised" when {

        "the authorisation service returns a NoActiveSession exception" in {
          object NoActiveSession extends NoActiveSession("Some reason")

          lazy val result = {
            mockAuthReturnException(NoActiveSession)
            auth.async("1234567890")(block)
          }

          status(result(fakeRequest)) mustBe UNAUTHORIZED
        }
      }

    }
  }
}
