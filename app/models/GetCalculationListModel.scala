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

package models

import enums.CalculationTrigger
import play.api.libs.functional.syntax.*
import play.api.libs.json.*

case class GetCalculationListModel(
                                    calculationId: String,
                                    calculationTimestamp: String,
                                    calculationType: String,
                                    calculationOutcome: Option[String] = None,
                                    calculationTrigger: Option[CalculationTrigger],
                                    crystallised: Option[Boolean] = None) {

  def updateCalcTypeAndCrystallisedIfReq(): GetCalculationListModel = {
    calculationType match {
      case "IY" => copy(calculationType = "inYear")
      case "DF" => copy(calculationType = "crystallisation", crystallised = Some(true))
      case _ => this
    }
  }
}

case class CalculationListResponseModel(calculations: Seq[GetCalculationListModel])


object GetCalculationListModel {
  implicit val writes: Writes[GetCalculationListModel] = Json.writes[GetCalculationListModel]
  implicit val reads: Reads[GetCalculationListModel] =
    ((JsPath \ "calculationId").read[String] and
      (JsPath \ "calculationTimestamp").read[String] and
      (JsPath \ "calculationType").read[String] and
      (JsPath \ "calculationOutcome").readNullable[String] and
      (JsPath \ "calculationTrigger").readNullable[CalculationTrigger] and
      (JsPath \ "crystallised").readNullable[Boolean]
      )(GetCalculationListModel.apply _)

}

object CalculationListResponseModel {
  implicit val format: Format[CalculationListResponseModel] = Json.format[CalculationListResponseModel]
}