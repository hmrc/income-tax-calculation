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

package models.calculation

import play.api.libs.json._

import java.time.LocalDate

sealed trait LiabilityCalculationResponseModel

case class LiabilityCalculationError(status: Int, message: String) extends LiabilityCalculationResponseModel

object LiabilityCalculationError {
  implicit val format: OFormat[LiabilityCalculationError] = Json.format[LiabilityCalculationError]
}

case class CalculationResponseModel(
                                         inputs: Inputs,
                                         metadata: Metadata,
                                         messages: Option[Messages],
                                         calculation: Option[Calculation]
                                       ) extends LiabilityCalculationResponseModel

object CalculationResponseModel {
  implicit val format: OFormat[CalculationResponseModel] = Json.format[CalculationResponseModel]
}

case class Metadata(
                     calculationTimestamp: Option[String],
                     calculationType: String,
                     crystallised: Option[Boolean],
                     calculationReason: Option[String],
                     periodFrom: Option[LocalDate],
                     periodTo: Option[LocalDate])

object Metadata {
  implicit val format: OFormat[Metadata] = Json.format[Metadata]
}

case class Inputs(personalInformation: PersonalInformation)

object Inputs {
  implicit val format: OFormat[Inputs] = Json.format[Inputs]
}

case class PersonalInformation(taxRegime: String, class2VoluntaryContributions: Option[Boolean])

object PersonalInformation {
  implicit val format: OFormat[PersonalInformation] = Json.format[PersonalInformation]
}

case class Message(id: String, text: String)

object Message {
  implicit val format: OFormat[Message] = Json.format[Message]
}

case class Messages(info: Option[Seq[Message]] = None, warnings: Option[Seq[Message]] = None, errors: Option[Seq[Message]] = None)

object Messages {
  implicit val format: OFormat[Messages] = Json.format[Messages]
}
