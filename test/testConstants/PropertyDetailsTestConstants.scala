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

package testConstants

import java.time.LocalDate
import models.incomeSourceDetails.PropertyDetailsModel
import play.api.libs.json.Json
import testConstants.AccountingPeriodTestConstants.{testAccountingPeriodModel, testAccountingPeriodToJson}

object PropertyDetailsTestConstants {

  val testPropertyDetailsModel = PropertyDetailsModel(
    incomeSourceId = "111111111111111",
    accountingPeriod = testAccountingPeriodModel,
    firstAccountingPeriodEndDate = Some(LocalDate.of(2016, 1, 1))
  )

  val testMinimumPropertyDetailsModel = PropertyDetailsModel(
    incomeSourceId = "111111111111111",
    accountingPeriod = testAccountingPeriodModel,
    firstAccountingPeriodEndDate = None
  )


  val testPropertiesRentedJson = Json.obj(
    "numPropRented" -> 4,
    "numPropRentedUK" -> 3,
    "numPropRentedEEA" -> 2,
    "numPropRentedNONEEA" -> 1
  )

  val testPropertiesRentedJsonString = Json.obj(
    "numPropRented" -> "4",
    "numPropRentedUK" -> "3",
    "numPropRentedEEA" -> "2",
    "numPropRentedNONEEA" -> "1"
  )

  val testPropertiesRentedToJson = Json.obj(
    "uk" -> 3,
    "eea" -> 2,
    "nonEea" -> 1,
    "total" -> 4
  )


  val testPropertyDetailsJson = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "accountingPeriodStartDate" -> "2017-06-01",
    "accountingPeriodEndDate" -> "2018-05-31",
    "emailAddress" -> "stephen@manncorpone.co.uk",
    "numPropRentedUK" -> 3,
    "numPropRentedEEA" -> 2,
    "numPropRentedNONEEA" -> 1,
    "numPropRented" -> 4,
    "cessationDate" -> "2017-06-01",
    "cessationReason" -> "Dummy reason",
    "paperLess" -> true,
    "firstAccountingPeriodEndDate" -> "2016-01-01"
  )

  val testPropertyDetailsJsonString = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "accountingPeriodStartDate" -> "2017-06-01",
    "accountingPeriodEndDate" -> "2018-05-31",
    "emailAddress" -> "stephen@manncorpone.co.uk",
    "numPropRentedUK" -> "3",
    "numPropRentedEEA" -> "2",
    "numPropRentedNONEEA" -> "1",
    "numPropRented" -> "4",
    "cessationDate" -> "2017-06-01",
    "cessationReason" -> "Dummy reason",
    "paperLess" -> true,
    "firstAccountingPeriodEndDate" -> "2016-01-01"
  )

  val testPropertyDetailsToJson = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "accountingPeriod" -> testAccountingPeriodToJson,
    "firstAccountingPeriodEndDate" -> "2016-01-01"
  )

  val testMinimumPropertyDetailsJson = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "accountingPeriodStartDate" -> "2017-06-01",
    "accountingPeriodEndDate" -> "2018-05-31"
  )


}
