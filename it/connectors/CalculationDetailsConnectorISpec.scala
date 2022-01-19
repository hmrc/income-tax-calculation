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

package connectors

import com.github.tomakehurst.wiremock.http.HttpHeader
import config.BackendAppConfig
import helpers.WiremockSpec
import models.{DesErrorBodyModel, DesErrorModel}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Configuration
import play.api.http.Status._
import assets.GetCalculationDetailsConstants.{successExpectedJsonFull, successModelFull}
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames, HttpClient, SessionId}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class CalculationDetailsConnectorISpec extends AnyWordSpec with WiremockSpec with Matchers {

  lazy val connector: CalculationDetailsConnector = app.injector.instanceOf[CalculationDetailsConnector]

  lazy val httpClient: HttpClient = app.injector.instanceOf[HttpClient]

  def appConfig(ifHost: String): BackendAppConfig = new BackendAppConfig(app.injector.instanceOf[Configuration], app.injector.instanceOf[ServicesConfig]) {
    override val ifBaseUrl: String = s"http://$ifHost:$wireMockPort"
  }

  "CalculationDetailsConnector" should {

    val appConfigWithInternalHost = appConfig("localhost")
    val connector = new CalculationDetailsConnector(httpClient, appConfigWithInternalHost)

    val nino = "taxable_entity_id"
    val calculationId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"
    val url = s"/income-tax/view/calculations/liability/$nino/$calculationId"

    "include internal headers" when {
      val headersSentToBenefits = Seq(
        new HttpHeader(HeaderNames.xSessionId, "sessionIdValue")
      )

      "the host for DES is 'internal'" in {
        implicit val headerCarrier: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("sessionIdValue")))

        stubGetWithResponseBody(url, OK, successExpectedJsonFull, headersSentToBenefits)

        val result = await(connector.getCalculationDetails(nino, calculationId)(headerCarrier))

        result mustBe Right(successModelFull)
      }
    }

    "handle errors" when {
      val desErrorBodyModel = DesErrorBodyModel("DES_CODE", "DES_REASON")

      Seq(BAD_REQUEST, NOT_FOUND, CONFLICT, INTERNAL_SERVER_ERROR, SERVICE_UNAVAILABLE).foreach { status =>
        s"DES returns $status" in {
          val desError = DesErrorModel(status, desErrorBodyModel)
          implicit val hc: HeaderCarrier = HeaderCarrier()

          stubGetWithResponseBody(url, status, desError.toJson.toString)

          val result = await(connector.getCalculationDetails(nino, calculationId)(hc))

          result mustBe Left(desError)
        }
      }
        "DES returns an unexpected error - 502 BadGateway" in {
          val desError = DesErrorModel(BAD_GATEWAY, desErrorBodyModel)
          implicit val hc: HeaderCarrier = HeaderCarrier()

          stubGetWithResponseBody(url, BAD_GATEWAY, desError.toJson.toString())

          val result = await(connector.getCalculationDetails(nino, calculationId)(hc))

          result mustBe Left(desError)
        }
    }
  }
}
