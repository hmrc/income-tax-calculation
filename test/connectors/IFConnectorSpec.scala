/*
 * Copyright 2023 HM Revenue & Customs
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
import testUtils.TestSuite
import uk.gov.hmrc.http.HeaderNames.{authorisation, xRequestChain, xSessionId}
import uk.gov.hmrc.http.{Authorization, HeaderCarrier, SessionId}

class IFConnectorSpec extends TestSuite {

  class FakeConnector(override val appConfig: AppConfig) extends IFConnector {
    def headerCarrierTest(url: String, apiNumber: String)(hc: HeaderCarrier): HeaderCarrier = iFHeaderCarrier(url, apiNumber)(hc)
  }

  val connector = new FakeConnector(appConfig = mockAppConfig)

  "FakeConnector" when {

    "host is Internal" should {
      val internalHost = "http://localhost"
      val apiNumber = "1523"

      "add the correct authorization" in {
        val hc = HeaderCarrier()
        val result = connector.headerCarrierTest(internalHost,apiNumber)(hc)
        result.authorization mustBe Some(Authorization(s"Bearer ${mockAppConfig.authorisationToken}"))
      }
      "add the correct environment" in {
        val hc = HeaderCarrier()
        val result = connector.headerCarrierTest(internalHost,apiNumber)(hc)
        result.extraHeaders mustBe List("Environment" -> mockAppConfig.ifEnvironment)
      }
    }

    "host is External" should {
      val externalHost = "http://127.0.0.1"
      val apiNumber = "1523"

      "include all HeaderCarrier headers in the extraHeaders when the host is external" in {
        val hc = HeaderCarrier(sessionId = Some(SessionId("sessionIdHeaderValue")))
        val result = connector.headerCarrierTest(externalHost,apiNumber)(hc)

        result.extraHeaders.size mustBe 4
        result.extraHeaders.contains(xSessionId -> "sessionIdHeaderValue") mustBe true
        result.extraHeaders.contains(authorisation -> s"Bearer ${mockAppConfig.authorisationToken}") mustBe true
        result.extraHeaders.contains("Environment" -> mockAppConfig.ifEnvironment) mustBe true
        result.extraHeaders.exists(x => x._1.equalsIgnoreCase(xRequestChain)) mustBe true
      }
    }
  }
}
