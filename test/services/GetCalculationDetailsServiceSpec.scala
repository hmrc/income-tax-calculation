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

package services

import connectors.httpParsers.CalculationDetailsHttpParser.CalculationDetailResponse
import connectors.httpParsers.GetCalculationListHttpParser.GetCalculationListResponse
import connectors.httpParsers.GetCalculationListHttpParserLegacy.GetCalculationListResponseLegacy
import connectors.{CalculationDetailsConnector, CalculationDetailsConnectorLegacy, GetCalculationListConnector, GetCalculationListConnectorLegacy}
import models.{ErrorBodyModel, ErrorModel, GetCalculationListModel, GetCalculationListModelLegacy}
import org.scalamock.handlers.{CallHandler2, CallHandler3, CallHandler4}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NO_CONTENT}
import testConstants.GetCalculationDetailsConstants.successModelFull
import testUtils.TestSuite
import uk.gov.hmrc.http.HeaderCarrier
import utils.TaxYear

import scala.concurrent.Future

class GetCalculationDetailsServiceSpec extends TestSuite {

  val mockSingleCalculationConnectorLegacy: CalculationDetailsConnectorLegacy = mock[CalculationDetailsConnectorLegacy]
  val mockSingleCalculationConnector: CalculationDetailsConnector = mock[CalculationDetailsConnector]
  val mockListCalculationConnector: GetCalculationListConnector = mock[GetCalculationListConnector]
  val mockListCalculationConnectorLegacy: GetCalculationListConnectorLegacy = mock[GetCalculationListConnectorLegacy]
  val service = new GetCalculationDetailsService(mockSingleCalculationConnectorLegacy, mockSingleCalculationConnector,
    mockListCalculationConnector, mockListCalculationConnectorLegacy)

  val nino = "AA123456A"
  val taxYear: Option[String] = Some("2022")
  val taxYear2016: Int = 2016
  val specificTaxYear: Option[String] = Some(TaxYear.specificTaxYear.toString)
  val specificTaxYearPlusOne: Option[String] = Some((TaxYear.specificTaxYear + 1).toString)
  val optionalTaxYear = false
  val calculationId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"


  def getCalculationDetailsSuccessLegacy: CallHandler3[String, String, HeaderCarrier, Future[CalculationDetailResponse]] =
    (mockSingleCalculationConnectorLegacy.getCalculationDetails(_: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(Future.successful(Right(successModelFull)))

  def getCalculationDetailsSuccess: CallHandler4[String, String, String, HeaderCarrier, Future[CalculationDetailResponse]] =
    (mockSingleCalculationConnector.getCalculationDetails(_: String, _: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *, *)
      .returning(Future.successful(Right(successModelFull)))

  def listCalculationDetailsSuccess: CallHandler3[String, String, HeaderCarrier, Future[GetCalculationListResponse]] =
    (mockListCalculationConnector.getCalculationList(_: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(
        Future.successful(
          Right(Seq(GetCalculationListModel(
            calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
            calculationTimestamp = "2019-03-17T09:22:59Z",
            calculationType = "inYear",
            requestedBy = Some("customer"),
            year = Some(taxYear2016),
            fromDate = Some("2013-05-d1"),
            toDate = Some("2016-05-d1"),
            totalIncomeTaxAndNicsDue = Some(500.00),
            intentToCrystallise = None,
            crystallised = None,
            crystallisationTimestamp = None
          )))
        )
      )

  def listCalculationDetailsSuccessLegacy: CallHandler3[String, Option[String], HeaderCarrier, Future[GetCalculationListResponseLegacy]] =
    (mockListCalculationConnectorLegacy.calcList(_: String, _: Option[String])(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(
        Future.successful(
          Right(Seq(GetCalculationListModelLegacy("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2019-03-17T09:22:59Z")))
        )
      )

  def getCalculationDetailsFailureLegacy: CallHandler3[String, String, HeaderCarrier, Future[CalculationDetailResponse]] =
    (mockSingleCalculationConnectorLegacy.getCalculationDetails(_: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(Future.successful(Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("error", "error")))))

  def listCalculationDetailsFailure: CallHandler3[String, Option[String], HeaderCarrier, Future[GetCalculationListResponseLegacy]] =
    (mockListCalculationConnectorLegacy.calcList(_: String, _: Option[String])(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(Future.successful(Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("error", "error")))))

  def emptyListCalculationDetailsFailure: CallHandler3[String, Option[String], HeaderCarrier, Future[GetCalculationListResponseLegacy]] =
    (mockListCalculationConnectorLegacy.calcList(_: String, _: Option[String])(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(Future.successful(Right(Seq.empty[GetCalculationListModelLegacy])))

  ".getCalculationDetails" should {

    "return a Right when successful" in {
      getCalculationDetailsSuccessLegacy

      listCalculationDetailsSuccessLegacy

      val result = await(service.getCalculationDetails(nino, taxYear))

      result mustBe Right(successModelFull)
    }

    "return a Right when successful for specific tax year" in {
      getCalculationDetailsSuccess

      listCalculationDetailsSuccess

      val result = await(service.getCalculationDetails(nino, specificTaxYear))

      result mustBe Right(successModelFull)
    }

    "return a Right when successful for specific tax year plus one" in {
      getCalculationDetailsSuccess

      listCalculationDetailsSuccess

      val result = await(service.getCalculationDetails(nino, specificTaxYearPlusOne))

      result mustBe Right(successModelFull)
    }

    "return a Left(DesError) when calling list calculations returns an empty calculation" in {

      emptyListCalculationDetailsFailure

      val result = await(service.getCalculationDetails(nino, taxYear))

      result mustBe Left(ErrorModel(NO_CONTENT, ErrorBodyModel("PARSING_ERROR", "Error parsing response from API")))
    }

    "return a Left(DesError) when calling list calculations and not call get calculations" in {

      listCalculationDetailsFailure

      val result = await(service.getCalculationDetails(nino, taxYear))

      result mustBe Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("error", "error")))
    }

    "return a Left(DesError) when calling list calculations succeeds however calling get calculations returns a DES error" in {

      listCalculationDetailsSuccessLegacy
      getCalculationDetailsFailureLegacy

      val result = await(service.getCalculationDetails(nino, taxYear))

      result mustBe Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("error", "error")))
    }
  }

  ".getCalculationDetailsByCalcId" should {

    "return a Right when there is no tax year given" in {
      getCalculationDetailsSuccessLegacy

      val result = await(service.getCalculationDetailsByCalcId(nino, calculationId, None))

      result mustBe Right(successModelFull)
    }

    "return a Right when successful for specific tax year" in {
      getCalculationDetailsSuccess

      val result = await(service.getCalculationDetailsByCalcId(nino, calculationId, specificTaxYear))

      result mustBe Right(successModelFull)
    }

    "return a Right when successful for specific tax year plus one" in {
      getCalculationDetailsSuccess

      val result = await(service.getCalculationDetailsByCalcId(nino, calculationId, specificTaxYearPlusOne))

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

      result mustBe Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("error", "error")))
    }
  }
}