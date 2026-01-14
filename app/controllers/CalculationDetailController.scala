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

import config.AppConfig
import controllers.predicates.AuthorisedAction
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.GetCalculationDetailsService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CalculationDetailController @Inject()(
                                             authorisedAction: AuthorisedAction,
                                             appConfig: AppConfig,
                                             cc: ControllerComponents,
                                             getCalculationDetailsService: GetCalculationDetailsService
                                           )(implicit ec: ExecutionContext) extends BackendController(cc) with Logging {

  def calculationDetail(
                         nino: String,
                         taxYear: Option[String],
                         calculationRecord: Option[String]
                       ): Action[AnyContent] = {
    authorisedAction.async { implicit user =>
      if (appConfig.useGetCalcDetailsHipPlatform5294) {
        getCalculationDetailsService
          .getCalculationDetailsHip(nino, taxYear, calculationRecord)
          .map {
            case Right(success) =>
              logger.debug(s"[CalculationDetailController][calculationDetail] - Successful Hip Response: $success")
              Ok(Json.toJson(success))
            case Left(error) =>
              logger.error(s"[CalculationDetailController][calculationDetail] - Error Hip Response: $error")
              Status(error.status)(error.toJson)
          }
      } else {
        getCalculationDetailsService
          .getCalculationDetailsLegacy(nino, taxYear, calculationRecord)
          .map {
            case Right(success) =>
              logger.debug(s"[CalculationDetailController][calculationDetail] - Successful Legacy Response: $success")
              Ok(Json.toJson(success))
            case Left(error) =>
              logger.error(s"[CalculationDetailController][calculationDetail] - Error Legacy Response: $error")
              Status(error.status)(error.toJson)
          }
      }
    }
  }

  def calculationDetailByCalcId(nino: String, calcId: String, taxYear: Option[String]): Action[AnyContent] =
    authorisedAction.async { implicit user =>
      if (appConfig.useGetCalcDetailsHipPlatform5294) {
        getCalculationDetailsService.getCalculationDetailsByCalcIdHip(nino, calcId, taxYear).map {
          case Right(successJson) =>
            logger.debug(s"[CalculationDetailController][calculationDetailByCalcId] - Successful Response: $successJson")
            Ok(Json.toJson(successJson))
          case Left(error) =>
            logger.error(s"[CalculationDetailController][calculationDetailByCalcId] - Error Response: $error")
            Status(error.status)(error.toJson)
        }
      } else {
        getCalculationDetailsService.getCalculationDetailsByCalcIdLegacy(nino, calcId, taxYear).map {
          case Right(successJson) =>
            logger.debug(s"[CalculationDetailController][calculationDetailByCalcId] - Successful Response: $successJson")
            Ok(Json.toJson(successJson))
          case Left(error) =>
            logger.error(s"[CalculationDetailController][calculationDetailByCalcId] - Error Response: $error")
            Status(error.status)(error.toJson)
        }
      }
    }
}

