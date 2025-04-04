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

package api

import assets.GetCalculationDetailsConstants.successCalcDetailsExpectedJsonFull
import helpers.{CalculationDetailsITestHelper, WiremockSpec}
import models.ErrorBodyModel
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.time.{Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json

class CalculationDetailsHipITest extends AnyWordSpec
  with WiremockSpec with ScalaFutures with Matchers with CalculationDetailsITestHelper {

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

  "get calculation details" when {

    "the user is an individual" should {

      "return the calculation details when called without tax year" in new Setup {
        val calcListLegacyUrl: String = if (enableHip) hipCalcListLegacyWithoutTaxYear else desUrlForListCalcWithoutTaxYear

        authorised()

        stubGetWithResponseBody(calcListLegacyUrl, 200, listCalcResponseLegacy)
        stubGetWithResponseBody(desUrlForCalculationDetails, 200, successCalcDetailsExpectedJsonFull)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 200
            Json.parse(result.body) mustBe
              Json.parse(s"""$successCalcDetailsExpectedJsonFull""")
        }

      }

      "return the calculation details when called with tax year" in new Setup {
        authorised()

        stubGetWithResponseBody(hipUrlForListCalcWithTaxYear, 200, listCalcResponseLegacy)
        stubGetWithResponseBody(desUrlForCalculationDetails, 200, successCalcDetailsExpectedJsonFull)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=$taxYear")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 200
            Json.parse(result.body) mustBe
              Json.parse(s"""$successCalcDetailsExpectedJsonFull""")
        }

      }

      "return the calculation details when called with TYS tax year 23/24" in new Setup {
        authorised()

        def getCalcListURL(taxYearRange: String): String = s"/income-tax/view/calculations/liability/$taxYearRange/$successNino"

        stubGetWithResponseBody(getCalcListURL("23-24"), 200, listCalcResponse)
        stubGetWithResponseBody(ifUrlforTYS24, 200, successCalcDetailsExpectedJsonFull)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2024")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 200
            Json.parse(result.body) mustBe
              Json.parse(s"""$successCalcDetailsExpectedJsonFull""")
        }
      }

      "return the calculation details when called with TYS tax year 24/25" in new Setup {
        authorised()

        def getCalcListURL(taxYearRange: String): String = s"/income-tax/view/calculations/liability/$taxYearRange/$successNino"

        stubGetWithResponseBody(getCalcListURL("24-25"), 200, listCalcResponse)
        stubGetWithResponseBody(ifUrlforTYS25, 200, successCalcDetailsExpectedJsonFull)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2025")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 200
            Json.parse(result.body) mustBe
              Json.parse(s"""$successCalcDetailsExpectedJsonFull""")
        }
      }

      "return a INTERNAL_SERVER_ERROR when des returns an INTERNAL_SERVER_ERROR from list calc details" in new Setup {
        val response = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()
        val calcListLegacyUrl: String = if (enableHip) hipCalcListLegacyWithoutTaxYear else desUrlForListCalcWithoutTaxYear

        authorised()

        stubGetWithResponseBody(calcListLegacyUrl, 500, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 500
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a INTERNAL_SERVER_ERROR when des returns an INTERNAL_SERVER_ERROR from get calc details" in new Setup {
        val response = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()
        val calcListLegacyUrl: String = if (enableHip) hipCalcListLegacyWithoutTaxYear else desUrlForListCalcWithoutTaxYear

        authorised()

        stubGetWithResponseBody(calcListLegacyUrl, 200, listCalcResponseLegacy)
        stubGetWithResponseBody(desUrlForCalculationDetails, 500, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 500
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when des returns an SERVICE_UNAVAILABLE from list calc details" in new Setup {
        val response = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()
        val calcListLegacyUrl: String = if (enableHip) hipCalcListLegacyWithoutTaxYear else desUrlForListCalcWithoutTaxYear

        authorised()

        stubGetWithResponseBody(calcListLegacyUrl, 503, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 503
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when des returns an SERVICE_UNAVAILABLE from get calc details" in new Setup {
        val response = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()
        val calcListLegacyUrl: String = if (enableHip) hipCalcListLegacyWithoutTaxYear else desUrlForListCalcWithoutTaxYear

        authorised()

        stubGetWithResponseBody(calcListLegacyUrl, 200, listCalcResponseLegacy)
        stubGetWithResponseBody(desUrlForCalculationDetails, 503, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 503
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a NO_CONTENT when if returns an NOT_FOUND from get calc details" in new Setup {
        val response = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()
        authorised()
        stubGetWithResponseBody(ifUrlForCalculationList, 404, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details")
          .withHttpHeaders(mtditidHeader, authorization)
          .withQueryStringParameters(("taxYear","2024"))
          .get()) {
          result =>
            result.status mustBe 204
        }
      }

      "return a NO_CONTENT when des returns an NOT_FOUND from get calc details legacy" in new Setup {
        val response = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()
        authorised()
        stubGetWithResponseBody(desUrlForListCalcWithTaxYear, 404, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details")
          .withHttpHeaders(mtditidHeader, authorization)
          .withQueryStringParameters(("taxYear", taxYear))
          .get()) {
          result =>
            result.status mustBe 204
        }
      }

      "return a 204 when des returns an 404" in new Setup {
        val response = Json.toJson(ErrorBodyModel("NOT_FOUND", "not found")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForListCalcWithoutTaxYear, 404, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 204
        }
      }
    }

    "the user is an agent" should {

      "return the calc details" in new Setup {
        agentAuthorised()
        val calcListLegacyUrl: String = if (enableHip) hipCalcListLegacyWithoutTaxYear else desUrlForListCalcWithoutTaxYear

        stubGetWithResponseBody(calcListLegacyUrl, 200, listCalcResponseLegacy)
        stubGetWithResponseBody(desUrlForCalculationDetails, 200, successCalcDetailsExpectedJsonFull)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 200
            Json.parse(result.body) mustBe
              Json.parse(s"""$successCalcDetailsExpectedJsonFull""")
        }

      }

      "return a INTERNAL_SERVER_ERROR when des returns an INTERNAL_SERVER_ERROR from list calc details" in new Setup {
        val response = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()
        val calcListLegacyUrl: String = if (enableHip) hipCalcListLegacyWithoutTaxYear else desUrlForListCalcWithoutTaxYear

        authorised()

        stubGetWithResponseBody(calcListLegacyUrl, 500, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 500
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a INTERNAL_SERVER_ERROR when des returns an INTERNAL_SERVER_ERROR from get calc details" in new Setup {
        val response = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()
        val calcListLegacyUrl: String = if (enableHip) hipCalcListLegacyWithoutTaxYear else desUrlForListCalcWithoutTaxYear

        authorised()

        stubGetWithResponseBody(calcListLegacyUrl, 200, listCalcResponseLegacy)
        stubGetWithResponseBody(desUrlForCalculationDetails, 500, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 500
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when des returns an SERVICE_UNAVAILABLE from list calc details" in new Setup {
        val response = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()
        val calcListLegacyUrl: String = if (enableHip) hipCalcListLegacyWithoutTaxYear else desUrlForListCalcWithoutTaxYear

        authorised()

        stubGetWithResponseBody(calcListLegacyUrl, 503, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 503
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when des returns an SERVICE_UNAVAILABLE from get calc details" in new Setup {
        val response = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()
        val calcListLegacyUrl: String = if (enableHip) hipCalcListLegacyWithoutTaxYear else desUrlForListCalcWithoutTaxYear

        authorised()

        stubGetWithResponseBody(calcListLegacyUrl, 200, listCalcResponseLegacy)
        stubGetWithResponseBody(desUrlForCalculationDetails, 503, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 503
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a 204 when des returns an 404" in new Setup {
        val response = Json.toJson(ErrorBodyModel("NOT_FOUND", "not found")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForListCalcWithoutTaxYear, 404, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 204
        }
      }

    }
  }

  "get calculation details with calcId" when {

    "the user is an individual" should {

      "return the calculation details" in new Setup {
        authorised()

        stubGetWithResponseBody(desUrlForCalculationDetails, 200, successCalcDetailsExpectedJsonFull)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 200
            Json.parse(result.body) mustBe
              Json.parse(s"""$successCalcDetailsExpectedJsonFull""")
        }

      }

      "return a INTERNAL_SERVER_ERROR when des returns an INTERNAL_SERVER_ERROR from get calc details" in new Setup {
        val response = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForCalculationDetails, 500, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 500
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when des returns an SERVICE_UNAVAILABLE from get calc details" in new Setup {
        val response = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForCalculationDetails, 503, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 503
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a 204 when IF returns an 404" in new Setup {
        val response = Json.toJson(ErrorBodyModel("NOT_FOUND", "not found")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForCalculationDetails, 404, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 204
        }
      }
    }

    "the user is an agent" should {

      "return the calc details" in new Setup {
        agentAuthorised()

        stubGetWithResponseBody(desUrlForCalculationDetails, 200, successCalcDetailsExpectedJsonFull)

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 200
            Json.parse(result.body) mustBe
              Json.parse(s"""$successCalcDetailsExpectedJsonFull""")
        }

      }

      "return a INTERNAL_SERVER_ERROR when des returns an INTERNAL_SERVER_ERROR from get calc details" in new Setup {
        val response = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForCalculationDetails, 500, response)

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 500
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when des returns an SERVICE_UNAVAILABLE from get calc details" in new Setup {
        val response = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForCalculationDetails, 503, response)

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 503
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a 204 when des returns an 404" in new Setup {
        val response = Json.toJson(ErrorBodyModel("NOT_FOUND", "not found")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForCalculationDetails, 404, response)

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 204
        }
      }

    }
  }
}
