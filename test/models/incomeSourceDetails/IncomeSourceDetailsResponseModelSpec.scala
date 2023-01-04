/*
 * Copyright 2023 HM Revenue & Customs
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

import play.api.http.Status
import play.api.libs.json._
import testConstants.IncomeSourceDetailsTestConstants.{testIncomeSourceDetailsError, testIncomeSourceDetailsJson, testIncomeSourceDetailsModel, testIncomeSourceDetailsToJson, testMinimumIncomeSourceDetailsJson, testMinimumIncomeSourceDetailsModel}
import testUtils.TestSuite

class IncomeSourceDetailsResponseModelSpec extends TestSuite {

  "The IncomeSourceDetailsResponseModel" should {
    "read from DES Json when all fields are returned" in {
      Json.fromJson(testIncomeSourceDetailsJson)(IncomeSourceDetailsModel.desReads) mustBe JsSuccess(testIncomeSourceDetailsModel)
    }
    "read from DES Json when minimum fields are returned" in {
      Json.fromJson(testMinimumIncomeSourceDetailsJson)(IncomeSourceDetailsModel.desReads) mustBe JsSuccess(testMinimumIncomeSourceDetailsModel)
    }
    "write to Json" in {
      Json.toJson(testIncomeSourceDetailsModel) mustBe testIncomeSourceDetailsToJson
    }
  }

  "The IncomeSourceDetailsErrorModel" should {
    "have the correct status code in the model" in {
      testIncomeSourceDetailsError.status mustBe Status.INTERNAL_SERVER_ERROR
    }
    "have the correct Error Message" in {
      testIncomeSourceDetailsError.reason mustBe "Dummy error message"
    }
  }
}
