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

package services

import connectors.LiabilityCalculationConnector
import connectors.httpParsers.LiabilityCalculationHttpParser.LiabilityCalculationResponse
import models.{DesErrorBodyModel, DesErrorModel, LiabilityCalculationIdModel}
import org.scalamock.handlers.CallHandler4
import testUtils.TestSuite
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future
import play.api.http.Status.INTERNAL_SERVER_ERROR

class LiabilityCalculationServiceSpec extends TestSuite {

  val mockConnector: LiabilityCalculationConnector = mock[LiabilityCalculationConnector]
  val service = new LiabilityCalculationService(mockConnector)

  def liabilityCalculationConnectorMockSuccess: CallHandler4[String, String, Boolean, HeaderCarrier, Future[LiabilityCalculationResponse]] =
    (mockConnector.calculateLiability(_: String, _: String, _: Boolean)(_: HeaderCarrier))
    .expects(*, *, *, *)
    .returning(Future.successful(Right(LiabilityCalculationIdModel("id"))))

  def liabilityCalculationConnectorMockFailure: CallHandler4[String, String, Boolean, HeaderCarrier, Future[LiabilityCalculationResponse]] =
    (mockConnector.calculateLiability(_: String, _: String, _: Boolean)(_: HeaderCarrier))
      .expects(*, *, *, *)
      .returning(Future.successful(Left(DesErrorModel(INTERNAL_SERVER_ERROR, DesErrorBodyModel("error","error")))))

  ".calculateLiability" should {

    "return a Right(LiabilityCalculationIdModel) " in {

      liabilityCalculationConnectorMockSuccess

      val result = await(service.calculateLiability("nino", "2018", crystallise = false))

      result mustBe Right(LiabilityCalculationIdModel("id"))
    }

    "return a Right(LiabilityCalculationIdModel) with a crystallisation flag" in {

      liabilityCalculationConnectorMockSuccess

      val result = await(service.calculateLiability("nino", "2018", crystallise = true))

      result mustBe Right(LiabilityCalculationIdModel("id"))
    }

    "return a Left(DesError)" in {

      liabilityCalculationConnectorMockFailure

      val result = await(service.calculateLiability("nino", "2018", crystallise = false))

      result mustBe Left(DesErrorModel(INTERNAL_SERVER_ERROR, DesErrorBodyModel("error","error")))
    }
  }

}
