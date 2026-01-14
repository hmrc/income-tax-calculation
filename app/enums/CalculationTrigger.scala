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

sealed trait CalculationTrigger2083 extends CalculationTrigger

sealed trait CalculationTrigger2150 extends CalculationTrigger

case object Attended2083 extends CalculationTrigger2083 {
  val asString = "attended"
}

case object Class2NicEvent2083 extends CalculationTrigger2083 {
  val asString = "class2NICEvent"
}

case object Unattended2083 extends CalculationTrigger2083 {
  val asString = "unattended"
}

case object CesaSAReturn2083 extends CalculationTrigger2083 {
  val asString = "cesaSAReturn"
}

case object NewLossEvent2083 extends CalculationTrigger2083 {
  val asString = "newLossEvent"
}

case object UpdatedLossEvent2083 extends CalculationTrigger2083 {
  val asString = "updatedLossEvent"
}


case object Attended2150 extends CalculationTrigger2150 {
  val asString = "Attended"
}

case object Class2NicEvent2150 extends CalculationTrigger2150 {
  val asString = "Class2NicEvent"
}

case object Unattended2150 extends CalculationTrigger2150 {
  val asString = "Unattended"
}

case object CesaSAReturn2150 extends CalculationTrigger2150 {
  val asString = "CesaSAReturn"
}

object CalculationTrigger {


  val all: List[CalculationTrigger] =
    List(
      Attended2083,
      Class2NicEvent2083,
      Unattended2083,
      CesaSAReturn2083,
      NewLossEvent2083,
      UpdatedLossEvent2083,
      Attended2150,
      Class2NicEvent2150,
      Unattended2150,
      CesaSAReturn2150
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
