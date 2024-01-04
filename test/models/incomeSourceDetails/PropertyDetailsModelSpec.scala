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

package models.incomeSourceDetails

import play.api.libs.json._
import testConstants.PropertyDetailsTestConstants.{testMinimumPropertyDetailsJson, testMinimumPropertyDetailsModel, testPropertyDetailsJson, testPropertyDetailsJsonString, testPropertyDetailsModel, testPropertyDetailsToJson}
import testUtils.TestSuite

class PropertyDetailsModelSpec extends TestSuite {

  "The PropertyDetailsModel" should {

    "read from DES Json with all fields" in {
      Json.fromJson(testPropertyDetailsJson)(PropertyDetailsModel.desReads) mustBe JsSuccess(testPropertyDetailsModel)
    }

    "read from DES Json where Ints are Strings" in {
      Json.fromJson(testPropertyDetailsJsonString)(PropertyDetailsModel.desReads) mustBe JsSuccess(testPropertyDetailsModel)
    }

    "read from DES Json with minimum fields" in {
      Json.fromJson(testMinimumPropertyDetailsJson)(PropertyDetailsModel.desReads) mustBe JsSuccess(testMinimumPropertyDetailsModel)
    }

    "write to Json" in {
      Json.toJson(testPropertyDetailsModel) mustBe testPropertyDetailsToJson
    }
  }
}
