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
import play.api.http.Status.NOT_FOUND
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetCalculationListConnector @Inject()(httpClient: HttpClient,
                                            val appConfig: AppConfig)
                                           (implicit ec: ExecutionContext) extends IFConnector with Logging {

  import uk.gov.hmrc.http.HttpReads.Implicits._

  def getCalculationList(nino: String, taxYear: String)(implicit hc: HeaderCarrier): Future[GetCalculationListResponse] = {
    val taxYearRange = s"${taxYear.takeRight(2).toInt - 1}-${taxYear.takeRight(2)}"
    val calculationListUrl: String = appConfig.ifBaseUrl + s"/income-tax/view/calculations/liability/$taxYearRange/$nino"

    def iFCall(implicit hc: HeaderCarrier): Future[HttpResponse] = {
      logger.info(s"[getCalculationList][getCalculationList] - GET URL: -$calculationListUrl-")
      httpClient.GET[HttpResponse](url = calculationListUrl)(HttpReads[HttpResponse], hc, ec)
    }

    def iFCallWithRetry(retries: Int = 0)
                       (implicit hc: HeaderCarrier): Future[GetCalculationListResponse] = {
      iFCall.flatMap {
        response =>
          response.status match {
            case NOT_FOUND if (retries < maxRetries) =>
              Thread.sleep(delayInMs)
              iFCallWithRetry(retries + 1)

            case _ =>
              logger.info(s"[GetCalculationListConnector][iFCallWithRetry] - Response: -${response.status}-")
              Future.successful(GetCalculationListHttpReads.read("GET", calculationListUrl, response))
          }
      }
    }

    iFCallWithRetry()(iFHeaderCarrier(calculationListUrl, "1896"))
  }
}
