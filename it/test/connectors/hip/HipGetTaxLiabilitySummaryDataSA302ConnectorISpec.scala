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

package connectors.hip

import config.BackendAppConfig
import constants.HipGetTaxLiabilitySummaryDataSA302Constants.successTaxLiabilitySummaryDataSA302Model
import helpers.WiremockSpec
import models.hip.taxLiabilitySummaryDataSA302.TaxLiabilitySummaryDataSA302Model
import models.{ErrorBodyModel, ErrorModel}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Configuration
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK, SERVICE_UNAVAILABLE}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class HipGetTaxLiabilitySummaryDataSA302ConnectorISpec extends AnyWordSpec with WiremockSpec with Matchers {

  lazy val connector: HipGetTaxLiabilitySummaryDataSA302Connector = app.injector.instanceOf[HipGetTaxLiabilitySummaryDataSA302Connector]

  lazy val httpClient: HttpClientV2 = app.injector.instanceOf[HttpClientV2]
  val taxYear: String = "2027"

  implicit val hc: HeaderCarrier = HeaderCarrier()
  val taxableEntityId: String = "AB1232211"
  val calculationId: String = "021a5e4d-12b6-2d2a-a396-2cfbdf92a7f1"

  def appConfig(ifHost: String): BackendAppConfig = new BackendAppConfig(app.injector.instanceOf[Configuration], app.injector.instanceOf[ServicesConfig]) {
    override val ifBaseUrl: String = s"http://$ifHost:$wireMockPort"
  }

  private def getGetTaxLiabilitySummaryDataSA302URL(taxYear: String, taxableEntityId: String, calculationId: String): String = s"/income-tax/v1/$taxYear/tax-liability-summary/$taxableEntityId/$calculationId"

  private def getGetTaxLiabilitySummaryDataSA302Response: String = {
    Json.toJson(successTaxLiabilitySummaryDataSA302Model).toString
  }

  "HipGetTaxLiabilitySummaryDataSA302Connector" should {

    val appConfigWithInternalHost = appConfig("localhost")
    val connector = new HipGetTaxLiabilitySummaryDataSA302Connector(httpClient, appConfigWithInternalHost)

    "return a success result" when {

      val successModel: TaxLiabilitySummaryDataSA302Model = successTaxLiabilitySummaryDataSA302Model

      "Hip Get Tax Liability Summary Data SA302 returns a success with expected JSON" in {

        stubGetWithResponseBody(getGetTaxLiabilitySummaryDataSA302URL(taxYear, taxableEntityId, calculationId), OK, getGetTaxLiabilitySummaryDataSA302Response)
        val result = await(connector.getGetTaxLiabilitySummaryDataSA302(taxYear, taxableEntityId, calculationId))

        result mustBe Right(successModel)
      }
    }
  }

  "return a failure result" when {
    val appConfigWithInternalHost = appConfig("localhost")
    val connector = new HipGetTaxLiabilitySummaryDataSA302Connector(httpClient, appConfigWithInternalHost)

    "Hip Get Tax Liability Summary Data SA302 returns an 503 error" in {
      val serviceUnavailableResponse =
        """
          |{
          |  "code": "SERVICE_UNAVAILABLE",
          |  "reason": "Dependent systems are currently not responding."
          |}
          |""".stripMargin
      stubGetWithResponseBody(getGetTaxLiabilitySummaryDataSA302URL(taxYear, taxableEntityId, calculationId), SERVICE_UNAVAILABLE, serviceUnavailableResponse)

      val result = await(connector.getGetTaxLiabilitySummaryDataSA302(taxYear, taxableEntityId, calculationId))

      result mustBe Left(ErrorModel(SERVICE_UNAVAILABLE, ErrorBodyModel("SERVICE_UNAVAILABLE", "Dependent systems are currently not responding.")))
    }


    "Hip Get Tax Liability Summary Data SA302 returns a 500 when parsing error occurs" in {
      val appConfigWithInternalHost = appConfig("localhost")
      val connector = new HipGetTaxLiabilitySummaryDataSA302Connector(httpClient, appConfigWithInternalHost)

      val parsingErrorResponse = Json.toJson(Seq(successTaxLiabilitySummaryDataSA302Model)).toString

      stubGetWithResponseBody(getGetTaxLiabilitySummaryDataSA302URL(taxYear, taxableEntityId, calculationId), OK, parsingErrorResponse)

      val result = await(connector.getGetTaxLiabilitySummaryDataSA302(taxYear, taxableEntityId, calculationId))

      result mustBe Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("PARSING_ERROR",
        "Error parsing response from API - List((,List(JsonValidationError(List(error.expected.jsobject),ArraySeq()))))")))
    }
  }
}