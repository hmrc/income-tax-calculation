/*
 * Copyright 2022 HM Revenue & Customs
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
import com.github.tomakehurst.wiremock.http.HttpHeader
import helpers.WiremockSpec
import models.{DesErrorBodyModel, GetCalculationListModel}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.time.{Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

class CalculationDetailsITest extends AnyWordSpec with WiremockSpec with ScalaFutures with Matchers{

  trait Setup {
    implicit val patienceConfig: PatienceConfig = PatienceConfig(Span(5, Seconds))
    val successNino: String = "AA123123A"
    val taxYear = "2021"
    val calculationId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"
    val desUrlForListCalcWithoutTaxYear = s"/income-tax/list-of-calculation-results/$successNino"
    val desUrlForListCalcWithTaxYear = s"/income-tax/list-of-calculation-results/$successNino\\?taxYear=$taxYear"
    val desUrlForCalculationDetails = s"/income-tax/view/calculations/liability/$successNino/$calculationId"
    val listCalcResponse = Json.toJson(Seq(GetCalculationListModel(calculationId,"2019-03-17T09:22:59Z"))).toString()
    val agentClientCookie: Map[String, String] = Map("MTDITID" -> "555555555")
    val mtditidHeader = ("mtditid", "555555555")
    val requestHeaders: Seq[HttpHeader] = Seq(new HttpHeader("mtditid", "555555555"))
    auditStubs()
    mergedAuditStubs()
  }

  "get calculation details" when {

    "the user is an individual" should {

      "return the calculation details when called without tax year" in new Setup {
        authorised()

        stubGetWithResponseBody(desUrlForListCalcWithoutTaxYear, 200, listCalcResponse)
        stubGetWithResponseBody(desUrlForCalculationDetails, 200, successCalcDetailsExpectedJsonFull)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details")
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 200
            Json.parse(result.body) mustBe
              Json.parse(s"""$successCalcDetailsExpectedJsonFull""")
        }

      }

      "return the calculation details when called with tax year" in new Setup {
        authorised()

        stubGetWithResponseBody(desUrlForListCalcWithTaxYear, 200, listCalcResponse)
        stubGetWithResponseBody(desUrlForCalculationDetails, 200, successCalcDetailsExpectedJsonFull)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details?taxYear=$taxYear")
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 200
            Json.parse(result.body) mustBe
              Json.parse(s"""$successCalcDetailsExpectedJsonFull""")
        }

      }

      "return a INTERNAL_SERVER_ERROR when des returns an INTERNAL_SERVER_ERROR from list calc details" in new Setup {
        val response = Json.toJson(DesErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForListCalcWithoutTaxYear, 500, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details")
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 500
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a INTERNAL_SERVER_ERROR when des returns an INTERNAL_SERVER_ERROR from get calc details" in new Setup {
        val response = Json.toJson(DesErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForListCalcWithoutTaxYear, 200, listCalcResponse)
        stubGetWithResponseBody(desUrlForCalculationDetails, 500, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details")
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 500
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when des returns an SERVICE_UNAVAILABLE from list calc details" in new Setup {
        val response = Json.toJson(DesErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForListCalcWithoutTaxYear, 503, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details")
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 503
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when des returns an SERVICE_UNAVAILABLE from get calc details" in new Setup {
        val response = Json.toJson(DesErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForListCalcWithoutTaxYear, 200, listCalcResponse)
        stubGetWithResponseBody(desUrlForCalculationDetails, 503, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details")
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 503
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a 4XX when des returns an 4XX" in new Setup {
        val response = Json.toJson(DesErrorBodyModel("NOT_FOUND", "not found")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForListCalcWithoutTaxYear, 404, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details")
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 404
            result.body mustBe
              """{"code":"NOT_FOUND","reason":"not found"}"""
        }
      }
    }

    "the user is an agent" should {

      "return the calc details" in new Setup {
        agentAuthorised()

        stubGetWithResponseBody(desUrlForListCalcWithoutTaxYear, 200, listCalcResponse)
        stubGetWithResponseBody(desUrlForCalculationDetails, 200, successCalcDetailsExpectedJsonFull)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 200
            Json.parse(result.body) mustBe
              Json.parse(s"""$successCalcDetailsExpectedJsonFull""")
        }

      }

      "return a INTERNAL_SERVER_ERROR when des returns an INTERNAL_SERVER_ERROR from list calc details" in new Setup {
        val response = Json.toJson(DesErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForListCalcWithoutTaxYear, 500, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 500
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a INTERNAL_SERVER_ERROR when des returns an INTERNAL_SERVER_ERROR from get calc details" in new Setup {
        val response = Json.toJson(DesErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForListCalcWithoutTaxYear, 200, listCalcResponse)
        stubGetWithResponseBody(desUrlForCalculationDetails, 500, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 500
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when des returns an SERVICE_UNAVAILABLE from list calc details" in new Setup {
        val response = Json.toJson(DesErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForListCalcWithoutTaxYear, 503, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 503
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when des returns an SERVICE_UNAVAILABLE from get calc details" in new Setup {
        val response = Json.toJson(DesErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForListCalcWithoutTaxYear, 200, listCalcResponse)
        stubGetWithResponseBody(desUrlForCalculationDetails, 503, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 503
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a 4XX when des returns an 4XX" in new Setup {
        val response = Json.toJson(DesErrorBodyModel("NOT_FOUND", "not found")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForListCalcWithoutTaxYear, 404, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 404
            result.body mustBe
              """{"code":"NOT_FOUND","reason":"not found"}"""
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
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 200
            Json.parse(result.body) mustBe
              Json.parse(s"""$successCalcDetailsExpectedJsonFull""")
        }

      }

      "return a INTERNAL_SERVER_ERROR when des returns an INTERNAL_SERVER_ERROR from get calc details" in new Setup {
        val response = Json.toJson(DesErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForCalculationDetails, 500, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details")
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 500
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when des returns an SERVICE_UNAVAILABLE from get calc details" in new Setup {
        val response = Json.toJson(DesErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForCalculationDetails, 503, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details")
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 503
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a 4XX when IF returns an 4XX" in new Setup {
        val response = Json.toJson(DesErrorBodyModel("NOT_FOUND", "not found")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForCalculationDetails, 404, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details")
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 404
            result.body mustBe
              """{"code":"NOT_FOUND","reason":"not found"}"""
        }
      }
    }

    "the user is an agent" should {

      "return the calc details" in new Setup {
        agentAuthorised()

        stubGetWithResponseBody(desUrlForCalculationDetails, 200, successCalcDetailsExpectedJsonFull)

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details",additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 200
            Json.parse(result.body) mustBe
              Json.parse(s"""$successCalcDetailsExpectedJsonFull""")
        }

      }

      "return a INTERNAL_SERVER_ERROR when des returns an INTERNAL_SERVER_ERROR from get calc details" in new Setup {
        val response = Json.toJson(DesErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForCalculationDetails, 500, response)

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 500
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when des returns an SERVICE_UNAVAILABLE from get calc details" in new Setup {
        val response = Json.toJson(DesErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForCalculationDetails, 503, response)

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 503
            result.body mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a 4XX when des returns an 4XX" in new Setup {
        val response = Json.toJson(DesErrorBodyModel("NOT_FOUND", "not found")).toString()

        authorised()

        stubGetWithResponseBody(desUrlForCalculationDetails, 404, response)

        whenReady(buildClient(
          s"/income-tax-calculation/income-tax/nino/$successNino/calc-id/$calculationId/calculation-details", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader)
          .get) {
          result =>
            result.status mustBe 404
            result.body mustBe
              """{"code":"NOT_FOUND","reason":"not found"}"""
        }
      }

    }
  }
}