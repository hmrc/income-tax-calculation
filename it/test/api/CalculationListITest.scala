/*
 * Copyright 2026 HM Revenue & Customs
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

import helpers.WiremockSpec
import models.{ErrorBodyModel, GetCalculationListModel}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers.mustBe
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import play.api.Application
import play.api.http.HeaderNames
import play.api.http.Status.*
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}


class CalculationListITest extends AnyWordSpec
  with WiremockSpec with ScalaFutures with Matchers {

  val nino: String = "AA123123A"
  val taxYear26: String = "2026"
  val taxYearRange26: String = "25-26"
  val taxYear24: String = "2024"
  val taxYearRange24: String = "23-24"
  val taxYear22: String = "2022"

  val url: String => String = taxYear => s"/income-tax-calculation/calculation-list/$nino/$taxYear"
  val url2083: String = s"/income-tax/$taxYearRange26/view/$nino/calculations-summary"
  val url2150: String = s"/income-tax/$taxYearRange24/view/calculations-summary/$nino"
  val url5624: String = s"/itsa/income-tax/v1/$taxYearRange24/view/calculations/liability/$nino"
  val urlLegacy: String = s"/itsd/calculations/liability/$nino\\?taxYear=$taxYear22"

  val mtditidHeader: (String, String) = ("mtditid", "555555555")
  val authorization: (String, String) = HeaderNames.AUTHORIZATION -> "mock-bearer-token"
  val auth: Boolean => Unit = isAgent => if (isAgent) agentAuthorised() else authorised()

  val calcListResponse: String = Json.toJson(Seq(GetCalculationListModel(
    calculationId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2",
    calculationTimestamp = "2019-03-17T09:22:59Z",
    calculationType = "IY",
    calculationTrigger = None
  ))).toString

  val calcListJson: String = {
    """
      |{
      |  "calculations": [
      |    {
      |      "calculationId": "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2",
      |      "calculationTimestamp": "2019-03-17T09:22:59Z",
      |      "calculationType": "inYear"
      |    }
      |  ]
      |}
      |""".stripMargin.trim
  }

  val errorResponse: String = Json.toJson(ErrorBodyModel("ERROR", "error")).toString()
  val parsingErrorResponse: String =
    Json.toJson(ErrorBodyModel("PARSING_ERROR", "Error parsing response from API")).toString()

  override implicit val patienceConfig: PatienceConfig = PatienceConfig(Span(5, Seconds))

  override implicit lazy val app: Application = GuiceApplicationBuilder()
    .configure(
      ("feature-switch.useEncryption" -> true) +:
        ("auditing.consumer.baseUri.port" -> wireMockPort) +:
        ("feature-switch.useGetCalcListHipPlatform5624" -> false) +:
        servicesToUrlConfig: _*
    )
    .build()
  
  val hip5624Tester: hip5624ITest =
    new hip5624ITest(url, url5624, taxYear24, calcListResponse, auth, mtditidHeader, authorization)

  
  "get calculation list" when {

    Seq(false, true).foreach { isAgent =>

      s"the user is an ${if (isAgent) "agent" else "individual"}" should {

        "return the calculation list when tax year >= 2026 (2083)" in {
          auth(isAgent)

          stubGetWithResponseBody(url2083, OK, calcListResponse)

          whenReady(buildClient(url(taxYear26))
            .withHttpHeaders(mtditidHeader, authorization)
            .get()) {
            result =>
              result.status mustBe OK
              Json.parse(result.body) mustBe Json.parse(calcListJson)
          }
        }

        "return the calculation list when tax 2024 <= year < 2026 and useGetCalcListHipPlatform5624 is true (5624)" in {
          val result: (Int, JsValue) = hip5624Tester.runTest(isAgent)
          result._1 mustBe OK
          result._2 mustBe Json.parse(calcListJson)
        }

        "return the calculation list when 2024 <= tax year < 2026 and there are no special cases (2150)" in {
          auth(isAgent)

          stubGetWithResponseBody(url2150, OK, calcListResponse)

          whenReady(buildClient(url(taxYear24))
            .withHttpHeaders(mtditidHeader, authorization)
            .get()) {
            result =>
              result.status mustBe OK
              Json.parse(result.body) mustBe Json.parse(calcListJson)
          }
        }

        "return the calculation list when tax year < 2024 (legacy)" in {
          auth(isAgent)

          stubGetWithResponseBody(urlLegacy, OK, calcListResponse)

          whenReady(buildClient(url(taxYear22))
            .withHttpHeaders(mtditidHeader, authorization)
            .get()) {
            result =>
              result.status mustBe OK
              Json.parse(result.body) mustBe Json.parse(calcListJson)
          }
        }

        "returns an INTERNAL_SERVER_ERROR when receiving an INTERNAL_SERVER_ERROR" in {
          auth(isAgent)

          stubGetWithResponseBody(url2083, INTERNAL_SERVER_ERROR, errorResponse)

          whenReady(buildClient(url(taxYear26))
            .withHttpHeaders(mtditidHeader, authorization)
            .get()) {
            result =>
              result.status mustBe INTERNAL_SERVER_ERROR
              Json.parse(result.body) mustBe Json.parse(errorResponse)
          }
        }

        "returns a SERVICE_UNAVAILABLE when receiving a SERVICE_UNAVAILABLE" in {
          auth(isAgent)

          stubGetWithResponseBody(url2083, SERVICE_UNAVAILABLE, errorResponse)

          whenReady(buildClient(url(taxYear26))
            .withHttpHeaders(mtditidHeader, authorization)
            .get()) {
            result =>
              result.status mustBe SERVICE_UNAVAILABLE
              Json.parse(result.body) mustBe Json.parse(errorResponse)
          }
        }

        "returns a NO_CONTENT when receiving a NOT_FOUND" in {
          auth(isAgent)

          stubGetWithResponseBody(url2083, NOT_FOUND, errorResponse)

          whenReady(buildClient(url(taxYear26))
            .withHttpHeaders(mtditidHeader, authorization)
            .get()) {
            result =>
              result.status mustBe NO_CONTENT
          }
        }

        "returns an INTERNAL_SERVER_ERROR when receiving other status codes" in {
          auth(isAgent)

          stubGetWithResponseBody(url2083, NO_CONTENT, errorResponse)

          whenReady(buildClient(url(taxYear26))
            .withHttpHeaders(mtditidHeader, authorization)
            .get()) {
            result =>
              result.status mustBe INTERNAL_SERVER_ERROR
              Json.parse(result.body) mustBe Json.parse(parsingErrorResponse)
          }
        }
      }
    }
  }
}


class hip5624ITest(url: String => String,
                   url5624: String,
                   taxYear24: String,
                   calcListResponse: String,
                   auth: Boolean => Unit,
                   mtditidHeader: (String, String),
                   authorization: (String, String)
                  ) extends AnyWordSpec with WiremockSpec with ScalaFutures {

  override implicit lazy val app: Application = GuiceApplicationBuilder()
    .configure(
      ("feature-switch.useEncryption" -> true) +:
        ("auditing.consumer.baseUri.port" -> wireMockPort) +:
        ("feature-switch.useGetCalcListHipPlatform5624" -> true) +:
        servicesToUrlConfig: _*
    )
    .build()

  def runTest(isAgent: Boolean): (Int, JsValue) = {
      auth(isAgent)

      stubGetWithResponseBody(url5624, OK, calcListResponse)

      whenReady(buildClient(url(taxYear24))
        .withHttpHeaders(mtditidHeader, authorization)
        .get()) {
        result => (result.status, Json.parse(result.body))
      }
  }
}