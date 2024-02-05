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

package models.mongo

import play.api.libs.json.{JsObject, Json}
import testUtils.{TestSuite, TestingClock}

class TaxYearsDataSpec extends TestSuite {

  val validJson: JsObject = Json.obj(
    "nino" -> "AA123456A",
    "taxYears" -> Seq(2016,2017,2018,2019,2020,2021,2022,2023),
    "lastUpdated" -> Json.obj(
      "$date" -> Json.obj("$numberLong" -> "1609459218000")
    )
  )

  val validModel: TaxYearsData = TaxYearsData("AA123456A",
    Seq(2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023),
    lastUpdated = TestingClock.now()
  )

  "TaxYearsData" should {

    "correctly parse from Json" in {
      validJson.as[TaxYearsData] mustBe validModel
    }

    "correctly parse to Json" in {
      Json.toJson(validModel) mustBe validJson
    }

  }

}
