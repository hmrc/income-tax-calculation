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
import play.api.Logging
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.LiabilityCalculationService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext

class LiabilityCalculationController @Inject()(liabilityCalculationService: LiabilityCalculationService,
                                               cc: ControllerComponents,
                                               authorisedAction: AuthorisedAction)
                                              (implicit ec: ExecutionContext) extends BackendController(cc) with Logging {

  def calculateLiability(nino: String, taxYear: String, crystallise: Boolean): Action[AnyContent] = authorisedAction.async { implicit user =>
    liabilityCalculationService.calculateLiability(nino, taxYear, crystallise).map {
      case Right(value) => Ok(Json.toJson(value))
      case Left(error) =>
        logger.error(s"[LiabilityCalculationController][calculateLiability] - Error Response: $error, taxYear: $taxYear")
        Status(error.status)(error.toJson)
    }
  }

}
