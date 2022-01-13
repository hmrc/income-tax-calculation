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

package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class GetCalculationListModel(calculationId: String,
                                   calculationTimestamp: String)


object GetCalculationListModel{
  implicit val writes: OWrites[GetCalculationListModel] = (
    (JsPath \ "calculationId").write[String] and
      (JsPath \ "calculationTimestamp").write[String]
    ) (unlift(GetCalculationListModel.unapply))


  implicit val reads: Reads[GetCalculationListModel] = (
    (JsPath \ "calculationId").read[String] and
      (JsPath \ "calculationTimestamp").read[String]
    ) (GetCalculationListModel.apply _)

}

case class CalculationsListResponse(calculations: Seq[GetCalculationListModel])

object CalculationsListResponse {
  implicit val writes: Writes[CalculationsListResponse] = Json.writes[CalculationsListResponse]
  implicit val reads: Reads[CalculationsListResponse] =
    implicitly[Reads[Seq[GetCalculationListModel]]].map(CalculationsListResponse(_))

}