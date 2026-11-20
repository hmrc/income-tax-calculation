/*
 * Copyright 2026 HM Revenue & Customs
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
import connectors.httpParsers.hip.HipGetTaxLiabilitySummaryDataSA302DetailsHttpParser.*
import play.api.Logging
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HipGetTaxLiabilitySummaryDataSA302Connector @Inject()(http: HttpClientV2,
                                                            val appConfig: AppConfig)
                                                           (implicit ec: ExecutionContext) extends HipConnector with Logging {

  def getGetTaxLiabilitySummaryDataSA302(taxYear: String, taxableEntityId: String, calculationId: String)
                                        (implicit hc: HeaderCarrier): Future[HipGetTaxLiabilitySummaryDataSA302DetailsResponse] = {

    val url = s"${appConfig.hipBaseUrl}/income-tax/v1/$taxYear/tax-liability-summary/$taxableEntityId/$calculationId"

    val correlationId = CorrelationId.fromHeaderCarrier(hc).getOrElse(CorrelationId())
    val hipHeaders = Seq((HeaderNames.authorisation, getBasicAuthValue("5624")), correlationId.asHeader())

    logger.debug(s"[HipGetTaxLiabilitySummaryDataSA302Connector][getGetTaxLiabilitySummaryDataSA302] - URL: $url - $hipHeaders ")

    http
      .get(url"$url")
      .setHeader(hipHeaders: _*)
      .execute[HipGetTaxLiabilitySummaryDataSA302DetailsResponse]
  }
}
