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

import cats.data.EitherT
import connectors.hip.HipGetTaxLiabilitySummaryDataSA302Connector
import models.ErrorModel
import models.hip.taxLiabilitySummaryDataSA302.TaxLiabilitySummaryDataSA302Model
import play.api.Logging
import service.CalculationResult
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class GetTaxLiabilitySummaryDataSA302Service @Inject()
(hipGetTaxLiabilitySummaryDataSA302Connector: HipGetTaxLiabilitySummaryDataSA302Connector)(implicit ec: ExecutionContext) extends Logging {

  def getTaxLiabilitySummaryDataSA302Details(
                                              taxYear: String,
                                              taxableEntityId: String,
                                              calculationId: String)
                                            (implicit hc: HeaderCarrier): CalculationResult[TaxLiabilitySummaryDataSA302Model] = EitherT {

    hipGetTaxLiabilitySummaryDataSA302Connector.getGetTaxLiabilitySummaryDataSA302(taxYear, taxableEntityId, calculationId).map {
      case Left(error) =>
        logger.error(s"[GetTaxLiabilitySummaryDataSA302Service][getTaxLiabilitySummaryDataSA302Details] - Error Response: $error")
        Left(error)

      case Right(model: TaxLiabilitySummaryDataSA302Model) =>
        logger.info(s"[hipGetTaxLiabilitySummaryDataSA302Connector][getTaxLiabilitySummaryDataSA302Details] taxYear=$taxYear")
        Right(model)
    }
  }
}
