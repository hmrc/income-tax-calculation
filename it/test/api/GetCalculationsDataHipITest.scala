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

package api

import com.github.tomakehurst.wiremock.http.HttpHeaders
import constants.HipGetCalculationDetailsConstants.successModelJson
import helpers.WiremockSpec
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.time.{Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import play.api.Application
import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder

class GetCalculationsDataHipITest extends AnyWordSpec with WiremockSpec with Matchers with ScalaFutures {

  override implicit val patienceConfig: PatienceConfig = PatienceConfig(Span(5, Seconds))
  private val enableHip: Boolean = true

  override implicit lazy val app: Application = GuiceApplicationBuilder()
    .configure(
      ("feature-switch.useEncryption" -> true) +:
        ("auditing.consumer.baseUri.port" -> wireMockPort) +:
        ("feature-switch.useGetCalcListHIPlatform" -> enableHip) +:
        ("feature-switch.useGetCalcListIFPlatform" -> !enableHip) +:
        servicesToUrlConfig: _*
    )
    .build()

  val taxYear: String = "24-25"
  val nino: String = "AA123456A"
  val calculationId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"

  val url = s"/income-tax/v1/$taxYear/view/calculations/liability/$nino/$calculationId"

  "GetCalculationsData" when {
    "the user is an individual" should {

      "retrieve calculations data for a given tax year, NINO and calculationId" in {

        def headers = new HttpHeaders("authroisaton" -> "correlation")


        authorised()

        stubGetWithResponseBody(url, OK, successModelJson, headers)

        whenReady(buildClient(s"/income-tax/nino/$nino/calc-id/$calculationId/calculation-details"))
      }
    }
  }
}
