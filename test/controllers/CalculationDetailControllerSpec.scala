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

import connectors.httpParsers.CalculationDetailsHttpParser.CalculationDetailResponse
import models.{ErrorBodyModel, ErrorModel}
import org.mockito.Mockito.when
import org.mockito.{ArgumentMatchers, Mockito}
import play.api.http.Status
import play.api.http.Status.*
import play.api.libs.json.Json
import services.GetCalculationDetailsService
import testConstants.GetCalculationDetailsConstants.successModelFull
import testUtils.TestSuite

import scala.concurrent.Future

class CalculationDetailControllerSpec extends TestSuite {

  val service: GetCalculationDetailsService = Mockito.mock(classOf[GetCalculationDetailsService])
  val controller = new CalculationDetailController(service, mockControllerComponents,authorisedAction)


  val nino = "AA123456A"
  val taxYear = "2022"
  val calculationId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"

  def calculationResponse(nino: String, taxYearOption: Option[String], calcType: Option[String])(response: CalculationDetailResponse): Unit = {
    when(service.getCalculationDetails(ArgumentMatchers.eq(nino), ArgumentMatchers.eq(taxYearOption), ArgumentMatchers.eq(calcType))(ArgumentMatchers.any())) thenReturn Future (
      response match {
        case Right(obj) => Right(Json.toJson(obj))
        case Left(err) => Left(err)
      }
      )
  }

  def calculationResponseByCalcId(nino: String, calculationId: String, taxYearOption: Option[String])(response: CalculationDetailResponse): Unit = {
    when(service.getCalculationDetailsByCalcId(ArgumentMatchers.eq(nino), ArgumentMatchers.eq(calculationId), ArgumentMatchers.eq(taxYearOption))
    (ArgumentMatchers.any()))thenReturn Future (
      response match {
        case Right(obj) => Right(Json.toJson(obj))
        case Left(err) => Left(err)
      }
    )
  }


    "CalculationDetailController.calculationDetail" should {

    "return a success response with a calculation model" in {

      mockAuth()
      calculationResponse(nino, Some(taxYear), None)(Right(successModelFull))

      val result = controller.calculationDetail(nino, Some(taxYear), None)(fakeRequestWithMtditid)
      status(result) mustBe Status.OK
      bodyOf(result) mustBe Json.toJson(successModelFull).toString()

    }


    "return a 500 error response with a calculation error" in {

      mockAuth()
      calculationResponse(nino, Some(taxYear), None)(Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("INTERNAL_SERVER_ERROR", "internal server error"))))

      val result = controller.calculationDetail(nino, Some(taxYear), None)(fakeRequestWithMtditid)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR

    }

    "return a 503 error response with a calculation error" in {

      mockAuth()
      calculationResponse(nino, Some(taxYear), None)(Left(ErrorModel(SERVICE_UNAVAILABLE, ErrorBodyModel("SERVICE_UNAVAILABLE", "service unavailable"))))

      val result = controller.calculationDetail(nino, Some(taxYear), None)(fakeRequestWithMtditid)
      status(result) mustBe Status.SERVICE_UNAVAILABLE

    }

    "return a 400 error response with a calculation error" in {

      mockAuth()
      calculationResponse(nino, Some(taxYear), None)(Left(ErrorModel(BAD_REQUEST, ErrorBodyModel("BAD_REQUEST", "bad request"))))

      val result = controller.calculationDetail(nino, Some(taxYear), None)(fakeRequestWithMtditid)
      status(result) mustBe Status.BAD_REQUEST
    }

    "return a 403 error response with a calculation error" in {

      mockAuth()
      calculationResponse(nino, Some(taxYear), None)(Left(ErrorModel(FORBIDDEN, ErrorBodyModel("FORBIDDEN", "forbidden"))))

      val result = controller.calculationDetail(nino, Some(taxYear), None)(fakeRequestWithMtditid)
      status(result) mustBe Status.FORBIDDEN

    }
  }

  "CalculationDetailController.calculationDetailByCalcId" should {

    "return a success response with a calculation model" in {

      mockAuth()
      calculationResponseByCalcId(nino, calculationId, Some(taxYear))(Right(successModelFull))

      val result = controller.calculationDetailByCalcId(nino, calculationId, Some(taxYear))(fakeRequestWithMtditid)
      status(result) mustBe Status.OK
      bodyOf(result) mustBe Json.toJson(successModelFull).toString()

    }


    "return a 500 error response with a calculation error" in {

      mockAuth()
      calculationResponseByCalcId(nino, calculationId, Some(taxYear))(Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("INTERNAL_SERVER_ERROR", "internal server error"))))

      val result = controller.calculationDetailByCalcId(nino, calculationId, Some(taxYear))(fakeRequestWithMtditid)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR

    }

    "return a 503 error response with a calculation error" in {

      mockAuth()
      calculationResponseByCalcId(nino, calculationId, Some(taxYear))(Left(ErrorModel(SERVICE_UNAVAILABLE, ErrorBodyModel("SERVICE_UNAVAILABLE", "service unavailable"))))


      val result = controller.calculationDetailByCalcId(nino, calculationId, Some(taxYear))(fakeRequestWithMtditid)
      status(result) mustBe Status.SERVICE_UNAVAILABLE

    }

    "return a 400 error response with a calculation error" in {

      mockAuth()
      calculationResponseByCalcId(nino, calculationId, Some(taxYear))(Left(ErrorModel(BAD_REQUEST, ErrorBodyModel("BAD_REQUEST", "bad request"))))

      val result = controller.calculationDetailByCalcId(nino, calculationId, Some(taxYear))(fakeRequestWithMtditid)
      status(result) mustBe Status.BAD_REQUEST

    }

    "return a 403 error response with a calculation error" in {

      mockAuth()
      calculationResponseByCalcId(nino, calculationId, Some(taxYear))(Left(ErrorModel(FORBIDDEN, ErrorBodyModel("FORBIDDEN", "forbidden"))))

      val result = controller.calculationDetailByCalcId(nino, calculationId, Some(taxYear))(fakeRequestWithMtditid)
      status(result) mustBe Status.FORBIDDEN

    }
  }
}