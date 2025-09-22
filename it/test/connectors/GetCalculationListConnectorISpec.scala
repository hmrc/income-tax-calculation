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

import config.BackendAppConfig
import helpers.WiremockSpec
import models.{ErrorBodyModel, ErrorModel, GetCalculationListModel, UnauthorisedErrorBodyModel}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Configuration
import play.api.http.Status.{BAD_REQUEST, CONFLICT, INTERNAL_SERVER_ERROR, OK, SERVICE_UNAVAILABLE, UNAUTHORIZED, UNPROCESSABLE_ENTITY}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class GetCalculationListConnectorISpec extends AnyWordSpec with WiremockSpec with Matchers{

  lazy val connector: GetCalculationListConnector = app.injector.instanceOf[GetCalculationListConnector]

  lazy val httpClient: HttpClientV2 = app.injector.instanceOf[HttpClientV2]

  def appConfig(ifHost: String): BackendAppConfig = new BackendAppConfig(app.injector.instanceOf[Configuration], app.injector.instanceOf[ServicesConfig]) {
    override val ifBaseUrl: String = s"http://$ifHost:$wireMockPort"
  }

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val nino = "nino"
  private def getURL1896(nino: String, taxYearRange: String): String = s"/income-tax/view/calculations/liability/$taxYearRange/$nino"
  private def getURL2083(nino: String, taxYearRange: String): String = s"/income-tax/$taxYearRange/view/$nino/calculations-summary"

  private def getResponse1896: String = {
    Json.toJson(Seq(GetCalculationListModel(
      calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      calculationTimestamp = "2019-03-17T09:22:59Z",
      calculationType = "inYear",
      requestedBy = Some("customer"),
      fromDate = Some("2013-05-d1"),
      toDate = Some("2016-05-d1")
    ))).toString
  }

  private def getResponse2083: String = {
    Json.obj("calculationsSummary" ->
      Json.toJson(Seq(GetCalculationListModel(
        calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
        calculationTimestamp = "2019-03-17T09:22:59Z",
        calculationType = "inYear",
        requestedBy = Some("customer"),
        fromDate = Some("2013-05-d1"),
        toDate = Some("2016-05-d1")
      )))
    ).toString
  }

  "GetCalculationListConnector" should {

    val appConfigWithInternalHost = appConfig("localhost")
    val connector = new GetCalculationListConnector(httpClient, appConfigWithInternalHost)

    "return a success result" when {
      val successModel = GetCalculationListModel(
        calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
        calculationTimestamp = "2019-03-17T09:22:59Z",
        calculationType = "inYear",
        requestedBy = Some("customer"),
        fromDate = Some("2013-05-d1"),
        toDate = Some("2016-05-d1")
      )
      "IF 1896 returns a success with expected JSON" in {
        val response = getResponse1896

        stubGetWithResponseBody(getURL1896(nino, "23-24"), OK, response)
        val result = await(connector.getCalculationList1896(nino, "2024"))

        result mustBe Right(Seq(successModel))
      }

      "IF 1896 returns a success result for future tax year 24/25" in {
        val response = getResponse1896

        stubGetWithResponseBody(getURL1896(nino, "24-25"), OK, response)
        val result = await(connector.getCalculationList1896(nino, "2025"))

        result mustBe Right(Seq(successModel))
      }

      "IF 2083 returns a success result for future tax year 25/26" in {
        val response = getResponse2083
        stubGetWithResponseBody(getURL2083(nino, "25-26"), OK, response)
        val result = await(connector.getCalculationList2083(nino, "2026"))

        result mustBe Right(Seq(successModel))
      }
    }
  }

  "return a failure result" when {
    val appConfigWithInternalHost = appConfig("localhost")
    val connector = new GetCalculationListConnector(httpClient, appConfigWithInternalHost)

      val errorBodyModel = ErrorBodyModel("DES_CODE", "DES_REASON")

      Seq(BAD_REQUEST, CONFLICT, UNPROCESSABLE_ENTITY, INTERNAL_SERVER_ERROR, SERVICE_UNAVAILABLE).foreach { status =>
        s"DES returns $status" in {
          val desError = ErrorModel(status, errorBodyModel)
          implicit val hc: HeaderCarrier = HeaderCarrier()

          stubGetWithResponseBody(getURL2083(nino, "25-26"), status, desError.toJson.toString)

          val result = await(connector.getCalculationList(nino, "2026")(hc))

          result mustBe Left(desError)
        }
      }

    "IF returns an 401 error" in {
      val response =
        """{"error_description":"The access token is invalid or has expired","error":"invalid_token"}"""
      stubGetWithResponseBody(getURL2083(nino, "25-26"), UNAUTHORIZED, response)

      val result = await(connector.getCalculationList(nino, taxYear = "2026"))

      result mustBe Left(ErrorModel(UNAUTHORIZED, UnauthorisedErrorBodyModel("invalid_token", "The access token is invalid or has expired")))
    }
    "IF returns an 503 error" in {
      val response =
        """
          |{
          |  "code": "SERVICE_UNAVAILABLE",
          |  "reason": "Dependent systems are currently not responding."
          |}
          |""".stripMargin
      stubGetWithResponseBody(getURL1896(nino, "23-24"), SERVICE_UNAVAILABLE, response)

      val result = await(connector.getCalculationList1896(nino, taxYear = "2024"))

      result mustBe Left(ErrorModel(SERVICE_UNAVAILABLE, ErrorBodyModel("SERVICE_UNAVAILABLE", "Dependent systems are currently not responding.")))
    }


    "IF returns an 500 when parsing error occurs" in {
      val appConfigWithInternalHost = appConfig("localhost")
      val connector = new GetCalculationListConnector(httpClient, appConfigWithInternalHost)

      val response = Json.toJson(GetCalculationListModel(
        calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
        calculationTimestamp = "2019-03-17T09:22:59Z",
        calculationType = "inYear",
        requestedBy = Some("customer"),
        fromDate = Some("2013-05-d1"),
        toDate = Some("2016-05-d1")
      )).toString()

      stubGetWithResponseBody(getURL2083(nino, "25-26"), OK, response)

      val result = await(connector.getCalculationList2083(nino, taxYear = "2026"))

      result mustBe Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("PARSING_ERROR",
        "Error parsing response from API - List((,List(JsonValidationError(List(error.expected.jsarray),List()))))")))
    }
  }

}