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
import connectors.httpParsers.GetCalculationListHttpParserLegacy._
import org.apache.pekko.http.scaladsl.model.headers.Authorization
import play.api.Logging
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HipCalculationLegacyListConnector @Inject()
(http: HttpClientV2, val appConfig: AppConfig)(implicit ec: ExecutionContext) extends HipConnector with Logging {

  def calcList(nino: String, taxYear: Option[String])(implicit hc: HeaderCarrier): Future[GetCalculationListResponseLegacy] = {

    val endpointUrl: String =
      s"${appConfig.hipBaseUrl}/itsd/calculations/liability/$nino${taxYear.fold("")(year => s"?taxYear=$year")}"

    logger.debug(s"[HipCalculationLegacyListConnector][calcList] - URL: ${endpointUrl}")

    val correlationId = CorrelationId.fromHeaderCarrier(hc)
      .getOrElse(CorrelationId())
    http
      .get(url"$endpointUrl")
      .setHeader(correlationId.asHeader())
      .setHeader(("Authorization", getBasicAuthValue("1404")) )
      .execute[GetCalculationListResponseLegacy]
  }
}
