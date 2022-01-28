/*
 * Copyright 2022 HM Revenue & Customs
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
import connectors.httpParsers.GetCalculationListHttpParser._
import javax.inject.Inject
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import scala.concurrent.{ExecutionContext, Future}

class GetCalculationListConnector @Inject()(http: HttpClient, val appConfig: AppConfig)(implicit ec: ExecutionContext) extends IFConnector {

  def calcList(nino: String, taxYear: Option[String])(implicit hc: HeaderCarrier): Future[GetCalculationListResponse] = {

    val getCalcListUrl: String =
      s"${appConfig.ifBaseUrl}/income-tax/list-of-calculation-results/$nino${taxYear.fold("")(year => s"?taxYear=$year")}"

    def iFCall(implicit hc: HeaderCarrier): Future[GetCalculationListResponse] = {
      http.GET(url = getCalcListUrl)(GetCalculationListHttpReads, hc, ec)
    }

    iFCall(iFHeaderCarrier(getCalcListUrl, "1404"))
  }
}
