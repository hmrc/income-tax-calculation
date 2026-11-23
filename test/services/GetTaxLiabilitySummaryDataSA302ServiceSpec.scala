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

package services

import connectors.hip.HipGetTaxLiabilitySummaryDataSA302Connector
import connectors.httpParsers.hip.HipGetTaxLiabilitySummaryDataSA302DetailsHttpParser.HipGetTaxLiabilitySummaryDataSA302DetailsResponse
import models.hip.taxLiabilitySummaryDataSA302.TaxLiabilitySummaryDataSA302Model
import models.{ErrorBodyModel, ErrorModel}
import org.scalamock.handlers.CallHandler4
import play.api.http.Status.INTERNAL_SERVER_ERROR
import testConstants.hip.GetTaxLiabilitySummaryDataSA302TestConstants.successModelFull
import testUtils.TestSuite
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class GetTaxLiabilitySummaryDataSA302ServiceSpec extends TestSuite {

  val mockHipGetTaxLiabilitySummaryDataSA302Connector: HipGetTaxLiabilitySummaryDataSA302Connector = mock[HipGetTaxLiabilitySummaryDataSA302Connector]

  val service =
    new GetTaxLiabilitySummaryDataSA302Service(mockHipGetTaxLiabilitySummaryDataSA302Connector)

  def getTaxLiabilitySummaryDataSA302DetailsSuccess: CallHandler4[String, String, String, HeaderCarrier, Future[HipGetTaxLiabilitySummaryDataSA302DetailsResponse]] =
    (mockHipGetTaxLiabilitySummaryDataSA302Connector.getGetTaxLiabilitySummaryDataSA302(_: String, _: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *, *)
      .returning(Future.successful(Right(successModelFull)))

  def getTaxLiabilitySummaryDataSA302DetailsFailure: CallHandler4[String, String, String, HeaderCarrier, Future[HipGetTaxLiabilitySummaryDataSA302DetailsResponse]] =
    (mockHipGetTaxLiabilitySummaryDataSA302Connector.getGetTaxLiabilitySummaryDataSA302(_: String, _: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *, *)
      .returning(Future.successful(Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("error", "error")))))

  ".getTaxLiabilitySummaryDataSA302Details" should {

    "return a Right when successful" in {
      getTaxLiabilitySummaryDataSA302DetailsSuccess

      val result: Either[ErrorModel, TaxLiabilitySummaryDataSA302Model] =
        await(service.getTaxLiabilitySummaryDataSA302Details("2027", "AB1234568", "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4").value)

      result mustBe Right(successModelFull)
    }

    "return a Left(ErrorModel) when calling get Tax Liability Summary Data SA302 Details returns a HIP error" in {

      getTaxLiabilitySummaryDataSA302DetailsFailure

      val result: Either[ErrorModel, TaxLiabilitySummaryDataSA302Model] =
        await(service.getTaxLiabilitySummaryDataSA302Details("2027", "NOT_EXIST", "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4").value)

      result mustBe Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("error", "error")))
    }
  }
}
