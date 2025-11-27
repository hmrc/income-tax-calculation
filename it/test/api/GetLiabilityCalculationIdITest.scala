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

import com.github.tomakehurst.wiremock.http.HttpHeader
import helpers.WiremockSpec
import models.{ErrorBodyModel, LiabilityCalculationIdModel}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import org.scalatest.matchers.must.Matchers
import play.api.http.HeaderNames

class GetLiabilityCalculationIdITest extends AnyWordSpec with WiremockSpec with ScalaFutures with Matchers{

  trait Setup {
    implicit val patienceConfig: PatienceConfig = PatienceConfig(Span(5, Seconds))
    val successNino: String = "AA123123A"
    val taxYear = "2021"
    val desUrl = s"/income-tax/nino/$successNino/taxYear/$taxYear/tax-calculation?crystallise=false"
    val agentClientCookie: Map[String, String] = Map("MTDITID" -> "555555555")
    val mtditidHeader = ("mtditid", "555555555")
    val authorization: (String, String) = HeaderNames.AUTHORIZATION -> "mock-bearer-token"
    val requestHeaders: Seq[HttpHeader] = Seq(new HttpHeader("mtditid", "555555555"))
    auditStubs()
  }

  "get calculation id" when {

    "the user is an individual" should {

      "return the calc id" in new Setup {
        val response = Json.toJson(LiabilityCalculationIdModel("00000000-0000-1000-8000-000000000000")).toString()

        authorised()

        stubPostWithoutRequestBody(desUrl, 200, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/taxYear/$taxYear/tax-calculation")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 200
            result.body.toString mustBe
              """{"id":"00000000-0000-1000-8000-000000000000"}"""
        }

      }

      "return a INTERNAL_SERVER_ERROR when des returns an INTERNAL_SERVER_ERROR" in new Setup {
        val response = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubPostWithoutRequestBody(desUrl, 500, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/taxYear/$taxYear/tax-calculation")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 500
            result.body.toString mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when des returns an SERVICE_UNAVAILABLE" in new Setup {
        val response = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        authorised()

        stubPostWithoutRequestBody(desUrl, 503, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/taxYear/$taxYear/tax-calculation")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 503
            result.body.toString mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a 4XX when des returns an 4XX" in new Setup {
        val response = Json.toJson(ErrorBodyModel("NOT_FOUND", "not found")).toString()

        authorised()

        stubPostWithoutRequestBody(desUrl, 404, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/taxYear/$taxYear/tax-calculation")
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 404
            result.body.toString mustBe
              """{"code":"NOT_FOUND","reason":"not found"}"""
        }
      }
    }

    "the user is an agent" should {

      "return the calc id" in new Setup {
        val response = Json.toJson(LiabilityCalculationIdModel("00000000-0000-1000-8000-000000000000")).toString()

        agentAuthorised()

        stubPostWithoutRequestBody(desUrl, 200, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/taxYear/$taxYear/tax-calculation", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 200
            result.body.toString mustBe
              """{"id":"00000000-0000-1000-8000-000000000000"}"""
        }

      }

      "return a INTERNAL_SERVER_ERROR when des returns an INTERNAL_SERVER_ERROR" in new Setup {
        val response = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        agentAuthorised()

        stubPostWithoutRequestBody(desUrl, 500, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/taxYear/$taxYear/tax-calculation", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 500
            result.body.toString mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a SERVICE_UNAVAILABLE when des returns an SERVICE_UNAVAILABLE" in new Setup {
        val response = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()

        agentAuthorised()

        stubPostWithoutRequestBody(desUrl, 503, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/taxYear/$taxYear/tax-calculation", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 503
            result.body.toString mustBe
              """{"code":"ERROR","reason":"error"}"""
        }
      }

      "return a 4XX when des returns an 4XX" in new Setup {
        val response = Json.toJson(ErrorBodyModel("NOT_FOUND", "not found")).toString()

        agentAuthorised()

        stubPostWithoutRequestBody(desUrl, 404, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$successNino/taxYear/$taxYear/tax-calculation", additionalCookies = agentClientCookie)
          .withHttpHeaders(mtditidHeader, authorization)
          .get()) {
          result =>
            result.status mustBe 404
            result.body.toString mustBe
              """{"code":"NOT_FOUND","reason":"not found"}"""
        }
      }

    }
  }
}
