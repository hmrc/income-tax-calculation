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
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient,  HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.HttpReads.Implicits._

class CalculationDetailsConnectorLegacy @Inject()(httpClient: HttpClient,
                                                  val appConfig: AppConfig)
                                                 (implicit ec: ExecutionContext) extends IFConnector with Logging {

  def getCalculationDetails(nino: String, calculationId: String)(implicit hc: HeaderCarrier): Future[CalculationDetailResponse]  = {
    val getCalculationDetailsUrl: String = appConfig.ifBaseUrl + s"/income-tax/view/calculations/liability/$nino/$calculationId"

    def iFCall(implicit hc: HeaderCarrier): Future[HttpResponse] = {
      httpClient.GET[HttpResponse](url = getCalculationDetailsUrl)
    }

    val delayInMs = 1000// 8s

    def iFCallWithRetry(nino: String, calculationId: String, retries: Int = 0)
                       (implicit hc: HeaderCarrier): Future[CalculationDetailResponse] = {
      iFCall.flatMap {
        response =>
          response.status match {
            case NOT_FOUND if (retries < 3) =>
              logger.error(s"[CalculationDetailsConnectorLegacy][iFCallWithRetry] - calculation not available - retrying ...: -${response.body}-")
              Thread.sleep(delayInMs)
              iFCallWithRetry(nino, calculationId, retries + 1)

            case _ =>
              logger.info(s"[CalculationDetailsConnectorLegacy][iFCallWithRetry] - Response: -${response.body}-")
              Future.successful(CalculationDetailsHttpReads.read("GET", getCalculationDetailsUrl, response))
          }
      }
    }
    iFCallWithRetry(nino, calculationId)(iFHeaderCarrier(getCalculationDetailsUrl, "1523"))
  }
}