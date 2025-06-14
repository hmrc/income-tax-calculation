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
import connectors.{LiabilityCalculationConnector, PostCalculateIncomeTaxLiabilityConnector}
import uk.gov.hmrc.http.HeaderCarrier
import utils.TaxYear

import javax.inject.Inject
import scala.concurrent.Future

class LiabilityCalculationService @Inject()(liabilityCalculationConnector: LiabilityCalculationConnector,
                                            postCalculateIncomeTaxLiabilityConnector: PostCalculateIncomeTaxLiabilityConnector) {

  def calculateLiability(nino: String, taxYear: String, crystallise: Boolean)
                        (implicit hc: HeaderCarrier): Future[LiabilityCalculationResponse] = {
    if (taxYear.toInt >= TaxYear.taxYear2024) {
      postCalculateIncomeTaxLiabilityConnector.calculateLiability(nino, taxYear, crystallise)
    }
    else {
      liabilityCalculationConnector.calculateLiability(nino, taxYear, crystallise)
    }
  }

}
