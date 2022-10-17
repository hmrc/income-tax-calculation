/*
 * Copyright 2022 HM Revenue & Customs
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

package services

import connectors.httpParsers.CalculationDetailsHttpParser.CalculationDetailResponse
import connectors.httpParsers.GetCalculationListHttpParser.GetCalculationListResponse
import connectors.{CalculationDetailsConnector, CalculationDetailsConnectorLegacy, GetCalculationListConnector}
import models.{DesErrorBodyModel, DesErrorModel, GetCalculationListModel}
import org.scalamock.handlers.{CallHandler3, CallHandler4}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NO_CONTENT}
import testConstants.GetCalculationDetailsConstants.successModelFull
import testUtils.TestSuite
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class GetCalculationDetailsServiceSpec extends TestSuite {

  val mockSingleCalculationConnectorLegacy: CalculationDetailsConnectorLegacy = mock[CalculationDetailsConnectorLegacy]
  val mockSingleCalculationConnector: CalculationDetailsConnector = mock[CalculationDetailsConnector]
  val mockListCalculationConnector: GetCalculationListConnector = mock[GetCalculationListConnector]
  val service = new GetCalculationDetailsService(mockSingleCalculationConnectorLegacy, mockSingleCalculationConnector, mockListCalculationConnector)

  val nino = "AA123456A"
  val taxYear = Some("2022")
  val optionalTaxYear = false
  val calculationId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"


  def getCalculationDetailsSuccessLegacy: CallHandler3[String, String, HeaderCarrier, Future[CalculationDetailResponse]] =
    (mockSingleCalculationConnectorLegacy.getCalculationDetails(_: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(Future.successful(Right((successModelFull))))

  def getCalculationDetailsSuccess: CallHandler4[String, String, String, HeaderCarrier, Future[CalculationDetailResponse]] =
    (mockSingleCalculationConnector.getCalculationDetails(_: String, _: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *, *)
      .returning(Future.successful(Right((successModelFull))))

  def listCalculationDetailsSuccess: CallHandler3[String, Option[String], HeaderCarrier, Future[GetCalculationListResponse]] =
    (mockListCalculationConnector.calcList(_: String, _: Option[String])(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(
        Future.successful(
          Right(Seq(GetCalculationListModel("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c","2019-03-17T09:22:59Z")))
        )
      )

  def getCalculationDetailsFailureLegacy: CallHandler3[String, String, HeaderCarrier, Future[CalculationDetailResponse]] =
    (mockSingleCalculationConnectorLegacy.getCalculationDetails(_: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(Future.successful(Left(DesErrorModel(INTERNAL_SERVER_ERROR, DesErrorBodyModel("error", "error")))))

  def listCalculationDetailsFailure: CallHandler3[String, Option[String], HeaderCarrier, Future[GetCalculationListResponse]] =
    (mockListCalculationConnector.calcList(_: String, _: Option[String])(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(Future.successful(Left(DesErrorModel(INTERNAL_SERVER_ERROR, DesErrorBodyModel("error", "error")))))

  def emptyListCalculationDetailsFailure: CallHandler3[String, Option[String], HeaderCarrier, Future[GetCalculationListResponse]] =
    (mockListCalculationConnector.calcList(_: String, _: Option[String])(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(Future.successful(Right(Seq.empty[GetCalculationListModel])))

  ".getCalculationDetails" should {

    "return a Right when successful" in {
      getCalculationDetailsSuccessLegacy

      listCalculationDetailsSuccess

      val result = await(service.getCalculationDetails(nino, taxYear))

      result mustBe Right(successModelFull)
    }

    "return a Left(DesError) when calling list calculations returns an empty calculation" in {

      emptyListCalculationDetailsFailure

      val result = await(service.getCalculationDetails(nino, taxYear))

      result mustBe Left(DesErrorModel(NO_CONTENT, DesErrorBodyModel("PARSING_ERROR","Error parsing response from DES")))
    }

    "return a Left(DesError) when calling list calculations and not call get calculations" in {

      listCalculationDetailsFailure

      val result = await(service.getCalculationDetails(nino, taxYear))

      result mustBe Left(DesErrorModel(INTERNAL_SERVER_ERROR, DesErrorBodyModel("error", "error")))
    }

    "return a Left(DesError) when calling list calculations succeeds however calling get calculations returns a DES error" in {

      listCalculationDetailsSuccess
      getCalculationDetailsFailureLegacy

      val result = await(service.getCalculationDetails(nino, taxYear))

      result mustBe Left(DesErrorModel(INTERNAL_SERVER_ERROR, DesErrorBodyModel("error", "error")))
    }
  }

  ".getCalculationDetailsByCalcId" should {

    "return a Right when there is no tax year given" in {
      getCalculationDetailsSuccessLegacy

      val result = await(service.getCalculationDetailsByCalcId(nino, calculationId, None))

      result mustBe Right(successModelFull)
    }

    "return a Right when successful and tax year is 23-24 or later" in {
      getCalculationDetailsSuccess

      val result = await(service.getCalculationDetailsByCalcId(nino, calculationId, Some("2024")))

      result mustBe Right(successModelFull)
    }

    "return a Right when successful and tax year is 22-23 or prior" in {
      getCalculationDetailsSuccessLegacy

      val result = await(service.getCalculationDetailsByCalcId(nino, calculationId, taxYear))

      result mustBe Right(successModelFull)
    }

    "return a Left(DesError) when calling get calculations returns a DES error" in {
      getCalculationDetailsFailureLegacy

      val result = await(service.getCalculationDetailsByCalcId(nino, calculationId, taxYear))

      result mustBe Left(DesErrorModel(INTERNAL_SERVER_ERROR, DesErrorBodyModel("error", "error")))
    }
  }
}
