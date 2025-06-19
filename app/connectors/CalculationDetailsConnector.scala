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
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CalculationDetailsConnector @Inject()(httpClient: HttpClientV2,
                                            val appConfig: AppConfig)
                                           (implicit ec: ExecutionContext) extends IFConnector with Logging {

  def getCalculationDetails(taxYear: String, nino: String, calculationId: String)(implicit hc: HeaderCarrier): Future[CalculationDetailResponse]  = {
    val getCalculationDetailsUrl: String = appConfig.ifBaseUrl + s"/income-tax/view/calculations/liability/$taxYear/$nino/$calculationId"

    def iFCall(implicit hc: HeaderCarrier): Future[CalculationDetailResponse] = {
      val urlString = getCalculationDetailsUrl
      logger.info(s"[CalculationDetailsConnector][getCalculationDetails] - GET URL: -${urlString}-")
      httpClient
        .get(url"$getCalculationDetailsUrl")
        .execute[CalculationDetailResponse]
    }

    iFCall(iFHeaderCarrier(getCalculationDetailsUrl, "1885"))
  }
}