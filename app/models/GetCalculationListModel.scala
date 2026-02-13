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
                                    requestedBy: Option[String],
                                    fromDate: Option[String],
                                    toDate: Option[String],
                                    calculationOutcome: Option[String] = None,
                                    calculationTrigger: Option[CalculationTrigger]
                                  )


object GetCalculationListModel {
  implicit val writes: Writes[GetCalculationListModel] = Json.writes[GetCalculationListModel]
  implicit val reads: Reads[GetCalculationListModel] =
    ((JsPath \ "calculationId").read[String] and
      (JsPath \ "calculationTimestamp").read[String] and
      (JsPath \ "calculationType").read[String] and
      (JsPath \ "requestedBy").readNullable[String] and
      (JsPath \ "fromDate").readNullable[String] and
      (JsPath \ "toDate").readNullable[String] and
      (JsPath \ "calculationOutcome").readNullable[String] and
      (JsPath \ "calculationTrigger").readNullable[CalculationTrigger]
      )(GetCalculationListModel.apply _)

}