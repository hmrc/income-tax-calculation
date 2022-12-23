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

package connectors

import config.AppConfig
import connectors.httpParsers.PostCalculateIncomeTaxLiabilityHttpParser._
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import utils.TaxYear.convertSpecificTaxYear

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PostCalculateIncomeTaxLiabilityConnector @Inject()(http: HttpClient, val appConfig: AppConfig)(implicit ec: ExecutionContext) extends IFConnector {
  val PostCalculateIncomeTaxLiability = "1897"

  def calculateLiability(nino: String, taxYear: String, crystallise: Boolean)(implicit hc: HeaderCarrier): Future[PostCalculateIncomeTaxLiabilityResponse] = {
    val taxYearParameter = convertSpecificTaxYear(taxYear)
    val liabilityIfCalculationUrl: String = appConfig.ifBaseUrl +
      s"/income-tax/calculation/$taxYearParameter/$nino" + (if (crystallise) "?crystallise=true" else "")

    def ifCall(implicit hc: HeaderCarrier): Future[PostCalculateIncomeTaxLiabilityResponse] = {
      http.POST[JsValue, PostCalculateIncomeTaxLiabilityResponse](liabilityIfCalculationUrl, Json.parse("""{}"""))
    }

    ifCall(iFHeaderCarrier(liabilityIfCalculationUrl, PostCalculateIncomeTaxLiability))
  }
}
