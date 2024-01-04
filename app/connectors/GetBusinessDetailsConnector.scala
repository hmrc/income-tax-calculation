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
import connectors.httpParsers.GetBusinessDetailsHttpParser.{GetBusinessDetailsHttpReads, GetBusinessDetailsResponse}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetBusinessDetailsConnector @Inject()(http: HttpClient, val appConfig: AppConfig)(implicit ec: ExecutionContext) extends IFConnector {

  def getBusinessDetails(nino: String)(implicit hc: HeaderCarrier): Future[GetBusinessDetailsResponse] = {

    val getBusinessDetailsUrl: String => String = {
      if (appConfig.useBusinessDetailsStub) {
        nino => s"${appConfig.incomeTaxSubmissionStubUrl}/registration/business-details/nino/$nino"
      }
      else {
        nino => s"${appConfig.ifBaseUrl}/registration/business-details/nino/$nino"
      }
    }

    def ifCall(implicit hc: HeaderCarrier): Future[GetBusinessDetailsResponse] = {
      http.GET(url = getBusinessDetailsUrl(nino))(GetBusinessDetailsHttpReads, hc, ec)
    }

    ifCall(iFHeaderCarrier(getBusinessDetailsUrl(nino), "1171"))
  }
}
