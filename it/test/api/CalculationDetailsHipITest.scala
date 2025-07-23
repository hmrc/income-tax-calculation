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

import constants.HipGetCalculationDetailsConstants._
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
        ("feature-switch.useGetCalcDetailHIPlatform" -> enableHip) +:
        ("feature-switch.useGetCalcDetailsLegacyToHipPlatform" -> enableHip) +:
        servicesToUrlConfig: _*
    )
    .build()

  "get calculation details" when {

    "the user is an individual" should {

      "return the calculation details when called with the tax year 2026" in new Setup {
        authorised()

        stubGetWithResponseBody(ifGetCalcListUrl26, 200, listCalcResponse)
        stubGetWithResponseBody(hipUrlForCalculationDetails, 200, Json.toJson(successFullModelGetCalculationDetailsHip).toString())

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2026")
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe 200
            result.body mustBe
              Json.toJson(successFullModelGetCalculationDetailsHip).toString()
        }
      }

      "return the calculation details when called with the tax year 2019" in new Setup {
        authorised()

        stubGetWithResponseBody(s"/itsd/calculations/liability/$successNino\\?taxYear=2019", 200, listCalcResponseLegacy)
        stubGetWithResponseBody(s"/itsa/income-tax/v1/18-19/view/calculations/liability/$successNino/$calculationId", 200, Json.toJson(successFullModelGetCalculationDetailsHip).toString())

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2019")
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe 200
            result.body mustBe
              Json.toJson(successFullModelGetCalculationDetailsHip).toString()
        }
      }

      "return the calculation details when called with TYS tax year 2024" in new Setup {
        authorised()
        val getCalcList1896 = s"/income-tax/view/calculations/liability/23-24/$successNino"

        stubGetWithResponseBody(getCalcList1896, 200, listCalcResponse)
        stubGetWithResponseBody(s"/itsa/income-tax/v1/23-24/view/calculations/liability/$successNino/$calculationId", 200, Json.toJson(successFullModelGetCalculationDetailsHip).toString())

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2024")
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe 200
            result.body mustBe
              Json.toJson(successFullModelGetCalculationDetailsHip).toString()
        }
      }

      "return the calculation details when called with TYS tax year 2025" in new Setup {
        val getCalcList1896 = s"/income-tax/view/calculations/liability/24-25/$successNino"
        authorised()

        stubGetWithResponseBody(getCalcList1896, 200, listCalcResponse)
        stubGetWithResponseBody(s"/itsa/income-tax/v1/24-25/view/calculations/liability/$successNino/$calculationId", 200, Json.toJson(successFullModelGetCalculationDetailsHip).toString())

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2025")
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe 200
            result.body mustBe
              Json.toJson(successFullModelGetCalculationDetailsHip).toString()
        }
      }


      "return a INTERNAL_SERVER_ERROR when IF returns an INTERNAL_SERVER_ERROR from list calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(ifGetCalcListUrl26, 500, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2026")
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe 500
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a INTERNAL_SERVER_ERROR when des returns an INTERNAL_SERVER_ERROR from get calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody("/income-tax/25-26/view/AA123123A/calculations-summary", 200, listCalcResponse)
        stubGetWithResponseBody(hipUrlForCalculationDetails, 500, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2026")
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe 500
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when des returns an SERVICE_UNAVAILABLE from list calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()
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
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody("/income-tax/25-26/view/AA123123A/calculations-summary", 200, listCalcResponse)
        stubGetWithResponseBody(hipUrlForCalculationDetails, 503, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2026")
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe 503
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a NO_CONTENT when if returns an NOT_FOUND from get calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody("/income-tax/25-26/view/AA123123A/calculations-summary", 200, listCalcResponse)
        stubGetWithResponseBody(hipUrlForCalculationDetails, 404, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2026")
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe 204
        }
      }

      "return a NO_CONTENT when des returns an NOT_FOUND from get calc details legacy" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

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
        val response: String = Json.toJson(ErrorBodyModel("NOT_FOUND", "not found")).toString()

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

        stubGetWithResponseBody(ifGetCalcListUrl26, 200, listCalcResponse)
        stubGetWithResponseBody(hipUrlForCalculationDetails, 200, Json.toJson(successFullModelGetCalculationDetailsHip).toString())

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe 200
            result.body mustBe
              Json.toJson(successFullModelGetCalculationDetailsHip).toString()
        }
      }


      "return a INTERNAL_SERVER_ERROR when IF returns an INTERNAL_SERVER_ERROR from list calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(hipUrlForCalculationDetails, 500, response)

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 500
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }


      "return a INTERNAL_SERVER_ERROR when HIP returns an INTERNAL_SERVER_ERROR from get calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(ifGetCalcListUrl26, 200, listCalcResponse)
        stubGetWithResponseBody(hipUrlForCalculationDetails, 500, response)

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 500
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }


      "return a SERVICE_UNAVAILABLE when IF returns an SERVICE_UNAVAILABLE from list calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(ifGetCalcListUrl26, 503, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 503
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when HIP returns an SERVICE_UNAVAILABLE from get calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(ifGetCalcListUrl26, 200, listCalcResponse)
        stubGetWithResponseBody(hipUrlForCalculationDetails, 503, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 503
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a 204 when des returns an 404" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("NOT_FOUND", "not found")).toString()

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

        stubGetWithResponseBody(hipUrlForCalculationDetails, 200, Json.toJson(successFullModelGetCalculationDetailsHip).toString())

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe 200
        }

      }


      "return a INTERNAL_SERVER_ERROR when HIP returns an INTERNAL_SERVER_ERROR from get calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(hipUrlForCalculationDetails, 500, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 500
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when HIP returns an SERVICE_UNAVAILABLE from get calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(hipUrlForCalculationDetails, 503, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 503
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a 204 when IF returns an 404" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("NOT_FOUND", "not found")).toString()

        authorised()

        stubGetWithResponseBody(hipUrlForCalculationDetails, 404, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026")
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

        stubGetWithResponseBody(hipUrlForCalculationDetails, 200, Json.toJson(successFullModelGetCalculationDetailsHip).toString())

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 200
            result.body mustBe
              Json.toJson(successFullModelGetCalculationDetailsHip).toString()
        }

      }

      "return a INTERNAL_SERVER_ERROR when HIP returns an INTERNAL_SERVER_ERROR from get calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(hipUrlForCalculationDetails, 500, response)

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 500
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when HIP returns an SERVICE_UNAVAILABLE from get calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(hipUrlForCalculationDetails, 503, response)

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 503
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a 204 when des returns an 404" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("NOT_FOUND", "not found")).toString()

        authorised()

        stubGetWithResponseBody(hipUrlForCalculationDetails, 404, response)

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 204
        }
      }
    }
  }
}
