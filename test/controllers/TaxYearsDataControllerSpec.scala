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

package controllers

import models.core.AccountingPeriodModel
import models.incomeSourceDetails.{BusinessDetailsModel, IncomeSourceDetailsModel, PropertyDetailsModel}
import models.mongo.TaxYearsData
import models.{DesErrorBodyModel, DesErrorModel, TaxYearsResponseData}
import org.scalamock.handlers.{CallHandler2, CallHandler3}
import play.api.http.Status
import play.api.http.Status.{BAD_REQUEST, FORBIDDEN, INTERNAL_SERVER_ERROR, SERVICE_UNAVAILABLE}
import play.api.libs.json.Json
import services.GetTaxYearsDataService
import testUtils.TestSuite
import uk.gov.hmrc.http.HeaderCarrier
import java.time.LocalDate

import scala.concurrent.Future

class TaxYearsDataControllerSpec extends TestSuite {

  val service: GetTaxYearsDataService = mock[GetTaxYearsDataService]
  val controller = new TaxYearsDataController(service, mockControllerComponents,authorisedAction)

  val nino = "BB123456A"

  val successModel = IncomeSourceDetailsModel(
    nino = nino,
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

  val taxYearsData = TaxYearsData(nino, successModel.taxYears)
  val taxYearsResponseData = TaxYearsResponseData(taxYearsData.taxYears)

  def taxYearsSuccessResponse: CallHandler3[String, String, HeaderCarrier, Future[Either[DesErrorModel, TaxYearsData]]] =
    (service.getTaxYearsData(_: String, _: String)(_: HeaderCarrier))
      .expects(nino, *, *)
      .returning(Future.successful(Right(taxYearsData)))

  def taxYearsErrorResponse(status: Int): CallHandler3[String, String, HeaderCarrier, Future[Either[DesErrorModel, TaxYearsData]]] =
    (service.getTaxYearsData(_: String, _:String)(_: HeaderCarrier))
      .expects(nino, *, *)
      .returning(Future.successful(Left(DesErrorModel(status, DesErrorBodyModel("INTERNAL_SERVER_ERROR", "internal server error")))))

  "TaxYearsController.getTaxYearsData" should {

    "return a success response with a list of tax years" in {

      mockAuth()
      taxYearsSuccessResponse

      val result = controller.getTaxYearsData(nino)(fakeRequestWithMtditid)
      status(result) mustBe Status.OK
      bodyOf(result) mustBe Json.toJson(taxYearsResponseData).toString()

    }


    "return a 500 error response with a business details error" in {

      mockAuth()
      taxYearsErrorResponse(INTERNAL_SERVER_ERROR)

      val result = controller.getTaxYearsData(nino)(fakeRequestWithMtditid)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR

    }

    "return a 503 error response with a business details error" in {

      mockAuth()
      taxYearsErrorResponse(SERVICE_UNAVAILABLE)

      val result = controller.getTaxYearsData(nino)(fakeRequestWithMtditid)
      status(result) mustBe Status.SERVICE_UNAVAILABLE

    }

    "return a 400 error response with a business details error" in {

      mockAuth()
      taxYearsErrorResponse(BAD_REQUEST)

      val result = controller.getTaxYearsData(nino)(fakeRequestWithMtditid)
      status(result) mustBe Status.BAD_REQUEST

    }

    "return a 403 error response with a business details error" in {

      mockAuth()
      taxYearsErrorResponse(FORBIDDEN)

      val result = controller.getTaxYearsData(nino)(fakeRequestWithMtditid)
      status(result) mustBe Status.FORBIDDEN

    }
  }
}
