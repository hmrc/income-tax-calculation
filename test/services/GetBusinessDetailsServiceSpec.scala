/*
 * Copyright 2023 HM Revenue & Customs
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

package services

import connectors.httpParsers.GetBusinessDetailsHttpParser.GetBusinessDetailsResponse
import connectors.GetBusinessDetailsConnector
import models.core.AccountingPeriodModel
import models.incomeSourceDetails.{BusinessDetailsModel, IncomeSourceDetailsError, IncomeSourceDetailsModel, PropertyDetailsModel}
import models.{ErrorBodyModel, ErrorModel}
import org.scalamock.handlers.CallHandler2
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND}
import testUtils.TestSuite
import uk.gov.hmrc.http.HeaderCarrier
import java.time.LocalDate

import scala.concurrent.Future

class GetBusinessDetailsServiceSpec extends TestSuite {

  val mockGetBusinessDetailsConnector: GetBusinessDetailsConnector = mock[GetBusinessDetailsConnector]

  val service = new GetBusinessDetailsService(mockGetBusinessDetailsConnector)

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

  def getBusinessDetailsSuccess: CallHandler2[String, HeaderCarrier, Future[GetBusinessDetailsResponse]] =
    (mockGetBusinessDetailsConnector.getBusinessDetails(_: String)(_: HeaderCarrier))
      .expects(*, *)
      .returning(Future.successful(Right((successModel))))

  def getBusinessDetails404: CallHandler2[String, HeaderCarrier, Future[GetBusinessDetailsResponse]] =
    (mockGetBusinessDetailsConnector.getBusinessDetails(_: String)(_: HeaderCarrier))
      .expects(*, *)
      .returning(Future.successful(Right(IncomeSourceDetailsError(NOT_FOUND,"Not found"))))

  def getBusinessDetailsFailure: CallHandler2[String, HeaderCarrier, Future[GetBusinessDetailsResponse]] =
    (mockGetBusinessDetailsConnector.getBusinessDetails(_: String)(_: HeaderCarrier))
      .expects(*, *)
      .returning(Future.successful(Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("error", "error")))))

  ".getBusinessDetails" should {

    "return a Right when successful" in {
      getBusinessDetailsSuccess

      val result = await(service.getBusinessDetails("BB123456A","12345"))

      result mustBe Right(successModel)
    }
    "return a Right when 404 and default the model" in {
      getBusinessDetails404

      val result = await(service.getBusinessDetails("BB123456A","12345"))

      result mustBe Right(IncomeSourceDetailsModel("BB123456A","12345",None,List.empty,None))
    }

    "return a Left(DesError) when calling get business details returns a DES error" in {

      getBusinessDetailsFailure

      val result = await(service.getBusinessDetails("BB123456A","12345"))

      result mustBe Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("error", "error")))
    }
  }
}
