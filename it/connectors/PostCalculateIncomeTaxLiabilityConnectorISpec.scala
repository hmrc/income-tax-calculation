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

import com.github.tomakehurst.wiremock.http.HttpHeader
import config.BackendAppConfig
import helpers.WiremockSpec
import models._
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Configuration
import play.api.http.Status.{ACCEPTED, SERVICE_UNAVAILABLE}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames, HttpClient, SessionId}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import utils.TaxYear.convertSpecificTaxYear

class PostCalculateIncomeTaxLiabilityConnectorISpec extends AnyWordSpec with WiremockSpec with Matchers {

  lazy val connector: PostCalculateIncomeTaxLiabilityConnector = app.injector.instanceOf[PostCalculateIncomeTaxLiabilityConnector]

  lazy val httpClient: HttpClient = app.injector.instanceOf[HttpClient]

  def appConfig(ifHost: String): BackendAppConfig = new BackendAppConfig(app.injector.instanceOf[Configuration], app.injector.instanceOf[ServicesConfig]) {
    override val ifBaseUrl: String = s"http://$ifHost:$wireMockPort"
  }

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val nino = "nino"
  val taxYear = "2024"
  val taxYearParameter: String = convertSpecificTaxYear(taxYear)
  val url = s"/income-tax/calculation/$taxYearParameter/$nino"
  val crystalliseUrl = s"/income-tax/calculation/$taxYearParameter/$nino?crystallise=true"


  "PostCalculateIncomeTaxLiabilityConnector" should {

    "return a success result" when {

      "IF returns a success result with expected JSON" in {
        val response = Json.toJson(LiabilityCalculationIdModel("00000000-0000-1000-8000-000000000000")).toString()

        stubPostWithoutRequestBody(url, ACCEPTED, response)

        val result = await(connector.calculateLiability(nino, taxYear, crystallise = false))

        result mustBe Right(LiabilityCalculationIdModel("00000000-0000-1000-8000-000000000000"))
      }

      "IF returns a success result with expected JSON and crystallise flag" in {
        val response = Json.toJson(LiabilityCalculationIdModel("00000000-0000-1000-8000-000000000000")).toString()

        stubPostWithoutRequestBody(crystalliseUrl, ACCEPTED, response)

        val result = await(connector.calculateLiability(nino, taxYear, crystallise = true))

        result mustBe Right(LiabilityCalculationIdModel("00000000-0000-1000-8000-000000000000"))
      }
    }

    "include internal headers" when {

      val response = Json.toJson(LiabilityCalculationIdModel("00000000-0000-1000-8000-000000000000")).toString()

      val headersSentToIF = Seq(
        new HttpHeader(HeaderNames.authorisation, "Bearer secret"),
        new HttpHeader(HeaderNames.xSessionId, "sessionIdValue")
      )

      val externalHost = "127.0.0.1"

      "the host for IF is 'Internal'" in {
        implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("sessionIdValue")))
        val expectedResult = LiabilityCalculationIdModel("00000000-0000-1000-8000-000000000000")

        stubPostWithoutRequestBody(url, ACCEPTED, response, headersSentToIF)

        val result = await(connector.calculateLiability(nino, taxYear, crystallise = false)(hc))

        result mustBe Right(expectedResult)
      }

      "the host for IF is 'External'" in {
        implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("sessionIdValue")))
        val connector = new PostCalculateIncomeTaxLiabilityConnector(httpClient, appConfig(externalHost))
        val expectedResult = LiabilityCalculationIdModel("00000000-0000-1000-8000-000000000000")

        stubPostWithoutRequestBody(url, ACCEPTED, response, headersSentToIF)

        val result = await(connector.calculateLiability(nino, taxYear, crystallise = false)(hc))

        result mustBe Right(expectedResult)
      }


    }

    "return a failure result" when {

      "IF returns an error" in {
        val response =
          """
            |{
            |  "code": "SERVICE_UNAVAILABLE",
            |  "reason": "Dependent systems are currently not responding."
            |}
            |""".stripMargin
        stubPostWithoutRequestBody(url, SERVICE_UNAVAILABLE, response)

        val result = await(connector.calculateLiability(nino, taxYear, crystallise = false))

        result mustBe Left(ErrorModel(SERVICE_UNAVAILABLE, ErrorBodyModel("SERVICE_UNAVAILABLE", "Dependent systems are currently not responding.")))

      }

      "IF returns an error with crystallise flag" in {
        val response =
          """
            |{
            |  "code": "SERVICE_UNAVAILABLE",
            |  "reason": "Dependent systems are currently not responding."
            |}
            |""".stripMargin
        stubPostWithoutRequestBody(crystalliseUrl, SERVICE_UNAVAILABLE, response)

        val result = await(connector.calculateLiability(nino, taxYear, crystallise = true))

        result mustBe Left(ErrorModel(SERVICE_UNAVAILABLE, ErrorBodyModel("SERVICE_UNAVAILABLE", "Dependent systems are currently not responding.")))

      }
    }
  }
}
