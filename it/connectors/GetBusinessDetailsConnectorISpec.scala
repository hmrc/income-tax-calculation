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
import models.core.AccountingPeriodModel
import models.incomeSourceDetails.{BusinessDetailsModel, IncomeSourceDetailsModel, PropertyDetailsModel}
import models.{DesErrorBodyModel, DesErrorModel}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Configuration
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames, HttpClient, SessionId}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.time.LocalDate

class GetBusinessDetailsConnectorISpec extends AnyWordSpec with WiremockSpec with Matchers {

  lazy val connector: GetBusinessDetailsConnector = app.injector.instanceOf[GetBusinessDetailsConnector]

  lazy val httpClient: HttpClient = app.injector.instanceOf[HttpClient]

  def appConfig(ifHost: String): BackendAppConfig = new BackendAppConfig(app.injector.instanceOf[Configuration], app.injector.instanceOf[ServicesConfig]) {
    override val ifBaseUrl: String = s"http://$ifHost:$wireMockPort"
  }

  val successModel = IncomeSourceDetailsModel(
    nino = "BB123456A",
    mtdbsa = "XIAT0000000000A",
    yearOfMigration = Some("2019"),
    businesses = List(BusinessDetailsModel(
      incomeSourceId = "111111111111111",
      accountingPeriod = AccountingPeriodModel(
        start = LocalDate.parse("2017-06-01"),
        end = LocalDate.parse("2018-05-31")
      ),
      firstAccountingPeriodEndDate = Some(LocalDate.of(2016, 1, 1))
    ), BusinessDetailsModel(
      incomeSourceId = "111111111111111",
      accountingPeriod = AccountingPeriodModel(
        start = LocalDate.parse("2017-06-01"),
        end = LocalDate.parse("2018-05-31")
      ),
      firstAccountingPeriodEndDate = None
    )),
    property = Some(PropertyDetailsModel(
      incomeSourceId = "111111111111111",
      accountingPeriod = AccountingPeriodModel(
        start = LocalDate.parse("2017-06-01"),
        end = LocalDate.parse("2018-05-31")
      ),
      firstAccountingPeriodEndDate = Some(LocalDate.of(2016, 1, 1))
    ))
  )

  val successJson: JsValue = Json.obj(
    "safeId" -> "XAIT12345678908",
    "nino" -> "BB123456A",
    "mtdbsa" -> "XIAT0000000000A",
    "yearOfMigration" -> "2019",
    "businessData" -> Json.arr(Json.obj(
      "incomeSourceId" -> "111111111111111",
      "accountingPeriodStartDate" -> "2017-06-01",
      "accountingPeriodEndDate" -> "2018-05-31",
      "tradingName" -> "Test Business",
      "businessAddressDetails" -> Json.obj(
        "addressLine1" -> "Test Lane",
        "addressLine2" -> "Test Unit",
        "addressLine3" -> "Test Town",
        "addressLine4" -> "Test City",
        "postalCode" -> "TE5 7TE",
        "countryCode" -> "GB"
      ),
      "businessContactDetails" -> Json.obj(
        "phoneNumber" -> "01332752856",
        "mobileNumber" -> "07782565326",
        "faxNumber" -> "01332754256",
        "emailAddress" -> "stephen@manncorpone.co.uk"
      ),
      "tradingStartDate" -> "2017-01-01",
      "cashOrAccruals" -> "cash",
      "seasonal" -> true,
      "cessationDate" -> "2017-06-01",
      "cessationReason" -> "Dummy reason",
      "paperLess" -> true,
      "firstAccountingPeriodEndDate" -> "2016-01-01"
    ), Json.obj(
      "incomeSourceId" -> "111111111111111",
      "accountingPeriodStartDate" -> "2017-06-01",
      "accountingPeriodEndDate" -> "2018-05-31"
    )),
    "propertyData" -> Json.arr(Json.obj(
      "incomeSourceId" -> "111111111111111",
      "accountingPeriodStartDate" -> "2017-06-01",
      "accountingPeriodEndDate" -> "2018-05-31",
      "emailAddress" -> "stephen@manncorpone.co.uk",
      "numPropRentedUK" -> 3,
      "numPropRentedEEA" -> 2,
      "numPropRentedNONEEA" -> 1,
      "numPropRented" -> 4,
      "cessationDate" -> "2017-06-01",
      "cessationReason" -> "Dummy reason",
      "paperLess" -> true,
      "firstAccountingPeriodEndDate" -> "2016-01-01"
    ))
  )

  "GetBusinessDetailsConnector" should {

    val appConfigWithInternalHost = appConfig("localhost")
    val connector = new GetBusinessDetailsConnector(httpClient, appConfigWithInternalHost)

    val nino = "taxable_entity_id"

    val url = s"/registration/business-details/nino/$nino"

    "include internal headers" when {
      val headersSentToBenefits = Seq(
        new HttpHeader(HeaderNames.xSessionId, "sessionIdValue")
      )

      "the host for DES is 'internal'" in {
        implicit val headerCarrier: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("sessionIdValue")))

        stubGetWithResponseBody(url, OK, successJson.toString(), headersSentToBenefits)

        val result = await(connector.getBusinessDetails(nino)(headerCarrier))

        result mustBe Right(successModel)
      }
    }

    "handle errors" when {
      val desErrorBodyModel = DesErrorBodyModel("DES_CODE", "DES_REASON")

      Seq(BAD_REQUEST, NOT_FOUND, CONFLICT, INTERNAL_SERVER_ERROR, SERVICE_UNAVAILABLE).foreach { status =>
        s"DES returns $status" in {
          val desError = DesErrorModel(status, desErrorBodyModel)
          implicit val hc: HeaderCarrier = HeaderCarrier()

          stubGetWithResponseBody(url, status, desError.toJson.toString)

          val result = await(connector.getBusinessDetails(nino)(hc))

          result mustBe Left(desError)
        }
      }
      "DES returns an unexpected error - 502 BadGateway" in {
        val desError = DesErrorModel(BAD_GATEWAY, desErrorBodyModel)
        implicit val hc: HeaderCarrier = HeaderCarrier()

        stubGetWithResponseBody(url, BAD_GATEWAY, desError.toJson.toString())

        val result = await(connector.getBusinessDetails(nino)(hc))

        result mustBe Left(desError)
      }
    }
  }
}
