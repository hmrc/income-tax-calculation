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

import cats.data.EitherT
import models.hip.taxLiabilitySummaryDataSA302.TaxLiabilitySummaryDataSA302Model
import models.{ErrorBodyModel, ErrorModel}
import org.scalamock.handlers.CallHandler4
import play.api.http.Status.*
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout}
import service.CalculationResult
import services.GetTaxLiabilitySummaryDataSA302Service
import testConstants.hip.GetTaxLiabilitySummaryDataSA302TestConstants.successModelFull
import testUtils.TestSuite
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class CalculationsFinalisationSummaryControllerSpec extends TestSuite {

  val service: GetTaxLiabilitySummaryDataSA302Service = mock[GetTaxLiabilitySummaryDataSA302Service]
  val controller = new CalculationsFinalisationSummaryController(service, mockControllerComponents, authorisedAction)

  val taxYear: String = "2027"
  val taxableEntityId: String = "AB1234568"
  val calculationId: String = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"

  val taxLiabilitySummaryDataSA302Response: String = Json.toJson(successModelFull).toString
  val errorResponse: JsObject = Json.obj("code" -> "error code", "reason" -> "error reason")


  def auth(isAgent: Boolean): Unit = if (isAgent) mockAuthAsAgent() else mockAuth()

  def getTaxLiabilitySummaryDataSA302DetailsSuccess: CallHandler4[String, String, String, HeaderCarrier, CalculationResult[TaxLiabilitySummaryDataSA302Model]] =
    (service.getTaxLiabilitySummaryDataSA302Details(_: String, _: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *, *)
      .returning(EitherT(Future.successful(Right(successModelFull))))

  def getTaxLiabilitySummaryDataSA302DetailsFailure(httpStatus: Int): CallHandler4[String, String, String, HeaderCarrier, CalculationResult[TaxLiabilitySummaryDataSA302Model]] =
    (service.getTaxLiabilitySummaryDataSA302Details(_: String, _: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *, *)
      .returning(EitherT(Future.successful(Left(ErrorModel(httpStatus, ErrorBodyModel("error code", "error reason"))))))


  Seq(false, true).foreach { isAgent =>
    s"the user is an ${if (isAgent) "agent" else "individual"}" should {
      "return a 200 Ok response when successful" in {

        val result = {
          auth(isAgent)
          getTaxLiabilitySummaryDataSA302DetailsSuccess

          controller.getCalculationsFinalisationSummary(taxYear, taxableEntityId, calculationId)(fakeRequestWithMtditid)
        }

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.parse(taxLiabilitySummaryDataSA302Response)
      }

      Seq(SERVICE_UNAVAILABLE, INTERNAL_SERVER_ERROR, NOT_FOUND, CONFLICT, BAD_REQUEST).foreach { httpErrorCode =>
        val expectedErrorCode = if (httpErrorCode == NO_CONTENT) NOT_FOUND else httpErrorCode
        s"return a $expectedErrorCode when unsuccessful" in {

          val result = {
            auth(isAgent)
            getTaxLiabilitySummaryDataSA302DetailsFailure(httpErrorCode)

            controller.getCalculationsFinalisationSummary(taxYear, taxableEntityId, calculationId)(fakeRequestWithMtditid)
          }

          status(result) mustBe expectedErrorCode
          contentAsJson(result) mustBe errorResponse
        }
      }
    }
  }
}