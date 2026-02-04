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

package controllers

import controllers.predicates.AuthorisedAction
import enums.*
import models.*
import play.api.Logging
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.GetCalculationDetailsService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CalculationDetailController @Inject()(getCalculationDetailsService: GetCalculationDetailsService,
                                            cc: ControllerComponents,
                                            authorisedAction: AuthorisedAction
                                           )(implicit ec: ExecutionContext) extends BackendController(cc) with Logging {

  def calculationDetail(nino: String, taxYear: Option[String], calculationRecord: Option[String]): Action[AnyContent] =
    authorisedAction.async { implicit user =>
      getCalculationDetailsService.getCalculationDetails(nino, taxYear, calculationRecord).map {
        case Right(success) =>
          logger.info(s"[CalculationDetailController][calculationDetail] - Successful Response: OK 200 - $success")
          Ok(success)
        case Left(error) =>
          logger.error(s"[CalculationDetailController][calculationDetail] - Error Response: $error")
          Status(error.status)(error.toJson)
      }
    }

  def calculationDetailByCalcId(nino: String, calcId: String, taxYear: Option[String]): Action[AnyContent] =
    authorisedAction.async { implicit user =>
      getCalculationDetailsService.getCalculationDetailsByCalcId(nino, Some(calcId), taxYear, None).map {
        case Right(success) =>
          logger.info(s"[CalculationDetailController][calculationDetailByCalcId] - Successful Response: OK 200 - $success")
          Ok(success)
        case Left(error) =>
          logger.error(s"[CalculationDetailController][calculationDetailByCalcId] - Error Response: $error")
          Status(error.status)(error.toJson)
      }
    }


}

