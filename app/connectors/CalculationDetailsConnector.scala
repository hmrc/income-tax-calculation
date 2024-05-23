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
import connectors.httpParsers.CalculationDetailsHttpParser.{CalculationDetailResponse, CalculationDetailsHttpReads}
import play.api.Logging
import play.api.http.Status.NOT_FOUND
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CalculationDetailsConnector @Inject()(httpClient: HttpClient,
                                            val appConfig: AppConfig)
                                           (implicit ec: ExecutionContext) extends IFConnector with Logging {
  import uk.gov.hmrc.http.HttpReads.Implicits._

  def getCalculationDetails(taxYear: String, nino: String, calculationId: String)(implicit hc: HeaderCarrier): Future[CalculationDetailResponse]  = {
    val getCalculationDetailsUrl: String = appConfig.ifBaseUrl + s"/income-tax/view/calculations/liability/$taxYear/$nino/$calculationId"

    def iFCall(implicit hc: HeaderCarrier): Future[HttpResponse] = {
      val urlString = getCalculationDetailsUrl
      logger.info(s"[CalculationDetailsConnector][getCalculationDetails] - GET URL: -${urlString}-")
      httpClient.GET[HttpResponse](url = getCalculationDetailsUrl)(HttpReads[HttpResponse], hc, ec)
    }

    val delayInMs = 1000

    def iFCallWithRetry(taxYear: String,nino: String, retries: Int = 0)
                        (implicit hc: HeaderCarrier): Future[CalculationDetailResponse] = {
      iFCall.flatMap {
        response =>
          response.status match {
            case NOT_FOUND if (retries < 8) =>
              logger.error(s"[CalculationDetailsConnector][iFCallWithRetry] - calculation not available - retrying ...: -${response.body}-")
              Thread.sleep(delayInMs)
              iFCallWithRetry(taxYear,nino, retries + 1)

            case _ =>
              logger.info(s"[CalculationDetailsConnector][iFCallWithRetry] - Response: -${response.body}-")
              Future.successful(CalculationDetailsHttpReads.read("GET", getCalculationDetailsUrl, response))
          }
      }
    }

    iFCallWithRetry(nino,taxYear)(iFHeaderCarrier(getCalculationDetailsUrl, "1885"))
  }





}