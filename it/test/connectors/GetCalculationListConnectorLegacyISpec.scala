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

import config.BackendAppConfig
import connectors.hip.HipCalculationLegacyListConnector
import helpers.WiremockSpec
import models.{ErrorBodyModel, ErrorModel, GetCalculationListModelLegacy}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Configuration
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK, SERVICE_UNAVAILABLE}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class GetCalculationListConnectorLegacyISpec extends AnyWordSpec with WiremockSpec with Matchers {

  lazy val connector: HipCalculationLegacyListConnector = app.injector.instanceOf[HipCalculationLegacyListConnector]

  lazy val httpClient: HttpClientV2 = app.injector.instanceOf[HttpClientV2]

  def appConfig(hipHost: String, isCalcMigratedToIF: Boolean = false): BackendAppConfig = new BackendAppConfig(app.injector.instanceOf[Configuration], app.injector.instanceOf[ServicesConfig]) {
    override val hipBaseUrl: String = s"http://$hipHost:$wireMockPort"
  }

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val nino = "nino"
  val taxYear = Some("2021")
  val url = s"/itsd/calculations/liability/$nino"
  val taxYearUrl = s"/itsd/calculations/liability/$nino\\?taxYear=${taxYear.get}"

  "GetCalculationListConnector" should {

    val appConfigWithInternalHost = appConfig("localhost")
    val connector = new HipCalculationLegacyListConnector(httpClient, appConfigWithInternalHost)

    "return a success result" when {
      "DES returns a success with expected JSON" in {
        val response =
          Json.toJson(Seq(GetCalculationListModelLegacy("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2019-03-17T09:22:59Z"))).toString

        stubGetWithResponseBody(url, OK, response)
        val result = await(connector.calcList(nino, None))

        result mustBe Right(Seq(GetCalculationListModelLegacy("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2019-03-17T09:22:59Z")))
      }


      "DES returns a success with expected JSON with OptionalTaxYear" in {
        val response =
          Json.toJson(Seq(GetCalculationListModelLegacy("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2019-03-17T09:22:59Z"))).toString

        stubGetWithResponseBody(taxYearUrl, OK, response)
        val result = await(connector.calcList(nino, taxYear))

        result mustBe Right(Seq(GetCalculationListModelLegacy("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2019-03-17T09:22:59Z")))
      }

      "IF returns a success with expected JSON" in {
        val appConfigWithInternalHost = appConfig("localhost", isCalcMigratedToIF = true)
        val connector = new HipCalculationLegacyListConnector(httpClient, appConfigWithInternalHost)

        val response =
          Json.toJson(Seq(GetCalculationListModelLegacy("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2019-03-17T09:22:59Z"))).toString

        stubGetWithResponseBody(url, OK, response)
        val result = await(connector.calcList(nino, None))

        result mustBe Right(Seq(GetCalculationListModelLegacy("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2019-03-17T09:22:59Z")))
      }

      "IF returns a success with expected JSON with OptionalTaxYear" in {
        val appConfigWithInternalHost = appConfig("localhost", isCalcMigratedToIF = true)
        val connector = new HipCalculationLegacyListConnector(httpClient, appConfigWithInternalHost)

        val response =
          Json.toJson(Seq(GetCalculationListModelLegacy("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2019-03-17T09:22:59Z"))).toString

        stubGetWithResponseBody(taxYearUrl, OK, response)
        val result = await(connector.calcList(nino, taxYear))

        result mustBe Right(Seq(GetCalculationListModelLegacy("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2019-03-17T09:22:59Z")))
      }
    }
  }

  "return a failure result" when {
    val appConfigWithInternalHost = appConfig("localhost")
    val connector = new HipCalculationLegacyListConnector(httpClient, appConfigWithInternalHost)

    "DES returns an 503 error" in {
      val response =
        """
          |{
          |  "code": "SERVICE_UNAVAILABLE",
          |  "reason": "Dependent systems are currently not responding."
          |}
          |""".stripMargin
      stubGetWithResponseBody(url, SERVICE_UNAVAILABLE, response)

      val result = await(connector.calcList(nino, None))

      result mustBe Left(ErrorModel(SERVICE_UNAVAILABLE, ErrorBodyModel("SERVICE_UNAVAILABLE", "Dependent systems are currently not responding.")))
    }


    "DES returns an 500 when parsing error occurs" in {
      val appConfigWithInternalHost = appConfig("localhost")
      val connector = new HipCalculationLegacyListConnector(httpClient, appConfigWithInternalHost)

      val response = Json.toJson(GetCalculationListModelLegacy("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2019-03-17T09:22:59Z")).toString()

      stubGetWithResponseBody(url, OK, response)

      val result = await(connector.calcList(nino, None))

      result mustBe Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("PARSING_ERROR",
        "Error parsing response from API - List((,List(JsonValidationError(List(error.expected.jsarray),ArraySeq()))))")))
    }

    "DES returns an 503 error with OptionalTaxYear" in {
      val appConfigWithInternalHost = appConfig("localhost")
      val connector = new HipCalculationLegacyListConnector(httpClient, appConfigWithInternalHost)

      val response =
        """
          |{
          |  "code": "SERVICE_UNAVAILABLE",
          |  "reason": "Dependent systems are currently not responding."
          |}
          |""".stripMargin
      stubGetWithResponseBody(taxYearUrl, SERVICE_UNAVAILABLE, response)

      val result = await(connector.calcList(nino, taxYear))

      result mustBe Left(ErrorModel(SERVICE_UNAVAILABLE, ErrorBodyModel("SERVICE_UNAVAILABLE", "Dependent systems are currently not responding.")))
    }


    "DES returns an 500 when parsing error occurs with OptionalTaxYear" in {
      val appConfigWithInternalHost = appConfig("localhost")
      val connector = new HipCalculationLegacyListConnector(httpClient, appConfigWithInternalHost)

      val response = Json.toJson(GetCalculationListModelLegacy("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2019-03-17T09:22:59Z")).toString()

      stubGetWithResponseBody(taxYearUrl, OK, response)

      val result = await(connector.calcList(nino, taxYear))

      result mustBe Left(ErrorModel(INTERNAL_SERVER_ERROR,
        ErrorBodyModel("PARSING_ERROR", "Error parsing response from API - List((,List(JsonValidationError(List(error.expected.jsarray),ArraySeq()))))")))
    }
  }

}