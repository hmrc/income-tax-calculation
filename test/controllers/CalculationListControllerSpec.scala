/*
 * Copyright 2026 HM Revenue & Customs
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

import cats.data.EitherT
import models.{CalculationListResponseModel, ErrorBodyModel, ErrorModel, GetCalculationListModel}
import org.scalamock.handlers.CallHandler3
import play.api.http.Status.*
import play.api.libs.json.Json
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout}
import service.CalculationResult
import services.GetCalculationDetailsService
import testUtils.TestSuite
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future


class CalculationListControllerSpec extends TestSuite {

  val service: GetCalculationDetailsService = mock[GetCalculationDetailsService]
  val controller: CalculationListController = CalculationListController(service, mockControllerComponents, authorisedAction)

  val nino = "AA123456A"
  val taxYear = "2026"

  val calcListResponse: String = Json.toJson(CalculationListResponseModel(
    Seq(GetCalculationListModel("calcId", "calcTimestamp", "calcType", None, None, None))
  )).toString()
  val errorResponse = Json.obj("code" -> "error code", "reason" -> "error reason")

  def auth(isAgent: Boolean): Unit = if (isAgent) mockAuthAsAgent() else mockAuth()

  def getCalculationListResponseSuccess: CallHandler3[String, String, HeaderCarrier, CalculationResult[CalculationListResponseModel]] =
    (service.getCalculationListResponse(_: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(EitherT(Future.successful(Right(CalculationListResponseModel(
        Seq(GetCalculationListModel("calcId", "calcTimestamp", "calcType", None, None, None))
      )))))

  def getCalculationListFailure(httpStatus: Int): CallHandler3[String, String, HeaderCarrier, CalculationResult[CalculationListResponseModel]] =
    (service.getCalculationListResponse(_: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(EitherT(Future.successful(Left(ErrorModel(httpStatus, ErrorBodyModel("error code", "error reason"))))))


  Seq(false, true).foreach { isAgent =>
    s"the user is an ${if (isAgent) "agent" else "individual"}" should {
      "return a 200 Ok response when successful" in {

        val result = {
          auth(isAgent)
          getCalculationListResponseSuccess

          controller.getCalculationList(nino, taxYear)(fakeRequestWithMtditid)
        }
        status(result) mustBe OK
        contentAsJson(result) mustBe Json.parse(calcListResponse)
      }

      Seq(SERVICE_UNAVAILABLE, INTERNAL_SERVER_ERROR, NOT_FOUND, CONFLICT, BAD_REQUEST).foreach { httpErrorCode =>
        val expectedErrorCode = if (httpErrorCode == NO_CONTENT) NOT_FOUND else httpErrorCode
        s"return a $expectedErrorCode when unsuccessful" in {

          val result = {
            auth(isAgent)
            getCalculationListFailure(httpErrorCode)

            controller.getCalculationList(nino, taxYear)(fakeRequestWithMtditid)
          }
          status(result) mustBe expectedErrorCode
          contentAsJson(result) mustBe errorResponse
        }
      }
    }
  }
}
