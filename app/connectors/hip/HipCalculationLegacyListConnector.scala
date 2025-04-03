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

package connectors.hip

import config.AppConfig
import connectors.httpParsers.GetCalculationListHttpParserLegacy._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HipCalculationLegacyListConnector @Inject()
(http: HttpClient, val appConfig: AppConfig)(implicit ec: ExecutionContext) extends HipConnector {

  def calcList(nino: String, taxYear: Option[String])(implicit hc: HeaderCarrier): Future[GetCalculationListResponseLegacy] = {

    val getCalcListUrl: String = {
      s"${appConfig.hipBaseUrl}/income-tax/list-of-calculation-results/$nino${taxYear.fold("")(year => s"?taxYear=$year")}"
    }

    def getCall(implicit hc: HeaderCarrier): Future[GetCalculationListResponseLegacy] = {
      http.GET(url = getCalcListUrl)(GetCalculationListHttpReadsLegacy, hc, ec)
    }

    getCall(hipHeaderCarrier("1404"))
  }
}
