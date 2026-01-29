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
import testUtils.TestSuite

class CalculationTriggerSpec extends TestSuite {

  "CalculationTrigger JSON format" should {

    "write all calculation triggers to their correct string values" in {
      CalculationTrigger.allCalculationTriggers.foreach { trigger =>
        Json.toJson(trigger) mustBe JsString(trigger.asString)
      }
    }

    "read all valid calculation trigger strings" in {
      CalculationTrigger.allCalculationTriggers.foreach { trigger =>
        Json.fromJson[CalculationTrigger](JsString(trigger.asString)) mustBe JsSuccess(trigger)
      }
    }

    "fail to read an unknown calculation trigger" in {
      val result =
        Json.fromJson[CalculationTrigger](JsString("unknownTrigger"))

      result mustBe JsError("Unknown calculationTrigger: unknownTrigger")
    }

    "fail to read a non-string JSON value" in {
      val result =
        Json.fromJson[CalculationTrigger](JsNumber(123))

      result mustBe JsError("calculationTrigger must be a string")
    }
  }
}
