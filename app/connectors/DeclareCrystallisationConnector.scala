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
import connectors.httpParsers.DeclareCrystallisationHttpParser._
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import play.api.libs.ws.writeableOf_JsValue
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeclareCrystallisationConnector @Inject()(httpClient: HttpClientV2,
                                                val appConfig: AppConfig)
                                               (implicit ec: ExecutionContext) extends IFConnector {

  private def toTaxYearParam(taxYear: Int): String = {
    s"${(taxYear - 1).toString takeRight 2}-${taxYear.toString takeRight 2}"
  }

  def declareCrystallisation(nino: String, taxYear: Int, calculationId: String)(implicit hc: HeaderCarrier): Future[DeclareCrystallisationResponse] ={
    val declareCrystallisationUrl: String = appConfig.ifBaseUrl +
      s"/income-tax/${toTaxYearParam(taxYear)}/calculation/$nino/$calculationId/crystallise"

    def iFCall(implicit hc: HeaderCarrier): Future[DeclareCrystallisationResponse] = {
      httpClient.post(url"$declareCrystallisationUrl")
        .withBody(Json.parse("""{}"""))
        .execute[DeclareCrystallisationResponse]
    }

    iFCall(iFHeaderCarrier(declareCrystallisationUrl, "1780"))
  }


}
