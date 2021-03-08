/*
 * Copyright 2021 HM Revenue & Customs
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
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.logging.Authorization

class DesConnectorSpec extends TestSuite{

  class FakeConnector(override val appConfig: AppConfig) extends DesConnector {
    def headerCarrierTest(hc: HeaderCarrier): HeaderCarrier = desHeaderCarrier(hc)
  }
  val connector = new FakeConnector(appConfig = mockAppConfig)

  "FakeConnector" should {
    "add the correct authorization" in {
      val hc = HeaderCarrier()
      val result = connector.headerCarrierTest(hc)
      result.authorization mustBe Some(Authorization(s"Bearer ${mockAppConfig.authorisationToken}"))
    }
    "add the correct environment" in {
      val hc = HeaderCarrier()
      val result = connector.headerCarrierTest(hc)
      result.extraHeaders mustBe List("Environment" -> mockAppConfig.environment)
    }
  }

}
