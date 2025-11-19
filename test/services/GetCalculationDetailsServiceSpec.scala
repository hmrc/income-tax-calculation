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

package services

import config.{AppConfig, MockAppConfig}
import connectors.hip.{HipCalculationLegacyListConnector, HipGetCalculationListConnector, HipGetCalculationsDataConnector}
import connectors.httpParsers.CalculationDetailsHttpParser.CalculationDetailResponse
import connectors.httpParsers.GetCalculationListHttpParser.GetCalculationListResponse
import connectors.httpParsers.GetCalculationListHttpParserLegacy.GetCalculationListResponseLegacy
import connectors.{CalculationDetailsConnector, CalculationDetailsConnectorLegacy, GetCalculationListConnector}
import models.{ErrorBodyModel, ErrorModel, GetCalculationListModel, GetCalculationListModelLegacy}
import org.scalamock.handlers.{CallHandler3, CallHandler4}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NO_CONTENT}
import play.api.libs.json.Json
import testConstants.GetCalculationDetailsConstants.successModelFull
import testUtils.TestSuite
import uk.gov.hmrc.http.HeaderCarrier
import utils.TaxYear

import scala.concurrent.Future

class GetCalculationDetailsServiceSpec extends TestSuite {

  val mockSingleCalculationConnectorLegacy: CalculationDetailsConnectorLegacy = mock[CalculationDetailsConnectorLegacy]
  val mockSingleCalculationConnector: CalculationDetailsConnector = mock[CalculationDetailsConnector]
  val mockListCalculationConnector: GetCalculationListConnector = mock[GetCalculationListConnector]
  val mockHipCalculationListConnectorLegacy: HipCalculationLegacyListConnector = mock[HipCalculationLegacyListConnector]
  val mockHipCalculationDetailsConnector: HipGetCalculationsDataConnector = mock[HipGetCalculationsDataConnector]
  val mockHipCalculationListConnector: HipGetCalculationListConnector = mock[HipGetCalculationListConnector]

  def service(appConfig: AppConfig = mockAppConfig) = new GetCalculationDetailsService(mockSingleCalculationConnectorLegacy, mockSingleCalculationConnector,
    mockListCalculationConnector, mockHipCalculationListConnectorLegacy,
    mockHipCalculationDetailsConnector, mockHipCalculationListConnector, appConfig)

  val nino = "AA123456A"
  val taxYear: Option[String] = Some("2022")
  val taxYear2016: Int = 2016
  val specificTaxYear: Option[String] = Some(TaxYear.taxYear2024.toString)
  val specificTaxYearPlusOne: Option[String] = Some((TaxYear.taxYear2024 + 1).toString)
  val taxYear2026: Option[String] = Some((TaxYear.taxYear2026).toString)
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

  def listCalculationDetailsSuccess2083: CallHandler3[String, String, HeaderCarrier, Future[GetCalculationListResponse]] =
    (mockListCalculationConnector.getCalculationList2083(_: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(
        Future.successful(
          Right(Seq(GetCalculationListModel(
            calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
            calculationTimestamp = "2019-03-17T09:22:59Z",
            calculationType = "inYear",
            requestedBy = Some("customer"),
            fromDate = Some("2013-05-d1"),
            toDate = Some("2016-05-d1")
          )))
        )
      )

  def listCalculationDetailsSuccess2150: CallHandler3[String, String, HeaderCarrier, Future[GetCalculationListResponse]] =
    (mockListCalculationConnector.getCalculationList2150(_: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(
        Future.successful(
          Right(Seq(GetCalculationListModel(
            calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
            calculationTimestamp = "2019-03-17T09:22:59Z",
            calculationType = "AM",
            requestedBy = Some("customer"),
            fromDate = Some("2013-05-d1"),
            toDate = Some("2016-05-d1"),
            calculationOutcome = Some("PROCESSED")
          ),
            GetCalculationListModel(
              calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1e",
              calculationTimestamp = "2019-02-17T09:22:59Z",
              calculationType = "DF",
              requestedBy = Some("customer"),
              fromDate = Some("2013-05-d1"),
              toDate = Some("2016-05-d1"),
              calculationOutcome = Some("ERROR")
            ),
            GetCalculationListModel(
              calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1f",
              calculationTimestamp = "2019-04-17T09:22:59Z",
              calculationType = "AM",
              requestedBy = Some("customer"),
              fromDate = Some("2013-05-d1"),
              toDate = Some("2016-05-d1"),
              calculationOutcome = Some("REJECTED")
            ),
            GetCalculationListModel(
              calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1b",
              calculationTimestamp = "2019-03-17T09:22:59Z",
              calculationType = "DF",
              requestedBy = Some("customer"),
              fromDate = Some("2013-05-d1"),
              toDate = Some("2016-05-d1"),
              calculationOutcome = Some("PROCESSED")
            )))
        )
      )

  def listCalculationDetailsSuccess2150ErrorAndRejected: CallHandler3[String, String, HeaderCarrier, Future[GetCalculationListResponse]] =
    (mockListCalculationConnector.getCalculationList2150(_: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(
        Future.successful(
          Right(Seq(GetCalculationListModel(
            calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
            calculationTimestamp = "2019-03-17T09:22:59Z",
            calculationType = "AM",
            requestedBy = Some("customer"),
            fromDate = Some("2013-05-d1"),
            toDate = Some("2016-05-d1"),
            calculationOutcome = Some("REJECTED")
          ),
            GetCalculationListModel(
              calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1b",
              calculationTimestamp = "2019-03-17T09:22:59Z",
              calculationType = "DF",
              requestedBy = Some("customer"),
              fromDate = Some("2013-05-d1"),
              toDate = Some("2016-05-d1"),
              calculationOutcome = Some("ERROR")
            ),
          GetCalculationListModel(
            calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1a",
            calculationTimestamp = "2019-03-17T09:22:59Z",
            calculationType = "DF",
            requestedBy = Some("customer"),
            fromDate = Some("2013-05-d1"),
            toDate = Some("2016-05-d1"),
            calculationOutcome = None
          )))
        )
      )

  def listCalculationDetailsEmpty2150: CallHandler3[String, String, HeaderCarrier, Future[GetCalculationListResponse]] =
    (mockListCalculationConnector.getCalculationList2150(_: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(
        Future.successful(
          Right(Seq(GetCalculationListModel(
            calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
            calculationTimestamp = "2019-03-17T09:22:59Z",
            calculationType = "IY",
            requestedBy = Some("customer"),
            fromDate = Some("2013-05-d1"),
            toDate = Some("2016-05-d1")
          )))
        )
      )

  def listCalculationDetailsSuccess5624: CallHandler3[String, String, HeaderCarrier, Future[GetCalculationListResponse]] =
    (mockHipCalculationListConnector.getCalculationList5624(_: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(
        Future.successful(
          Right(Seq(GetCalculationListModel(
            calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
            calculationTimestamp = "2019-03-17T09:22:59Z",
            calculationType = "inYear",
            requestedBy = Some("customer"),
            fromDate = Some("2013-05-d1"),
            toDate = Some("2016-05-d1")
          )))
        )
      )

  def listCalculationDetailsSuccessLegacy: CallHandler3[String, Option[String], HeaderCarrier, Future[GetCalculationListResponseLegacy]] =
    (mockHipCalculationListConnectorLegacy.calcList(_: String, _: Option[String])(_: HeaderCarrier))
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
    (mockHipCalculationListConnectorLegacy.calcList(_: String, _: Option[String])(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(Future.successful(Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("error", "error")))))

  def emptyListCalculationDetailsFailure: CallHandler3[String, Option[String], HeaderCarrier, Future[GetCalculationListResponseLegacy]] =
    (mockHipCalculationListConnectorLegacy.calcList(_: String, _: Option[String])(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(Future.successful(Right(Seq.empty[GetCalculationListModelLegacy])))

  def setHipEnabledFeatureSwitchConfig(): MockAppConfig = {
    new MockAppConfig {
      override val useGetCalcListHip5624: Boolean = true
    }
  }

  ".getCalculationDetails" should {

    "return a Right when successful" in {
      getCalculationDetailsSuccessLegacy

      listCalculationDetailsSuccessLegacy

      val result = await(service().getCalculationDetails(nino, taxYear, None))

      result mustBe Right(Json.toJson(successModelFull))
    }

    "return a Right when successful for specific tax year before 25-26 with hip disabled and no calculationRecord" in {
      getCalculationDetailsSuccess

      listCalculationDetailsSuccess2150

      val result = await(service().getCalculationDetails(nino, specificTaxYear, None))

      result mustBe Right(Json.toJson(successModelFull))
    }

    "return a Right when successful for specific tax year before 25-26 with hip disabled and a latest calculationRecord" in {
      getCalculationDetailsSuccess

      listCalculationDetailsSuccess2150

      val result = await(service().getCalculationDetails(nino, specificTaxYear, Some("LATEST")))

      result mustBe Right(Json.toJson(successModelFull))
    }

    "return a Right when successful for specific tax year before 25-26 with hip disabled and a previous calculationRecord" in {
      getCalculationDetailsSuccess

      listCalculationDetailsSuccess2150

      val result = await(service().getCalculationDetails(nino, specificTaxYear, Some("PREVIOUS")))

      result mustBe Right(Json.toJson(successModelFull))
    }

    "return a Right when successful for specific tax year before 25-26 with hip enabled and no calculationRecord" in {
      getCalculationDetailsSuccess

      listCalculationDetailsSuccess5624

      val result = await(service(setHipEnabledFeatureSwitchConfig()).getCalculationDetails(nino, specificTaxYear, None))

      result mustBe Right(Json.toJson(successModelFull))
    }

    "return a Right when successful for specific tax year for 25-26 onwards and no calculationRecord" in {
      getCalculationDetailsSuccess

      listCalculationDetailsSuccess2083

      val result = await(service().getCalculationDetails(nino, taxYear2026, None))

      result mustBe Right(Json.toJson(successModelFull))
    }

    "return a Left(DesError) when calling list calculations returns an empty calculation and no calcType" in {

      emptyListCalculationDetailsFailure

      val result = await(service().getCalculationDetails(nino, taxYear, None))

      result mustBe Left(ErrorModel(NO_CONTENT, ErrorBodyModel("PARSING_ERROR", "Error parsing response from API")))
    }

    "return a Left(DesError) when calling list calculations and not call get calculations" in {

      listCalculationDetailsFailure

      val result = await(service().getCalculationDetails(nino, taxYear, None))

      result mustBe Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("error", "error")))
    }

    "return a Left(DesError) when calling list calculations succeeds however calling get calculations returns a DES error" in {

      listCalculationDetailsSuccessLegacy
      getCalculationDetailsFailureLegacy

      val result = await(service().getCalculationDetails(nino, taxYear, None))

      result mustBe Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("error", "error")))
    }

    "return a Left(DesError) when calling list calculations returns an empty 2nd calculation and PREVIOUS calculationRecord" in {

      listCalculationDetailsEmpty2150

      val result = await(service().getCalculationDetails(nino, specificTaxYear, Some("PREVIOUS")))

      result mustBe Left(ErrorModel(NO_CONTENT, ErrorBodyModel("NOT_FOUND", "Resource not found from API")))
    }

    "return a Left(DesError) when calling list calculations and it returns only errors and rejected outcomes" in {
      listCalculationDetailsSuccess2150ErrorAndRejected

      val result = await(service().getCalculationDetails(nino, specificTaxYear, Some("PREVIOUS")))

      result mustBe Left(ErrorModel(NO_CONTENT, ErrorBodyModel("NOT_FOUND", "Resource not found from API")))
    }

  }

  ".getCalculationDetailsByCalcId" should {

    "return a Right when there is no tax year given" in {
      getCalculationDetailsSuccessLegacy

      val result = await(service().getCalculationDetailsByCalcId(nino, calculationId, None))

      result mustBe Right(Json.toJson(successModelFull))
    }

    "return a Right when successful for specific tax year" in {
      getCalculationDetailsSuccess

      val result = await(service().getCalculationDetailsByCalcId(nino, calculationId, specificTaxYear))

      result mustBe Right(Json.toJson(successModelFull))
    }

    "return a Right when successful for specific tax year plus one" in {
      getCalculationDetailsSuccess

      val result = await(service().getCalculationDetailsByCalcId(nino, calculationId, specificTaxYearPlusOne))

      result mustBe Right(Json.toJson(successModelFull))
    }

    "return a Right when successful and tax year is 22-23 or prior" in {
      getCalculationDetailsSuccessLegacy

      val result = await(service().getCalculationDetailsByCalcId(nino, calculationId, taxYear))

      result mustBe Right(Json.toJson(successModelFull))
    }

    "return a Left(DesError) when calling get calculations returns a DES error" in {
      getCalculationDetailsFailureLegacy

      val result = await(service().getCalculationDetailsByCalcId(nino, calculationId, taxYear))

      result mustBe Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("error", "error")))
    }
  }
}