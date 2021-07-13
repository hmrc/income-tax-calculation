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

package helpers

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.JsValue

trait WiremockStubHelpers {

  def stubGetWithResponseBody(url: String, status: Int, response: String, requestHeaders: Seq[HttpHeader] = Seq.empty): StubMapping = {
    val mappingWithHeaders: MappingBuilder = requestHeaders.foldLeft(get(urlMatching(url))) { (result, nxt) =>
      result.withHeader(nxt.key(), equalTo(nxt.firstValue()))
    }
    stubFor(mappingWithHeaders
      .willReturn(
        aResponse()
          .withStatus(status)
          .withBody(response)
          .withHeader("Content-Type", "application/json; charset=utf-8")))

  }

  def stubGetWithoutResponseBody(url: String, status: Int): StubMapping =
    stubFor(get(urlMatching(url))
      .willReturn(
        aResponse()
          .withStatus(status)))

  def stubPostWithoutResponseBody(url: String, status: Int, requestBody: String, requestHeaders: Seq[HttpHeader] = Seq.empty): StubMapping = {
    val mappingWithHeaders: MappingBuilder = requestHeaders.foldLeft(post(urlEqualTo(url))){ (result, nxt) =>
      result.withHeader(nxt.key(), equalTo(nxt.firstValue()))
    }
    stubFor(mappingWithHeaders
      .willReturn(
        aResponse()
          .withStatus(status)
          .withHeader("Content-Type", "application/json; charset=utf-8")))
  }

  def stubPostWithResponseBody(url: String, status: Int, requestBody: String, response: String): StubMapping =
    stubFor(post(urlEqualTo(url)).withRequestBody(equalToJson(requestBody))
      .willReturn(
        aResponse()
          .withStatus(status)
          .withBody(response)
          .withHeader("Content-Type", "application/json; charset=utf-8")))

  def stubPostWithoutRequestBody(url: String, status: Int, response: String, requestHeaders: Seq[HttpHeader] = Seq.empty): StubMapping = {
    val mappingWithHeaders: MappingBuilder = requestHeaders.foldLeft(post(urlEqualTo(url))) { (result, nxt) =>
      result.withHeader(nxt.key(), equalTo(nxt.firstValue()))
    }
    stubFor(mappingWithHeaders
      .willReturn(
        aResponse()
          .withStatus(status)
          .withBody(response)
          .withHeader("Content-Type", "application/json; charset=utf-8")))
  }

  def stubPutWithoutResponseBody(url: String, requestBody: String, status: Int): StubMapping =
    stubFor(put(urlEqualTo(url))
      .willReturn(
        aResponse()
          .withStatus(status)
          .withHeader("Content-Type", "application/json; charset=utf-8")))

  def stubPatchWithoutResponseBody(url: String, requestBody: String, status: Int): StubMapping =
    stubFor(patch(urlEqualTo(url))
      .willReturn(
        aResponse()
          .withStatus(status)
          .withHeader("Content-Type", "application/json; charset=utf-8")))

  def stubPostWithoutResponseAndRequestBody(url: String, status: Int): StubMapping =
    stubFor(post(urlEqualTo(url))
      .willReturn(
        aResponse()
          .withStatus(status)
          .withHeader("Content-Type", "application/json; charset=utf-8")))

  def verifyPostWithRequestBody(url: String, times: Int, body: JsValue): Unit =
    verify(times, postRequestedFor(urlEqualTo(url))
      .withRequestBody(equalToJson(body.toString(), true, true))
    )

  def auditStubs(): Unit = {
    val auditResponseCode = 204
    stubPostWithoutResponseAndRequestBody("/write/audit", auditResponseCode)
  }

}
