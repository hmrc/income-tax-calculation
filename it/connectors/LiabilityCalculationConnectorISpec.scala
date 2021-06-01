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

import com.github.tomakehurst.wiremock.http.HttpHeader
import config.AppConfig
import controllers.Assets.OK
import helpers.WiremockSpec
import models._
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Configuration
import play.api.http.Status.SERVICE_UNAVAILABLE
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames, HttpClient, SessionId}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class LiabilityCalculationConnectorISpec extends AnyWordSpec with WiremockSpec with Matchers{

  lazy val connector: LiabilityCalculationConnector = app.injector.instanceOf[LiabilityCalculationConnector]

  lazy val httpClient: HttpClient = app.injector.instanceOf[HttpClient]

  def appConfig(desHost: String): AppConfig = new AppConfig(app.injector.instanceOf[Configuration], app.injector.instanceOf[ServicesConfig]) {
    override val desBaseUrl: String = s"http://$desHost:$wireMockPort"
  }

  implicit val hc: HeaderCarrier = HeaderCarrier()

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

    "include internal headers" when {

      val response = Json.toJson(LiabilityCalculationIdModel("00000000-0000-1000-8000-000000000000")).toString()

      val headersSentToDes = Seq(
        new HttpHeader(HeaderNames.authorisation, "Bearer secret"),
        new HttpHeader(HeaderNames.xSessionId, "sessionIdValue")
      )

      val externalHost = "127.0.0.1"

      "the host for DES is 'Internal'" in {
        implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("sessionIdValue")))
        val expectedResult = LiabilityCalculationIdModel("00000000-0000-1000-8000-000000000000")

        stubPostWithoutRequestBody(url, OK, response, headersSentToDes)

        val result = await(connector.calculateLiability(nino, taxYear)(hc))

        result mustBe Right(expectedResult)
      }

      "the host for DES is 'External'" in {
        implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("sessionIdValue")))
        val connector = new LiabilityCalculationConnector(httpClient, appConfig(externalHost))
        val expectedResult = LiabilityCalculationIdModel("00000000-0000-1000-8000-000000000000")

        stubPostWithoutRequestBody(url, OK, response, headersSentToDes)

        val result = await(connector.calculateLiability(nino, taxYear)(hc))

        result mustBe Right(expectedResult)
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
