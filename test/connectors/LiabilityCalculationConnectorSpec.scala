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

import models.{DesErrorBodyModel, DesErrorModel, LiabilityCalculationIdModel}
import org.scalatest.concurrent.ScalaFutures
import play.api.http.Status._
import play.api.libs.json.Json
import testUtils.TestSuite
class LiabilityCalculationConnectorSpec extends TestSuite with ScalaFutures {

  private val connector = new LiabilityCalculationConnector(httpClient, mockAppConfig)
  private val nino = "nino"
  private val taxYear = "2017-18"

  "liabilityCalculation" should {

    "return a calculation ID model" when {

      "DES returns a calculation ID" in {

        val response = LiabilityCalculationIdModel("00000000-0000-1000-8000-000000000000")

        mockHttpPost(OK, Json.parse("""{}"""), Right(response))

        val result = connector.calculateLiability(nino, taxYear)
        whenReady(result) { res =>
          res.right.get mustBe response
        }
      }
    }

    "return a DES error model" when {

      "DES returns a 500" in {

        val response = DesErrorModel(INTERNAL_SERVER_ERROR, DesErrorBodyModel("ERROR","error"))

        mockHttpPost(INTERNAL_SERVER_ERROR, Json.parse("""{}"""), Left(response))

        val result = connector.calculateLiability(nino, taxYear)
        whenReady(result) { res =>
          res.left.get mustBe response
        }
      }
    }
  }
}
