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

import connectors.httpParsers.LiabilityCalculationHttpParser.LiabilityCalculationResponse
import connectors.httpParsers.PostCalculateIncomeTaxLiabilityHttpParser.PostCalculateIncomeTaxLiabilityResponse
import connectors.{LiabilityCalculationConnector, PostCalculateIncomeTaxLiabilityConnector}
import models.{ErrorBodyModel, ErrorModel, LiabilityCalculationIdModel}
import org.scalamock.handlers.CallHandler4
import play.api.http.Status.INTERNAL_SERVER_ERROR
import testUtils.TestSuite
import uk.gov.hmrc.http.HeaderCarrier
import utils.TaxYear

import scala.concurrent.Future

class LiabilityCalculationServiceSpec extends TestSuite {

  val specificTaxYear: String = TaxYear.specificTaxYear.toString
  val specificTaxYearPlusOne: String = (TaxYear.specificTaxYear + 1).toString
  val mockConnector: LiabilityCalculationConnector = mock[LiabilityCalculationConnector]
  val mockIfConnector: PostCalculateIncomeTaxLiabilityConnector = mock[PostCalculateIncomeTaxLiabilityConnector]
  val service = new LiabilityCalculationService(mockConnector, mockIfConnector)

  def liabilityCalculationConnectorMockSuccess: CallHandler4[String, String, Boolean, HeaderCarrier, Future[LiabilityCalculationResponse]] =
    (mockConnector.calculateLiability(_: String, _: String, _: Boolean)(_: HeaderCarrier))
      .expects(*, *, *, *)
      .returning(Future.successful(Right(LiabilityCalculationIdModel("id"))))

  def liabilityCalculationConnectorMockFailure: CallHandler4[String, String, Boolean, HeaderCarrier, Future[LiabilityCalculationResponse]] =
    (mockConnector.calculateLiability(_: String, _: String, _: Boolean)(_: HeaderCarrier))
      .expects(*, *, *, *)
      .returning(Future.successful(Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("error", "error")))))

  def postCalculateIncomeTaxLiabilityConnectorMockSuccess: CallHandler4[String, String, Boolean, HeaderCarrier,
    Future[PostCalculateIncomeTaxLiabilityResponse]] =
    (mockIfConnector.calculateLiability(_: String, _: String, _: Boolean)(_: HeaderCarrier))
      .expects(*, *, *, *)
      .returning(Future.successful(Right(LiabilityCalculationIdModel("id"))))

  def postCalculateIncomeTaxLiabilityConnectorMockFailure: CallHandler4[String, String, Boolean, HeaderCarrier,
    Future[PostCalculateIncomeTaxLiabilityResponse]] =
    (mockIfConnector.calculateLiability(_: String, _: String, _: Boolean)(_: HeaderCarrier))
      .expects(*, *, *, *)
      .returning(Future.successful(Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("error", "error")))))

  ".calculateLiability" should {

    "return a Right(LiabilityCalculationIdModel) " in {

      liabilityCalculationConnectorMockSuccess

      val result = await(service.calculateLiability("nino", "2018", crystallise = false))

      result mustBe Right(LiabilityCalculationIdModel("id"))
    }

    "return a Right(LiabilityCalculationIdModel) with a crystallisation flag" in {

      liabilityCalculationConnectorMockSuccess

      val result = await(service.calculateLiability("nino", "2018", crystallise = true))

      result mustBe Right(LiabilityCalculationIdModel("id"))
    }

    "return a Left(DesError)" in {

      liabilityCalculationConnectorMockFailure

      val result = await(service.calculateLiability("nino", "2018", crystallise = false))

      result mustBe Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("error", "error")))
    }
  }

  ".calculateLiability if connector for specific tax year" should {

    "return a Right(LiabilityIfCalculationIdModel) " in {

      postCalculateIncomeTaxLiabilityConnectorMockSuccess

      val result = await(service.calculateLiability("nino", specificTaxYear, crystallise = false))

      result mustBe Right(LiabilityCalculationIdModel("id"))
    }

    "return a Right(LiabilityIfCalculationIdModel) with a crystallisation flag" in {

      postCalculateIncomeTaxLiabilityConnectorMockSuccess

      val result = await(service.calculateLiability("nino", specificTaxYear, crystallise = true))

      result mustBe Right(LiabilityCalculationIdModel("id"))
    }

    "return a Left(IfError)" in {

      postCalculateIncomeTaxLiabilityConnectorMockFailure

      val result = await(service.calculateLiability("nino", specificTaxYear, crystallise = false))

      result mustBe Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("error", "error")))
    }
  }

  ".calculateLiability if connector for specific tax year plus one" should {

    "return a Right(LiabilityIfCalculationIdModel)" in {

      postCalculateIncomeTaxLiabilityConnectorMockSuccess

      val result = await(service.calculateLiability("nino", specificTaxYearPlusOne, crystallise = false))

      result mustBe Right(LiabilityCalculationIdModel("id"))
    }

    "return a Right(LiabilityIfCalculationIdModel) with a crystallisation flag" in {

      postCalculateIncomeTaxLiabilityConnectorMockSuccess

      val result = await(service.calculateLiability("nino", specificTaxYearPlusOne, crystallise = true))

      result mustBe Right(LiabilityCalculationIdModel("id"))
    }

    "return a Left(IfError)" in {

      postCalculateIncomeTaxLiabilityConnectorMockFailure

      val result = await(service.calculateLiability("nino", specificTaxYearPlusOne, crystallise = false))

      result mustBe Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("error", "error")))
    }
  }
}
