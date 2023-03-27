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

package connectors

import config.AppConfig
import connectors.httpParsers.GetCalculationListHttpParserLegacy._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetCalculationListConnectorLegacy @Inject()(http: HttpClient, val appConfig: AppConfig)(implicit ec: ExecutionContext) extends DesConnector {

  def calcList(nino: String, taxYear: Option[String])(implicit hc: HeaderCarrier): Future[GetCalculationListResponseLegacy] = {

    val localEnv = "http://localhost:9081"

    val stagingEnv = "https://www.staging.tax.service.gov.uk"

    val baseUrl: String = hc.headers(Seq("env")).headOption.collect {
      case (_, env) if env == localEnv || env == stagingEnv => appConfig.ifBaseUrl
    }.getOrElse(appConfig.desBaseUrl)

    val getCalcListUrl: String =
      s"$baseUrl/income-tax/list-of-calculation-results/$nino${taxYear.fold("")(year => s"?taxYear=$year")}"

    def desCall(implicit hc: HeaderCarrier): Future[GetCalculationListResponseLegacy] = {
      http.GET(url = getCalcListUrl)(GetCalculationListHttpReadsLegacy, hc, ec)
    }

    desCall(desHeaderCarrier(getCalcListUrl))
  }
}
