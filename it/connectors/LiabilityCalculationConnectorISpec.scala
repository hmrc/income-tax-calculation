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

import helpers.WiremockSpec
import models._
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.SERVICE_UNAVAILABLE
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier

class LiabilityCalculationConnectorISpec extends AnyWordSpec with WiremockSpec{

  lazy val connector: LiabilityCalculationConnector = app.injector.instanceOf[LiabilityCalculationConnector]
  implicit val hc = HeaderCarrier()
  val nino = "nino"
  val taxYear = "2021"
  val url = s"/income-tax/nino/$nino/taxYear/$taxYear/tax-calculation"

  "LiabilityCalculationConnector" should {

    "return a success result" when {

      "DES returns a success result with expected JSON" in {
        val response = Json.toJson(LiabilityCalculationIdModel("00000000-0000-1000-8000-000000000000")).toString()

        stubPostWithoutRequestBody(url, 200, response)

        val result = await(connector.calculateLiability(nino, taxYear))

        result mustBe Right(LiabilityCalculationIdModel("00000000-0000-1000-8000-000000000000"))
      }
    }

    "return a failure result" when {

      "DES returns an error" in {
        val response =
          """
            |{
            |  "code": "SERVICE_UNAVAILABLE",
            |  "reason": "Dependent systems are currently not responding."
            |}
            |""".stripMargin
        stubPostWithoutRequestBody(url, 503, response)

        val result = await(connector.calculateLiability(nino, taxYear))

        result mustBe Left(DesErrorModel(SERVICE_UNAVAILABLE, DesErrorBodyModel("SERVICE_UNAVAILABLE", "Dependent systems are currently not responding.")))

      }
    }
  }
}
