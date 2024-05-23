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
import play.api.Logging
import play.api.http.Status.NOT_FOUND
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.HttpReads.Implicits._

class GetCalculationListConnectorLegacy @Inject()(http: HttpClient, val appConfig: AppConfig)(implicit ec: ExecutionContext) extends DesConnector with Logging{

  def calcList(nino: String, taxYear: Option[String])(implicit hc: HeaderCarrier): Future[GetCalculationListResponseLegacy] = {

    val getCalcListUrl: String =
      s"${appConfig.desBaseUrl}/income-tax/list-of-calculation-results/$nino${taxYear.fold("")(year => s"?taxYear=$year")}"

    def desCall(implicit hc: HeaderCarrier): Future[HttpResponse] = {
//      http.GET(url = getCalcListUrl)(GetCalculationListHttpReadsLegacy, hc, ec)
      http.GET[HttpResponse](url = getCalcListUrl)
    }


    val delayInMs = 1000
    def desCallWithRetry(nino: String, taxYear: Option[String], retries: Int = 0)
                       (implicit hc: HeaderCarrier): Future[GetCalculationListResponseLegacy] = {
      desCall.flatMap {
        response =>
          response.status match {
            case NOT_FOUND if (retries < 8) =>
              logger.error(s"[GetCalculationListConnectorLegacy][calcList] - calculation not available - retrying ...: -${response.body}-")
              Thread.sleep(delayInMs)
              desCallWithRetry(nino, taxYear, retries + 1)

            case _ =>
              logger.info(s"[GetCalculationListConnectorLegacy][calcList] - Response: -${response.body}-")
              Future.successful(GetCalculationListHttpReadsLegacy.read("GET", getCalcListUrl, response))
          }
      }
    }

    desCallWithRetry(nino,taxYear)(desHeaderCarrier(getCalcListUrl))
  }

}
