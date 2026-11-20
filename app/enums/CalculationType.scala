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

enum CalculationType(val name: String):
  case IF extends CalculationType("Intent to Finalise")
  case DF extends CalculationType("Declare Finalisation")
  case IA extends CalculationType("Intent to Amend")
  case CA extends CalculationType("Confirm Amendment")

object CalculationType:
  private val byString: Map[String, CalculationType] =
    CalculationType.values.map(ct => ct.name -> ct).toMap

  given reads: Reads[CalculationType] =
    Reads {
      case JsString(value) =>
        byString.get(value)
          .map(JsSuccess(_))
          .getOrElse(JsError(s"Unknown CalculationType: $value"))

      case _ =>
        JsError("calculationType must be a string")
    }

  given writes: Writes[CalculationType] =
    Writes(t => JsString(t.name))

end CalculationType