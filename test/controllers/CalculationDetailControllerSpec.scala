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

package controllers

import connectors.httpParsers.CalculationDetailsHttpParser.CalculationDetailResponse
import models.{ErrorBodyModel, ErrorModel}
import org.scalamock.handlers.{CallHandler3, CallHandler4}
import play.api.http.Status
import play.api.http.Status.{BAD_REQUEST, FORBIDDEN, INTERNAL_SERVER_ERROR, SERVICE_UNAVAILABLE}
import play.api.libs.json.Json
import services.GetCalculationDetailsService
import testConstants.GetCalculationDetailsConstants.successModelFull
import testUtils.TestSuite
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class CalculationDetailControllerSpec extends TestSuite {

  val service: GetCalculationDetailsService = mock[GetCalculationDetailsService]
  val controller = new CalculationDetailController(service, mockControllerComponents,authorisedAction)


  val nino = "AA123456A"
  val taxYear = "2022"
  val calculationId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"


  def calculationSuccessResponse: CallHandler3[String, Option[String], HeaderCarrier, Future[CalculationDetailResponse]] =
    (service.getCalculationDetails(_: String, _: Option[String])(_: HeaderCarrier))
      .expects(nino, Some(taxYear), *)
      .returning(Future.successful(Right(successModelFull)))

  def calculationErrorResponse(status: Int): CallHandler3[String, Option[String], HeaderCarrier, Future[CalculationDetailResponse]] =
    (service.getCalculationDetails(_: String, _: Option[String])(_: HeaderCarrier))
      .expects(nino, Some(taxYear), *)
      .returning(Future.successful(Left(ErrorModel(status, ErrorBodyModel("INTERNAL_SERVER_ERROR", "internal server error")))))

  def calculationSuccessResponseByCalcId: CallHandler4[String, String, Option[String], HeaderCarrier, Future[CalculationDetailResponse]] =
    (service.getCalculationDetailsByCalcId(_: String, _: String, _: Option[String])(_: HeaderCarrier))
      .expects(nino, calculationId, Some(taxYear), *)
      .returning(Future.successful(Right(successModelFull)))

  def calculationErrorResponseByCalcId(status: Int): CallHandler4[String, String, Option[String], HeaderCarrier, Future[CalculationDetailResponse]] =
    (service.getCalculationDetailsByCalcId(_: String, _: String, _: Option[String])(_: HeaderCarrier))
      .expects(nino, calculationId, Some(taxYear), *)
      .returning(Future.successful(Left(ErrorModel(status, ErrorBodyModel("INTERNAL_SERVER_ERROR", "internal server error")))))

  "CalculationDetailController.calculationDetail" should {

    "return a success response with a calculation model" in {

      mockAuth()
      calculationSuccessResponse

      val result = controller.calculationDetail(nino, Some(taxYear))(fakeRequestWithMtditid)
      status(result) mustBe Status.OK
      bodyOf(result) mustBe Json.toJson(successModelFull).toString()

    }


    "return a 500 error response with a calculation error" in {

      mockAuth()
      calculationErrorResponse(INTERNAL_SERVER_ERROR)

      val result = controller.calculationDetail(nino, Some(taxYear))(fakeRequestWithMtditid)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR

    }

    "return a 503 error response with a calculation error" in {

      mockAuth()
      calculationErrorResponse(SERVICE_UNAVAILABLE)

      val result = controller.calculationDetail(nino, Some(taxYear))(fakeRequestWithMtditid)
      status(result) mustBe Status.SERVICE_UNAVAILABLE

    }

    "return a 400 error response with a calculation error" in {

      mockAuth()
      calculationErrorResponse(BAD_REQUEST)

      val result = controller.calculationDetail(nino, Some(taxYear))(fakeRequestWithMtditid)
      status(result) mustBe Status.BAD_REQUEST

    }

    "return a 403 error response with a calculation error" in {

      mockAuth()
      calculationErrorResponse(FORBIDDEN)

      val result = controller.calculationDetail(nino, Some(taxYear))(fakeRequestWithMtditid)
      status(result) mustBe Status.FORBIDDEN

    }
  }
  "CalculationDetailController.calculationDetailByCalcId" should {

    "return a success response with a calculation model" in {

      mockAuth()
      calculationSuccessResponseByCalcId

      val result = controller.calculationDetailByCalcId(nino, calculationId, Some(taxYear))(fakeRequestWithMtditid)
      status(result) mustBe Status.OK
      bodyOf(result) mustBe Json.toJson(successModelFull).toString()

    }


    "return a 500 error response with a calculation error" in {

      mockAuth()
      calculationErrorResponseByCalcId(INTERNAL_SERVER_ERROR)

      val result = controller.calculationDetailByCalcId(nino, calculationId, Some(taxYear))(fakeRequestWithMtditid)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR

    }

    "return a 503 error response with a calculation error" in {

      mockAuth()
      calculationErrorResponseByCalcId(SERVICE_UNAVAILABLE)

      val result = controller.calculationDetailByCalcId(nino, calculationId, Some(taxYear))(fakeRequestWithMtditid)
      status(result) mustBe Status.SERVICE_UNAVAILABLE

    }

    "return a 400 error response with a calculation error" in {

      mockAuth()
      calculationErrorResponseByCalcId(BAD_REQUEST)

      val result = controller.calculationDetailByCalcId(nino, calculationId, Some(taxYear))(fakeRequestWithMtditid)
      status(result) mustBe Status.BAD_REQUEST

    }

    "return a 403 error response with a calculation error" in {

      mockAuth()
      calculationErrorResponseByCalcId(FORBIDDEN)

      val result = controller.calculationDetailByCalcId(nino, calculationId, Some(taxYear))(fakeRequestWithMtditid)
      status(result) mustBe Status.FORBIDDEN

    }
  }
}