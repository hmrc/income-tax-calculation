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
import models.ErrorBodyModel
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.time.{Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.HeaderNames
import play.api.http.Status._
import play.api.libs.json.{JsString, Json}

class DeclareCrystallisationITest extends AnyWordSpec with WiremockSpec with ScalaFutures with Matchers {

  def toTaxYearParam(taxYear: Int): String = {
    s"${(taxYear - 1).toString takeRight 2}-${taxYear.toString takeRight 2}"
  }

  trait Setup {
    implicit val patienceConfig: PatienceConfig = PatienceConfig(Span(5, Seconds))
    val nino: String = "AA123123A"
    val taxYear = "2022"
    val calculationId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"
    val ifUrl = s"/income-tax/${toTaxYearParam(2022)}/calculation/$nino/$calculationId/crystallise"
    val agentClientCookie: Map[String, String] = Map("MTDITID" -> "555555555")
    val mtditidHeader: (String, String) = ("mtditid", "555555555")
    val authorization: (String, String) = HeaderNames.AUTHORIZATION -> "mock-bearer-token"
    val requestHeaders: Seq[HttpHeader] = Seq(new HttpHeader("mtditid", "555555555"))
    auditStubs()
  }

  "declareCrystallisation" when {

    "the user is an individual" should {

      "return a 204 NoContent if declareCrystallisation has been posted successfully" in new Setup {

        val response: String = JsString("").toString()

        authorised()
        stubPostWithoutRequestBody(ifUrl, NO_CONTENT, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader, authorization)
          .post("""{}""")) {
          result =>
            result.status mustBe 204
        }
      }

      "return an InternalServerError(500) when DES returns an InternalServerError" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        authorised()
        stubPostWithoutRequestBody(ifUrl, INTERNAL_SERVER_ERROR, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader, authorization)
          .post("""{}""")) {
          result =>
            result.status mustBe 500
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a ServiceUnavailable(503) when DES returns ServiceUnavailable" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        authorised()
        stubPostWithoutRequestBody(ifUrl, SERVICE_UNAVAILABLE, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader, authorization)
          .post("""{}""")) {
          result =>
            result.status mustBe 503
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a 4XX response when DES returns a 404 NotFound" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        authorised()
        stubPostWithoutRequestBody(ifUrl, NOT_FOUND, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader, authorization)
          .post("""{}""")) {
          result =>
            result.status mustBe 404
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a 4XX response when DES returns a 409 Conflict" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        authorised()
        stubPostWithoutRequestBody(ifUrl, CONFLICT, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader, authorization)
          .post("""{}""")) {
          result =>
            result.status mustBe 409
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a 4XX response when DES returns a 400 BadRequest" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        authorised()
        stubPostWithoutRequestBody(ifUrl, BAD_REQUEST, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader, authorization)
          .post("""{}""")) {
          result =>
            result.status mustBe 400
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a 4XX response when IF returns a 422 UnprocessableEntity" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        authorised()
        stubPostWithoutRequestBody(ifUrl, UNPROCESSABLE_ENTITY, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader, authorization)
          .post("""{}""")) {
          result =>
            result.status mustBe 422
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }
    }

    "the user is an agent" should {

      "return a 204 NoContent if declareCrystallisation has been posted successfully" in new Setup {

        val response: String = JsString("").toString()

        agentAuthorised()

        stubPostWithoutRequestBody(ifUrl, NO_CONTENT, response)
        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader, authorization)
          .post("""{}""")) {
          result =>
            result.status mustBe 204
        }
      }

      "return an InternalServerError(500) when IF returns an InternalServerError" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        agentAuthorised()
        stubPostWithoutRequestBody(ifUrl, INTERNAL_SERVER_ERROR, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader, authorization)
          .post("""{}""")) {
          result =>
            result.status mustBe 500
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a ServiceUnavailable(503) when IF returns ServiceUnavailable" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        agentAuthorised()
        stubPostWithoutRequestBody(ifUrl, SERVICE_UNAVAILABLE, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader, authorization)
          .post("""{}""")) {
          result =>
            result.status mustBe 503
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a 4XX response when IF returns a 409 Conflict" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        agentAuthorised()
        stubPostWithoutRequestBody(ifUrl, CONFLICT, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader, authorization)
          .post("""{}""")) {
          result =>
            result.status mustBe 409
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a 4XX response when IF returns a 404 NotFound" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        agentAuthorised()
        stubPostWithoutRequestBody(ifUrl, NOT_FOUND, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader, authorization)
          .post("""{}""")) {
          result =>
            result.status mustBe 404
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a 4XX response when IF returns a 400 BadRequest" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        agentAuthorised()
        stubPostWithoutRequestBody(ifUrl, BAD_REQUEST, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader, authorization)
          .post("""{}""")) {
          result =>
            result.status mustBe 400
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a 4XX response when IF returns a 422 UnprocessableEntity" in new Setup {
        val response: String = Json.toJson(ErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        agentAuthorised()
        stubPostWithoutRequestBody(ifUrl, UNPROCESSABLE_ENTITY, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader, authorization)
          .post("""{}""")) {
          result =>
            result.status mustBe 422
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }
    }
  }
}
