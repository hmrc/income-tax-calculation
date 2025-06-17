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
import connectors.httpParsers.GetCalculationListHttpParser.{GetCalculationListHttpReads, GetCalculationListResponse}
import play.api.Logging
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import utils.TaxYear

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetCalculationListConnector @Inject()(httpClient: HttpClientV2,
                                            val appConfig: AppConfig)
                                           (implicit ec: ExecutionContext) extends IFConnector with Logging {

  import uk.gov.hmrc.http.HttpReads.Implicits._

  def getCalculationList(nino: String, taxYear: String)(implicit hc: HeaderCarrier): Future[GetCalculationListResponse] = {
    if(taxYear.toInt >= TaxYear.taxYear2026) {
      getCalculationList2083(nino, taxYear)
    } else {
      getCalculationList1896(nino, taxYear)
    }
  }

  private def getCalculationList1896(nino: String, taxYear: String)(implicit hc: HeaderCarrier): Future[GetCalculationListResponse] = {
    val taxYearRange = s"${taxYear.takeRight(2).toInt - 1}-${taxYear.takeRight(2)}"
    val getCalculationListUrl: String = appConfig.ifBaseUrl + s"/income-tax/view/calculations/liability/$taxYearRange/$nino"
    iFCall(getCalculationListUrl)(iFHeaderCarrier(getCalculationListUrl, "1896"))
  }

  private def getCalculationList2083(nino: String, taxYear: String)(implicit hc: HeaderCarrier): Future[GetCalculationListResponse] = {
    val taxYearRange = s"${taxYear.takeRight(2).toInt - 1}-${taxYear.takeRight(2)}"
    val getCalculationListUrl: String = appConfig.ifBaseUrl + s"/income-tax/$taxYearRange/view/$nino/calculations-summary"
    iFCall(getCalculationListUrl)(iFHeaderCarrier(getCalculationListUrl, "2083"))
  }

  def iFCall(urlString: String)(implicit hc: HeaderCarrier): Future[GetCalculationListResponse] = {
//    httpClient.GET[HttpResponse](url = urlString)(HttpReads[HttpResponse], hc, ec).map {
//      response =>
//        logger.info(s"[getCalculationList][getCalculationList] - Response: -${response.body}-")
//        GetCalculationListHttpReads.read("GET", urlString, response)
//    }
    httpClient.get(url"$urlString")
      .execute[HttpResponse]
      .map {response =>
        logger.info(s"[getCalculationList][getCalculationList] - Response: -${response.body}-")
        GetCalculationListHttpReads.read("GET", urlString, response)
      }
  }
}
