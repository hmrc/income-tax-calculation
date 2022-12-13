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

import config.BackendAppConfig
import helpers.WiremockSpec
import models.{DesErrorBodyModel, DesErrorModel, GetCalculationListModel}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Configuration
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK, SERVICE_UNAVAILABLE}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class GetCalculationListConnectorISpec extends AnyWordSpec with WiremockSpec with Matchers{

  lazy val connector: GetCalculationListConnector = app.injector.instanceOf[GetCalculationListConnector]

  lazy val httpClient: HttpClient = app.injector.instanceOf[HttpClient]

  def appConfig(ifHost: String): BackendAppConfig = new BackendAppConfig(app.injector.instanceOf[Configuration], app.injector.instanceOf[ServicesConfig]) {
    override val ifBaseUrl: String = s"http://$ifHost:$wireMockPort"
  }

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val nino = "nino"
  val url = s"/income-tax/view/calculations/liability/23-24/$nino"

  "GetCalculationListConnector" should {

    val appConfigWithInternalHost = appConfig("localhost")
    val connector = new GetCalculationListConnector(httpClient, appConfigWithInternalHost)

    "return a success result" when {
      "IF returns a success with expected JSON" in {
        val response =
          Json.toJson(Seq(GetCalculationListModel(
            calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
            calculationTimestamp = "2019-03-17T09:22:59Z",
            calculationType = "inYear",
            requestedBy = Some("customer"),
            year = Some(2016),
            fromDate = Some("2013-05-d1"),
            toDate = Some("2016-05-d1"),
            totalIncomeTaxAndNicsDue = 500.00,
            intentToCrystallise = None,
            crystallised = None,
            crystallisationTimestamp = None
          ))).toString

        stubGetWithResponseBody(url, OK, response)
        val result = await(connector.getCalculationList(nino))

        result mustBe Right(Seq(GetCalculationListModel(
          calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          calculationTimestamp = "2019-03-17T09:22:59Z",
          calculationType = "inYear",
          requestedBy = Some("customer"),
          year = Some(2016),
          fromDate = Some("2013-05-d1"),
          toDate = Some("2016-05-d1"),
          totalIncomeTaxAndNicsDue = 500.00,
          intentToCrystallise = None,
          crystallised = None,
          crystallisationTimestamp = None
        )))
      }
    }
  }

  "return a failure result" when {
    val appConfigWithInternalHost = appConfig("localhost")
    val connector = new GetCalculationListConnector(httpClient, appConfigWithInternalHost)

    "IF returns an 503 error" in {
      val response =
        """
          |{
          |  "code": "SERVICE_UNAVAILABLE",
          |  "reason": "Dependent systems are currently not responding."
          |}
          |""".stripMargin
      stubGetWithResponseBody(url, SERVICE_UNAVAILABLE, response)

      val result = await(connector.getCalculationList(nino))

      result mustBe Left(DesErrorModel(SERVICE_UNAVAILABLE, DesErrorBodyModel("SERVICE_UNAVAILABLE", "Dependent systems are currently not responding.")))
    }


    "IF returns an 500 when parsing error occurs" in {
      val appConfigWithInternalHost = appConfig("localhost")
      val connector = new GetCalculationListConnector(httpClient, appConfigWithInternalHost)

      val response = Json.toJson(GetCalculationListModel(
        calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
        calculationTimestamp = "2019-03-17T09:22:59Z",
        calculationType = "inYear",
        requestedBy = Some("customer"),
        year = Some(2016),
        fromDate = Some("2013-05-d1"),
        toDate = Some("2016-05-d1"),
        totalIncomeTaxAndNicsDue = 500.00,
        intentToCrystallise = None,
        crystallised = None,
        crystallisationTimestamp = None
      )).toString()

      stubGetWithResponseBody(url, OK, response)

      val result = await(connector.getCalculationList(nino))

      result mustBe Left(DesErrorModel(INTERNAL_SERVER_ERROR, DesErrorBodyModel("PARSING_ERROR",
        "Error parsing response from DES - List((,List(JsonValidationError(List(error.expected.jsarray),List()))))")))
    }
  }

}
