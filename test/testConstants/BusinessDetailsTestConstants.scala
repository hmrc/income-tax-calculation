/*
 * Copyright 2022 HM Revenue & Customs
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

import models.incomeSourceDetails.BusinessDetailsModel
import play.api.libs.json.{JsObject, Json}
import testConstants.AccountingPeriodTestConstants.{testAccountingPeriodModel, testAccountingPeriodToJson}

import java.time.LocalDate

object BusinessDetailsTestConstants {

  val testBusinessDetailsModel: BusinessDetailsModel =
    BusinessDetailsModel(
      incomeSourceId = "111111111111111",
      accountingPeriod = testAccountingPeriodModel,
      firstAccountingPeriodEndDate = Some(LocalDate.of(2016, 1, 1))
    )


  val testMinimumBusinessDetailsModel: BusinessDetailsModel = BusinessDetailsModel(
    incomeSourceId = "111111111111111",
    accountingPeriod = testAccountingPeriodModel,
    firstAccountingPeriodEndDate = None
  )

  val testBusinessDetailsJson: JsObject = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "accountingPeriodStartDate" -> "2017-06-01",
    "accountingPeriodEndDate" -> "2018-05-31",
    "tradingName" -> "Test Business",
    "businessAddressDetails" -> Json.obj(
      "addressLine1" -> "Test Lane",
      "addressLine2" -> "Test Unit",
      "addressLine3" -> "Test Town",
      "addressLine4" -> "Test City",
      "postalCode" -> "TE5 7TE",
      "countryCode" -> "GB"
    ),
    "businessContactDetails" -> Json.obj(
      "phoneNumber" -> "01332752856",
      "mobileNumber" -> "07782565326",
      "faxNumber" -> "01332754256",
      "emailAddress" -> "stephen@manncorpone.co.uk"
    ),
    "tradingStartDate" -> "2017-01-01",
    "cashOrAccruals" -> "cash",
    "seasonal" -> true,
    "cessationDate" -> "2017-06-01",
    "cessationReason" -> "Dummy reason",
    "paperLess" -> true,
    "firstAccountingPeriodEndDate" -> "2016-01-01"
  )

  val testBusinessDetailsToJson: JsObject = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "accountingPeriod" -> testAccountingPeriodToJson,
    "firstAccountingPeriodEndDate" -> "2016-01-01"
  )

  val testMinimumBusinessDetailsJson: JsObject = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "accountingPeriodStartDate" -> "2017-06-01",
    "accountingPeriodEndDate" -> "2018-05-31"
  )

  val testMinimumBusinessDetailsToJson = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "accountingPeriod" -> testAccountingPeriodToJson
  )

}
