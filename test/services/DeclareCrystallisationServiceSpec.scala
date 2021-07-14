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

import connectors.DeclareCrystallisationConnector
import connectors.httpParsers.DeclareCrystallisationHttpParser.DeclareCrystallisationResponse
import models.{DesErrorBodyModel, DesErrorModel}
import org.scalamock.handlers.CallHandler4
import play.api.http.Status.INTERNAL_SERVER_ERROR
import testUtils.TestSuite
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class DeclareCrystallisationServiceSpec extends TestSuite {

  val mockConnector: DeclareCrystallisationConnector = mock[DeclareCrystallisationConnector]
  val service = new DeclareCrystallisationService(mockConnector)

  val nino = "AA123456A"
  val taxYear = 2022
  val calculationId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"

  def declareCrystallisationConnectorSuccess: CallHandler4[String, Int, String, HeaderCarrier, Future[DeclareCrystallisationResponse]] =
    (mockConnector.declareCrystallisation(_: String, _: Int, _: String)(_: HeaderCarrier))
      .expects(*, *, *, *)
      .returning(Future.successful(Right(())))

  def declareCrystallisationConnectorFailure: CallHandler4[String, Int, String, HeaderCarrier, Future[DeclareCrystallisationResponse]] =
    (mockConnector.declareCrystallisation(_: String, _: Int, _: String)(_: HeaderCarrier))
      .expects(*, *, *, *)
      .returning(Future.successful(Left(DesErrorModel(INTERNAL_SERVER_ERROR, DesErrorBodyModel("error", "error")))))

  ".declareCrystallisation" should {

    "return a Right when successful" in {

      declareCrystallisationConnectorSuccess

      val result = await(service.declareCrystallisation(nino, taxYear, calculationId))

      result mustBe Right(())
    }

    "return a Left(DesError) when unsuccessful" in {

      declareCrystallisationConnectorFailure

      val result = await(service.declareCrystallisation(nino, taxYear, calculationId))

      result mustBe Left(DesErrorModel(INTERNAL_SERVER_ERROR, DesErrorBodyModel("error", "error")))
    }
  }

}
