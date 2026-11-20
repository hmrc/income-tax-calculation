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

import play.api.libs.json.{JsError, JsString, JsSuccess, Reads, Writes}

enum PlanType(val name: String):
  case One extends PlanType("01")
  case Two extends PlanType("02")
  case Three extends PlanType("03")
  case Four extends PlanType("04")
  case Five extends PlanType("05")

object PlanType:
  private val byString: Map[String, PlanType] =
    PlanType.values.map(ct => ct.name -> ct).toMap

  given reads: Reads[PlanType] =
    Reads {
      case JsString(value) =>
        byString.get(value)
          .map(JsSuccess(_))
          .getOrElse(JsError(s"Unknown PlanType: $value"))

      case _ =>
        JsError("planType must be a string")
    }

  given writes: Writes[PlanType] =
    Writes(t => JsString(t.name))

end PlanType