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

package connectors

import config.AppConfig
import connectors.httpParsers.LiabilityCalculationHttpParser._
import javax.inject.Inject
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import scala.concurrent.{ExecutionContext, Future}

class LiabilityCalculationConnector @Inject()(http: HttpClient, val appConfig: AppConfig)(implicit ec: ExecutionContext) extends DesConnector {

  def calculateLiability(nino: String, taxYear: String, crystallise: Boolean)(implicit hc: HeaderCarrier): Future[LiabilityCalculationResponse] = {
    val liabilityCalculationUrl: String = appConfig.desBaseUrl +
      s"/income-tax/nino/$nino/taxYear/$taxYear/tax-calculation?crystallise=$crystallise"

    def desCall(implicit hc: HeaderCarrier): Future[LiabilityCalculationResponse] = {
      http.POST[JsValue,LiabilityCalculationResponse](liabilityCalculationUrl, Json.parse("""{}"""))
    }

    desCall(desHeaderCarrier(liabilityCalculationUrl))
  }
}
