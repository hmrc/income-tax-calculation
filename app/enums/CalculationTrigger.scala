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

package enums

import play.api.libs.json.*

sealed trait CalculationTrigger {
  def asString: String
}

case object Attended extends CalculationTrigger {
  val asString = "Attended"
}

case object Class2NicEvent extends CalculationTrigger {
  val asString = "Class2NicEvent"
}

case object Unattended extends CalculationTrigger {
  val asString = "Unattended"
}

case object CesaSAReturn extends CalculationTrigger {
  val asString = "CesaSAReturn"
}

object CalculationTrigger {


  val all: List[CalculationTrigger] =
    List(
      Attended,
      Class2NicEvent,
      Unattended,
      CesaSAReturn
    )

  private val byString: Map[String, CalculationTrigger] =
    all.map(t => t.asString -> t).toMap

  implicit val reads: Reads[CalculationTrigger] =
    Reads {
      case JsString(value) =>
        byString.get(value)
          .map(JsSuccess(_))
          .getOrElse(JsError(s"Unknown calculationTrigger: $value"))

      case _ =>
        JsError("calculationTrigger must be a string")
    }

  implicit val writes: Writes[CalculationTrigger] =
    Writes(t => JsString(t.asString))
}
