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
import connectors.core.CorrelationId
import connectors.httpParsers.GetCalculationListHttpParser.{GetCalculationListHttpReads, GetCalculationListResponse}
import play.api.Logging
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HipGetCalculationListConnector @Inject()(httpClient: HttpClientV2,
                                               val appConfig: AppConfig)
                                              (implicit ec: ExecutionContext) extends HipConnector with Logging {

  def getCalculationList5624(nino: String, taxYear: String)(implicit hc: HeaderCarrier): Future[GetCalculationListResponse] = {
    val taxYearRange = s"${taxYear.takeRight(2).toInt - 1}-${taxYear.takeRight(2)}"
    val getCalculationListUrl: String = appConfig.hipBaseUrl + s"/itsa/income-tax/v1/$taxYearRange/view/calculations/liability/$nino"

    val correlationId = CorrelationId.fromHeaderCarrier(hc)
      .getOrElse(CorrelationId())

    val hipHeaders =  Seq(
      (HeaderNames.authorisation, getBasicAuthValue("5624")),
      correlationId.asHeader()
    )

    hipCall(getCalculationListUrl, hipHeaders)
  }

  private def hipCall(urlString: String, headers: Seq[(String, String)])(implicit hc: HeaderCarrier): Future[GetCalculationListResponse] = {
    httpClient.get(url"$urlString")
      .setHeader(headers: _*)
      .execute[GetCalculationListResponse]
  }
}
