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

package controllers

import connectors.httpParsers.DeclareCrystallisationHttpParser.DeclareCrystallisationResponse
import models.{ErrorBodyModel, ErrorModel}
import org.scalamock.handlers.CallHandler4
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout}
import services.DeclareCrystallisationService
import testUtils.TestSuite
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class DeclareCrystallisationControllerSpec extends TestSuite {

  val service: DeclareCrystallisationService = mock[DeclareCrystallisationService]
  val controller = new DeclareCrystallisationController(service, authorisedAction, mockControllerComponents)

  val nino = "AA123456A"
  val taxYear = 2022
  val calculationId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"

  def declareCrystallisationSuccess(): CallHandler4[String, Int, String, HeaderCarrier, Future[DeclareCrystallisationResponse]] =
    (service.declareCrystallisation(_: String, _: Int, _: String)(_: HeaderCarrier))
      .expects(*, *, *, *)
      .returning(Future.successful(Right(())))

  def declareCrystallisationFailure(httpStatus: Int): CallHandler4[String, Int, String, HeaderCarrier, Future[DeclareCrystallisationResponse]] =
    (service.declareCrystallisation(_: String, _: Int, _: String)(_: HeaderCarrier))
      .expects(*, *, *, *)
      .returning(Future.successful(Left(ErrorModel(httpStatus, ErrorBodyModel("DES_CODE", "DES_REASON")))))


  "Request from an individual" should {
    "return a 204 NoContent response when successful" in {

      val result = {
        mockAuth()
        declareCrystallisationSuccess()

        controller.declareCrystallisation(nino, taxYear, calculationId)(fakeRequestWithMtditid)
      }

      status(result) mustBe NO_CONTENT
    }

    Seq(SERVICE_UNAVAILABLE, INTERNAL_SERVER_ERROR, NOT_FOUND, CONFLICT, BAD_REQUEST).foreach{ httpErrorCode =>
      s"return a $httpErrorCode when unsuccessful" in {
        val result = {
          mockAuth()
          declareCrystallisationFailure(httpErrorCode)

          controller.declareCrystallisation(nino, taxYear, calculationId)(fakeRequestWithMtditid)
        }

        status(result) mustBe httpErrorCode
        contentAsJson(result)mustBe Json.obj("code" -> "DES_CODE", "reason" -> "DES_REASON")
      }
    }
  }

  "Request from an agent" should {
    "return a 204 NoContent response when successful" in {

      val result = {
        mockAuthAsAgent()
        declareCrystallisationSuccess()

        controller.declareCrystallisation(nino, taxYear, calculationId)(fakeRequestWithMtditid)
      }

      status(result) mustBe NO_CONTENT
    }

    Seq(SERVICE_UNAVAILABLE, INTERNAL_SERVER_ERROR, NOT_FOUND, CONFLICT, BAD_REQUEST).foreach{ httpErrorCode =>
      s"return a $httpErrorCode when unsuccessful" in {
        val result = {
          mockAuthAsAgent()
          declareCrystallisationFailure(httpErrorCode)

          controller.declareCrystallisation(nino, taxYear, calculationId)(fakeRequestWithMtditid)
        }

        status(result) mustBe httpErrorCode
        contentAsJson(result)mustBe Json.obj("code" -> "DES_CODE", "reason" -> "DES_REASON")
      }
    }

  }

}
