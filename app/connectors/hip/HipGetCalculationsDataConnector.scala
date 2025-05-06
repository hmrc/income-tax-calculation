/*
 * Copyright 2025 HM Revenue & Customs
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
import connectors.httpParsers.hip.GetCalculationDetailsHttpParser._
import play.api.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HipGetCalculationsDataConnector @Inject()(http: HttpClientV2,
                                                val appConfig: AppConfig)
                                               (implicit ec: ExecutionContext) extends HipConnector with Logging {

  def getCalculationsData(taxYear: String, nino: String, calculationId: String)(implicit hc: HeaderCarrier): Future[GetCalculationDetailsResponse] = {
    // need to add the correlationId in as a header (no other headers needed)
    // passing through the other parameters in the path

    val url = s"${appConfig.hipBaseUrl}/income-tax/v1/$taxYear/view/calculations/liability/$nino/$calculationId"

    val correlationId = CorrelationId.fromHeaderCarrier(hc).getOrElse(CorrelationId())
    val hipHeaders = Seq((HeaderNames.authorisation, getBasicAuthValue("1885")), correlationId.asHeader())

    logger.debug(s"[HipCalculationLegacyListConnector][calcList] - URL: ${url} - ${hipHeaders} ")

    http
      .get(url"$url")
      .setHeader(hipHeaders: _*)
      .execute[GetCalculationDetailsResponse]
  }

}
