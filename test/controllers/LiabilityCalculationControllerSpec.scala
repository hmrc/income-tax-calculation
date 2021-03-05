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

package controllers

import connectors.httpParsers.LiabilityCalculationHttpParser.LiabilityCalculationResponse
import models.{DesErrorBodyModel, DesErrorModel, LiabilityCalculationIdModel}
import org.scalamock.handlers.CallHandler3
import play.api.http.Status
import play.api.http.Status._
import play.api.libs.json.Json
import services.LiabilityCalculationService
import testUtils.TestSuite
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class LiabilityCalculationControllerSpec extends TestSuite {

  private val service = mock[LiabilityCalculationService]
  private val controller = new LiabilityCalculationController(service, mockControllerComponents, authorisedAction)
  private val nino = "nino"
  private val taxYear = "2017-18"
  private val mtditid = "id"

  def mockServiceSuccessCall: CallHandler3[String, String, HeaderCarrier, Future[LiabilityCalculationResponse]] =
    (service.calculateLiability(_: String, _: String)(_: HeaderCarrier))
      .expects(nino, taxYear, *)
      .returning(Future.successful(Right(LiabilityCalculationIdModel("id"))))

  def mockServiceFailCall(status: Int): CallHandler3[String, String, HeaderCarrier, Future[LiabilityCalculationResponse]] =
    (service.calculateLiability(_: String, _: String)(_: HeaderCarrier))
      .expects(nino, taxYear, *)
      .returning(Future.successful(Left(DesErrorModel(status, DesErrorBodyModel("INTERNAL_SERVER_ERROR", "internal server error")))))

  "liabilityCalculation" should {

    "return 200 with a valid calculationId" when {

      "passed a valid URI" in {
        mockAuth()
        mockServiceSuccessCall

        val result = controller.calculateLiability(nino, taxYear, mtditid)(fakeRequest)
        status(result) mustBe Status.OK
        bodyOf(result) mustBe Json.toJson(LiabilityCalculationIdModel("id")).toString()
      }
    }

    "return errors" when {

      "passed a 500" in {
        mockAuth()
        mockServiceFailCall(INTERNAL_SERVER_ERROR)

        val result = controller.calculateLiability(nino, taxYear, mtditid)(fakeRequest)
        status(result) mustBe INTERNAL_SERVER_ERROR
      }

      "passed a 400" in {
        mockAuth()
        mockServiceFailCall(BAD_REQUEST)

        val result = controller.calculateLiability(nino, taxYear, mtditid)(fakeRequest)
        status(result) mustBe BAD_REQUEST
      }

      "passed a 409" in {
        mockAuth()
        mockServiceFailCall(CONFLICT)

        val result = controller.calculateLiability(nino, taxYear, mtditid)(fakeRequest)
        status(result) mustBe CONFLICT
      }

      "passed a 403" in {
        mockAuth()
        mockServiceFailCall(FORBIDDEN)

        val result = controller.calculateLiability(nino, taxYear, mtditid)(fakeRequest)
        status(result) mustBe FORBIDDEN
      }
    }
  }
}
