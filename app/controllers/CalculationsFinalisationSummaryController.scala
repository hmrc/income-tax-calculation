/*
 * Copyright 2026 HM Revenue & Customs
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
import models.*
import models.hip.taxLiabilitySummaryDataSA302.TaxLiabilitySummaryDataSA302Model
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.GetTaxLiabilitySummaryDataSA302Service
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CalculationsFinalisationSummaryController @Inject()(getTaxLiabilitySummaryDataSA302Service: GetTaxLiabilitySummaryDataSA302Service,
                                                          cc: ControllerComponents,
                                                          authorisedAction: AuthorisedAction
                                           )(implicit ec: ExecutionContext) extends BackendController(cc) with Logging {

  private def isDownstreamTimeout(status: Int): Boolean = {
    status == 499 || status == 502 || status == 503
  }

  def getCalculationsFinalisationSummary(taxYear: String, taxableEntityId: String, calculationId: String): Action[AnyContent] =
    authorisedAction.async { implicit user =>
      getTaxLiabilitySummaryDataSA302Service.getTaxLiabilitySummaryDataSA302Details(taxYear, taxableEntityId, calculationId).value.map {
        case Right(success) =>
          logger.info(s"[CalculationsFinalisationSummaryController][getCalculationsFinalisationSummary] - Successful Response: OK 200 - $success")
          Ok(Json.toJson(success))
        case Left(error) if error.status == NO_CONTENT => Status(NOT_FOUND)(error.toJson)
        case Left(error) if isDownstreamTimeout(error.status) =>
          logger.warn(s"[CalculationsFinalisationSummaryController][getCalculationsFinalisationSummary] - Downstream Timeout Error Response: $error")
          Status(error.status)(error.toJson)
        case Left(error) =>
          logger.error(s"[CalculationsFinalisationSummaryController][getCalculationsFinalisationSummary] - Error Response: $error")
          Status(error.status)(error.toJson)
      }
    }
}

