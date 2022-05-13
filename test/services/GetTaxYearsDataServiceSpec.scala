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

package services

import connectors.httpParsers.GetBusinessDetailsHttpParser.GetBusinessDetailsResponse
import models.core.AccountingPeriodModel
import models.incomeSourceDetails.{BusinessDetailsModel, IncomeSourceDetailsError, IncomeSourceDetailsModel, PropertyDetailsModel}
import models.mongo.{DataNotFoundError, DatabaseError, MongoError, TaxYearsData}
import models.{DesErrorBodyModel, DesErrorModel}
import org.scalamock.handlers.{CallHandler1, CallHandler2, CallHandler3}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND}
import repositories.TaxYearsDataRepository
import testUtils.{TestSuite, TestingClock}
import uk.gov.hmrc.http.HeaderCarrier
import java.time.LocalDate

import scala.concurrent.Future

class GetTaxYearsDataServiceSpec extends TestSuite {

  val mockGetBusinessDetailsService: GetBusinessDetailsService = mock[GetBusinessDetailsService]
  val mockTaxYearsDataRepository: TaxYearsDataRepository = mock[TaxYearsDataRepository]

  val service = new GetTaxYearsDataService(mockGetBusinessDetailsService, mockTaxYearsDataRepository, TestingClock)

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

  val successTaxYearsData = TaxYearsData("BB123456A",
    Seq(2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023),
    TestingClock.now())

  val desErrorModel = DesErrorModel(INTERNAL_SERVER_ERROR, DesErrorBodyModel("error", "error"))

  def getBusinessDetailsSuccess: CallHandler3[String, String, HeaderCarrier, Future[Either[DesErrorModel, IncomeSourceDetailsModel]]] =
    (mockGetBusinessDetailsService.getBusinessDetails(_: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(Future.successful(Right((successModel))))

  def getBusinessDetails404(nino: String, mtditid: String): CallHandler3[String, String, HeaderCarrier,
    Future[Either[DesErrorModel, IncomeSourceDetailsModel]]] =
    (mockGetBusinessDetailsService.getBusinessDetails(_: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(Future.successful(Right(IncomeSourceDetailsModel(nino,mtditid,None,List.empty,None))))

  def getBusinessDetailsFailure: CallHandler3[String, String, HeaderCarrier, Future[Either[DesErrorModel, IncomeSourceDetailsModel]]] =
    (mockGetBusinessDetailsService.getBusinessDetails(_: String, _: String)(_: HeaderCarrier))
      .expects(*, *, *)
      .returning(Future.successful(Left((desErrorModel))))

  def findSuccess: CallHandler1[String, Future[Either[DatabaseError, Option[TaxYearsData]]]] =
    (mockTaxYearsDataRepository.find(_: String))
      .expects(*)
      .returning(Future.successful(Right(Some(successTaxYearsData))))

  def findFailureNoTaxYearsData: CallHandler1[String, Future[Either[DatabaseError, Option[TaxYearsData]]]] =
    (mockTaxYearsDataRepository.find(_: String))
      .expects(*)
      .returning(Future.successful(Right(None)))

  def findFailureDatabaseError: CallHandler1[String, Future[Either[DatabaseError, Option[TaxYearsData]]]] =
    (mockTaxYearsDataRepository.find(_: String))
      .expects(*)
      .returning(Future.successful(Left(DataNotFoundError)))

  def createOrUpdateSuccess: CallHandler1[TaxYearsData, Future[Either[DatabaseError, Unit]]] =
    (mockTaxYearsDataRepository.createOrUpdate(_: TaxYearsData))
      .expects(*)
      .returning(Future.successful(Right()))

  def createOrUpdateFailure: CallHandler1[TaxYearsData, Future[Either[DatabaseError, Unit]]] =
    (mockTaxYearsDataRepository.createOrUpdate(_: TaxYearsData))
      .expects(*)
      .returning(Future.successful(Left(MongoError("exception"))))

  ".getTaxYearsData" should {

    "return a Right when tax years data already exists in database" in {
      findSuccess

      val result = await(service.getTaxYearsData("BB123456A","12345"))

      result mustBe Right(successTaxYearsData)
    }

    "return a Right when no tax years data is found, getting business details succeeds and storing tax years data succeeds" in {
      findFailureNoTaxYearsData

      getBusinessDetailsSuccess

      createOrUpdateSuccess

      val result = await(service.getTaxYearsData("BB123456A","12345"))

      result mustBe Right(successTaxYearsData)
    }

    "return a Right when no tax years data is found, getting business details is not found and then defaults and stores tax year data succeeds" in {
      findFailureNoTaxYearsData

      getBusinessDetails404("BB123456A","12345")

      createOrUpdateSuccess

      val result = await(service.getTaxYearsData("BB123456A","12345"))

      val currentYear = IncomeSourceDetailsModel("BB123456A","12345",None,List.empty,None).getCurrentTaxEndYear

      result mustBe Right(successTaxYearsData.copy(taxYears = Seq(currentYear)))
    }

    "return a Right when no tax years data is found, getting business details succeeds and storing tax years data fails" in {
      findFailureNoTaxYearsData

      getBusinessDetailsSuccess

      createOrUpdateFailure

      val result = await(service.getTaxYearsData("BB123456A","12345"))

      result mustBe Right(successTaxYearsData)
    }

    "return a Right when a database error occurs when retrieving tax years data, getting business details succeeds and storing tax years data succeeds" in {
      findFailureDatabaseError

      getBusinessDetailsSuccess

      createOrUpdateSuccess

      val result = await(service.getTaxYearsData("BB123456A","12345"))

      result mustBe Right(successTaxYearsData)
    }

    "return a Right when a database error occurs when retrieving tax years data, getting business details succeeds and storing tax years data fails" in {
      findFailureDatabaseError

      getBusinessDetailsSuccess

      createOrUpdateFailure

      val result = await(service.getTaxYearsData("BB123456A","12345"))

      result mustBe Right(successTaxYearsData)
    }

    "return a Left when no tax years data is found and getting business details fails due to error with DES" in {
      findFailureNoTaxYearsData

      getBusinessDetailsFailure

      val result = await(service.getTaxYearsData("BB123456A","12345"))

      result mustBe Left(DesErrorModel(INTERNAL_SERVER_ERROR, DesErrorBodyModel("error", "error")))
    }

    "return a Left when a database error occurs when retrieving tax years data and getting business details fails due to error with DES" in {
      findFailureDatabaseError

      getBusinessDetailsFailure

      val result = await(service.getTaxYearsData("BB123456A","12345"))

      result mustBe Left(DesErrorModel(INTERNAL_SERVER_ERROR, DesErrorBodyModel("error", "error")))
    }
  }
}
