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
import connectors.httpParsers.GetCalculationListHttpParserLegacy._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetCalculationListConnectorLegacy @Inject()
(http: HttpClientV2, val appConfig: AppConfig)(implicit ec: ExecutionContext) extends DesConnector with IFConnector {

  def calcList(nino: String, taxYear: Option[String])(implicit hc: HeaderCarrier): Future[GetCalculationListResponseLegacy] = {

    val getCalcListUrl: String = {
      val platformURL = if (appConfig.useGetCalcListIFPlatform) appConfig.ifBaseUrl else appConfig.desBaseUrl
      s"$platformURL/income-tax/list-of-calculation-results/$nino${taxYear.fold("")(year => s"?taxYear=$year")}"
    }

    def getCall(implicit hc: HeaderCarrier): Future[GetCalculationListResponseLegacy] = {
      http.get(url"$getCalcListUrl")
        .execute[GetCalculationListResponseLegacy]
    }

    if (appConfig.useGetCalcListIFPlatform) {
      getCall(iFHeaderCarrier(getCalcListUrl, "1404"))
    } else {
      getCall(desHeaderCarrier(getCalcListUrl))
    }
  }
}
