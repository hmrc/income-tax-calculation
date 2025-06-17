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

import com.github.tomakehurst.wiremock.http.HttpHeader
import config.BackendAppConfig
import helpers.WiremockSpec
import models.{ErrorBodyModel, ErrorModel}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Configuration
import play.api.http.Status._
import play.api.libs.json.JsString
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames, SessionId}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class DeclareCrystallisationConnectorISpec extends AnyWordSpec with WiremockSpec with Matchers {

  lazy val connector: DeclareCrystallisationConnector = app.injector.instanceOf[DeclareCrystallisationConnector]

  lazy val httpClient: HttpClientV2 = app.injector.instanceOf[HttpClientV2]

  def appConfig(desHost: String): BackendAppConfig = new BackendAppConfig(app.injector.instanceOf[Configuration], app.injector.instanceOf[ServicesConfig]) {
    override val desBaseUrl: String = s"http://$desHost:$wireMockPort"
  }

  val taxYear = 2022

  "DeclareCrystallisationConnector" should {

    val appConfigWithInternalHost = appConfig("localhost")
    val appConfigWithExternalHost = appConfig("127.0.0.1")

    def toTaxYearParam(taxYear: Int): String = {
      s"${(taxYear - 1).toString takeRight 2}-${taxYear.toString takeRight 2}"
    }

    val nino = "taxable_entity_id"
    val calculationId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"
    val url = s"/income-tax/${toTaxYearParam(taxYear)}/calculation/$nino/$calculationId/crystallise"


    "include internal headers" when {
      val headersSentToBenefits = Seq(
        new HttpHeader(HeaderNames.xSessionId, "sessionIdValue")
      )

      "the host for DES is 'internal'" in {
        implicit val headerCarrier: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("sessionIdValue")))
        val connector = new DeclareCrystallisationConnector(httpClient, appConfigWithInternalHost)

        stubPostWithoutResponseBody(url, NO_CONTENT, JsString("").toString(), headersSentToBenefits)

        val result = await(connector.declareCrystallisation(nino, taxYear, calculationId)(headerCarrier))

        result mustBe Right(())
      }

      "the host for DES is 'external'" in {
        implicit val headerCarrier: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("sessionIdValue")))
        val connector = new DeclareCrystallisationConnector(httpClient, appConfigWithExternalHost)

        stubPostWithoutResponseBody(url, NO_CONTENT, JsString("").toString(), headersSentToBenefits)

        val result = await(connector.declareCrystallisation(nino, taxYear, calculationId)(headerCarrier))

        result mustBe Right(())
      }
    }

    "handle errors" when {
      val errorBodyModel = ErrorBodyModel("DES_CODE", "DES_REASON")

      Seq(BAD_REQUEST, NOT_FOUND, CONFLICT, INTERNAL_SERVER_ERROR, SERVICE_UNAVAILABLE).foreach { status =>
        s"DES returns $status" in {
          val desError = ErrorModel(status, errorBodyModel)
          implicit val hc: HeaderCarrier = HeaderCarrier()

          stubPostWithoutRequestBody(url, status, desError.toJson.toString)

          val result = await(connector.declareCrystallisation(nino, taxYear, calculationId)(hc))

          result mustBe Left(desError)
        }
      }
        "DES returns an unexpected error - 502 BadGateway" in {
          val desError = ErrorModel(BAD_GATEWAY, errorBodyModel)
          implicit val hc: HeaderCarrier = HeaderCarrier()

          stubPostWithoutRequestBody(url, BAD_GATEWAY, desError.toJson.toString())

          val result = await(connector.declareCrystallisation(nino, taxYear, calculationId)(hc))

          result mustBe Left(desError)
        }
    }
  }

}
