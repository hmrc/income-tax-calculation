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

import connectors.hip.HipGetTaxLiabilitySummaryDataSA302Connector
import models.ErrorModel
import models.hip.calculation.taxLiabilitySummaryDataSA302.{
  TaxLiabilitySummaryDataSA302Error, TaxLiabilitySummaryDataSA302Model, TaxLiabilitySummaryDataSA302ResponseModel
}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetTaxLiabilitySummaryDataSA302Service @Inject()
                        (hipGetTaxLiabilitySummaryDataSA302Connector: HipGetTaxLiabilitySummaryDataSA302Connector)(implicit ec: ExecutionContext) {

  def getTaxLiabilitySummaryDataSA302Details(
                                              taxYear: String,
                                              taxableEntityId: String,
                                              calculationId: String)
                                            (implicit hc: HeaderCarrier): Future[Either[ErrorModel, TaxLiabilitySummaryDataSA302ResponseModel]] = {

    hipGetTaxLiabilitySummaryDataSA302Connector.getGetTaxLiabilitySummaryDataSA302(taxYear, taxableEntityId, calculationId).map {
      case Left(error) => Left(error)

      case Right(model: TaxLiabilitySummaryDataSA302Model) =>
        Right(model)

      case Right(error: TaxLiabilitySummaryDataSA302Error) =>
        Right(TaxLiabilitySummaryDataSA302Error(status = error.status, message = error.message))
    }
  }
}
