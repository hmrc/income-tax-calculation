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

import play.api.http.Status._
import constants.HipGetCalculationDetailsConstants.*
import helpers.{CalculationDetailsITestHelper, WiremockSpec}
import models.ErrorBodyModel
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.time.{Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.WSBodyReadables.readableAsJson

class CalculationDetailsHipITest extends AnyWordSpec
  with WiremockSpec with ScalaFutures with Matchers with CalculationDetailsITestHelper {

  override implicit val patienceConfig: PatienceConfig = PatienceConfig(Span(5, Seconds))
  private val enableHip: Boolean = true

  override implicit lazy val app: Application = GuiceApplicationBuilder()
    .configure(
      ("feature-switch.useEncryption" -> true) +:
        ("auditing.consumer.baseUri.port" -> wireMockPort) +:
        ("feature-switch.useGetCalcDetailsHipPlatform5294" -> enableHip) +:
        ("feature-switch.useGetCalcListHipPlatform5624" -> enableHip) +:
        servicesToUrlConfig: _*
    )
    .build()

  "get calculation details" when {

    "the user is an individual" should {

      "return the calculation details when called with the tax year 2026" in new Setup {
        authorised()

        stubGetWithResponseBody(ifGetCalcListUrl26, OK, listCalcResponse)
        stubGetWithResponseBody(hipUrlForCalculationDetails, OK, Json.toJson(successFullModelGetCalculationDetailsHip).toString())

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2026")
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe OK
            result.body mustBe
              Json.toJson(successFullModelGetCalculationDetailsHip)
        }
      }

      "return the calculation details when called with the tax year 2019" in new Setup {
        authorised()

        stubGetWithResponseBody(s"/itsd/calculations/liability/$successNino\\?taxYear=2019", OK, listCalcResponseLegacy)
        stubGetWithResponseBody(s"/itsa/income-tax/v1/18-19/view/calculations/liability/$successNino/$calculationId", OK, Json.toJson(successFullModelGetCalculationDetailsHip).toString())

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2019")
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe OK
            result.body mustBe
              Json.toJson(successFullModelGetCalculationDetailsHip.copy(submissionChannel = None))
        }
      }

      "return the calculation details when called with TYS tax year 2024" in new Setup {
        authorised()
        val getCalcList5624 = s"/itsa/income-tax/v1/23-24/view/calculations/liability/$successNino"

        stubGetWithResponseBody(getCalcList5624, OK, listCalcResponse)
        stubGetWithResponseBody(s"/itsa/income-tax/v1/23-24/view/calculations/liability/$successNino/$calculationId", OK, Json.toJson(successFullModelGetCalculationDetailsHip).toString())

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2024")
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe OK
            result.body mustBe
              Json.toJson(successFullModelGetCalculationDetailsHip)
        }
      }

      "return the calculation details when called with TYS tax year 2025" in new Setup {
        val getCalcList5624 = s"/itsa/income-tax/v1/24-25/view/calculations/liability/$successNino"
        authorised()

        stubGetWithResponseBody(getCalcList5624, OK, listCalcResponse)
        stubGetWithResponseBody(s"/itsa/income-tax/v1/24-25/view/calculations/liability/$successNino/$calculationId", OK, Json.toJson(successFullModelGetCalculationDetailsHip).toString())

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2025")
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe OK
            result.body mustBe
              Json.toJson(successFullModelGetCalculationDetailsHip)
        }
      }


      "return a INTERNAL_SERVER_ERROR when IF returns an INTERNAL_SERVER_ERROR from list calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(ifGetCalcListUrl26, INTERNAL_SERVER_ERROR, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2026")
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe INTERNAL_SERVER_ERROR
            result.body.toString mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a INTERNAL_SERVER_ERROR when des returns an INTERNAL_SERVER_ERROR from get calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody("/income-tax/25-26/view/AA123123A/calculations-summary", OK, listCalcResponse)
        stubGetWithResponseBody(hipUrlForCalculationDetails, INTERNAL_SERVER_ERROR, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2026")
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe INTERNAL_SERVER_ERROR
            result.body.toString mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when des returns an SERVICE_UNAVAILABLE from list calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()
        val calcListLegacyUrl: String = if (enableHip) hipCalcListLegacyWithoutTaxYear else desUrlForListCalcWithoutTaxYear

        authorised()

        stubGetWithResponseBody(calcListLegacyUrl, SERVICE_UNAVAILABLE, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe SERVICE_UNAVAILABLE
            result.body.toString mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when des returns an SERVICE_UNAVAILABLE from get calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody("/income-tax/25-26/view/AA123123A/calculations-summary", OK, listCalcResponse)
        stubGetWithResponseBody(hipUrlForCalculationDetails, SERVICE_UNAVAILABLE, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2026")
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe SERVICE_UNAVAILABLE
            result.body.toString mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a NOT_FOUND when if returns an NOT_FOUND from get calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody("/income-tax/25-26/view/AA123123A/calculations-summary", OK, listCalcResponse)
        stubGetWithResponseBody(hipUrlForCalculationDetails, NOT_FOUND, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2026")
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe NOT_FOUND
        }
      }

      "return a NO_CONTENT when des returns an NOT_FOUND from get calc details legacy" in new Setup {
        
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForListCalcWithTaxYear, NOT_FOUND, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details")
          .withHttpHeaders(mtditidHeader, authorization)
          .withQueryStringParameters(("taxYear", taxYear))
          .get()) {
          result =>
            result.status mustBe 204
        }
      }

      "return a NOT_FOUND when des returns an NOT_FOUND" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("NOT_FOUND", "not found")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForListCalcWithoutTaxYear, NOT_FOUND, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe NOT_FOUND
        }
      }
    }

    "the user is an agent" should {

      "return the calc details when called with the tax year 2026" in new Setup {
        agentAuthorised()

        stubGetWithResponseBody(ifGetCalcListUrl26, OK, listCalcResponse)
        stubGetWithResponseBody(hipUrlForCalculationDetails, OK, Json.toJson(successFullModelGetCalculationDetailsHip).toString())

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe OK
            result.body mustBe
              Json.toJson(successFullModelGetCalculationDetailsHip)
        }
      }

      "return the calculation details when called with the tax year 2019" in new Setup {
        agentAuthorised()

        stubGetWithResponseBody(s"/itsd/calculations/liability/$successNino\\?taxYear=2019", OK, listCalcResponseLegacy)
        stubGetWithResponseBody(s"/itsa/income-tax/v1/18-19/view/calculations/liability/$successNino/$calculationId", OK, Json.toJson(successFullModelGetCalculationDetailsHip).toString())

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2019")
          .withHttpHeaders(mtditidHeader, authorization, correlationId)
          .get()) {
          result =>
            result.status mustBe OK
            result.body mustBe
              Json.toJson(successFullModelGetCalculationDetailsHip.copy(submissionChannel = None))
        }
      }


      "return a INTERNAL_SERVER_ERROR when IF returns an INTERNAL_SERVER_ERROR from list calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        agentAuthorised()

        stubGetWithResponseBody(hipUrlForCalculationDetails, INTERNAL_SERVER_ERROR, response)

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe INTERNAL_SERVER_ERROR
            result.body.toString mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }


      "return a INTERNAL_SERVER_ERROR when HIP returns an INTERNAL_SERVER_ERROR from get calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        agentAuthorised()

        stubGetWithResponseBody(ifGetCalcListUrl26, OK, listCalcResponse)
        stubGetWithResponseBody(hipUrlForCalculationDetails, INTERNAL_SERVER_ERROR, response)

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe INTERNAL_SERVER_ERROR
            result.body.toString mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }


      "return a SERVICE_UNAVAILABLE when IF returns an SERVICE_UNAVAILABLE from list calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        agentAuthorised()

        stubGetWithResponseBody(ifGetCalcListUrl26, SERVICE_UNAVAILABLE, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe SERVICE_UNAVAILABLE
            result.body.toString mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when HIP returns an SERVICE_UNAVAILABLE from get calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        agentAuthorised()

        stubGetWithResponseBody(ifGetCalcListUrl26, OK, listCalcResponse)
        stubGetWithResponseBody(hipUrlForCalculationDetails, SERVICE_UNAVAILABLE, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe SERVICE_UNAVAILABLE
            result.body.toString mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a NOT_FOUND when des returns an NOT_FOUND" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("NOT_FOUND", "not found")).toString()

        agentAuthorised()

        stubGetWithResponseBody(desUrlForListCalcWithoutTaxYear, NOT_FOUND, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe NOT_FOUND
        }
      }
    }
  }

  "get calculation details with calcId" when {

    "the user is an individual" should {

      "return the calculation details when called with the tax year 2026" in new Setup {
        authorised()

        stubGetWithResponseBody(hipUrlForCalculationDetails, OK, Json.toJson(successFullModelGetCalculationDetailsHip).toString())

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe OK
        }

      }

      "return the calculation details when called with the tax year 2019" in new Setup {
        authorised()

        stubGetWithResponseBody(s"/itsa/income-tax/v1/18-19/view/calculations/liability/$successNino/$calculationId", OK, Json.toJson(successFullModelGetCalculationDetailsHip).toString())

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2019")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe OK
            result.body mustBe
              Json.toJson(successFullModelGetCalculationDetailsHip)
        }
      }


      "return a INTERNAL_SERVER_ERROR when HIP returns an INTERNAL_SERVER_ERROR from get calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(hipUrlForCalculationDetails, INTERNAL_SERVER_ERROR, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe INTERNAL_SERVER_ERROR
            result.body.toString mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when HIP returns an SERVICE_UNAVAILABLE from get calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(hipUrlForCalculationDetails, SERVICE_UNAVAILABLE, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe SERVICE_UNAVAILABLE
            result.body.toString mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a NOT_FOUND when IF returns an NOT_FOUND" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("NOT_FOUND", "not found")).toString()

        authorised()

        stubGetWithResponseBody(hipUrlForCalculationDetails, NOT_FOUND, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe NOT_FOUND
        }
      }
    }

    "the user is an agent" should {

      "return the calc details when called with the tax year 2026" in new Setup {
        agentAuthorised()

        stubGetWithResponseBody(hipUrlForCalculationDetails, OK, Json.toJson(successFullModelGetCalculationDetailsHip).toString())

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe OK
            result.body mustBe
              Json.toJson(successFullModelGetCalculationDetailsHip)
        }

      }

      "return the calculation details when called with the tax year 2019" in new Setup {
        authorised()

        stubGetWithResponseBody(s"/itsa/income-tax/v1/18-19/view/calculations/liability/$successNino/$calculationId", OK, Json.toJson(successFullModelGetCalculationDetailsHip).toString())

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2019")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe OK
            result.body mustBe
              Json.toJson(successFullModelGetCalculationDetailsHip)
        }
      }

      "return a INTERNAL_SERVER_ERROR when HIP returns an INTERNAL_SERVER_ERROR from get calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(hipUrlForCalculationDetails, INTERNAL_SERVER_ERROR, response)

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe INTERNAL_SERVER_ERROR
            result.body.toString mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when HIP returns an SERVICE_UNAVAILABLE from get calc details" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(hipUrlForCalculationDetails, SERVICE_UNAVAILABLE, response)

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe SERVICE_UNAVAILABLE
            result.body.toString mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a NOT_FOUND when des returns an NOT_FOUND" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("NOT_FOUND", "not found")).toString()

        authorised()

        stubGetWithResponseBody(hipUrlForCalculationDetails, NOT_FOUND, response)

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details?taxYear=2026", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe NOT_FOUND
        }
      }
    }
  }
}
