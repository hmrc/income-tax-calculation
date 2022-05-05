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

import models.incomeSourceDetails.{IncomeSourceDetailsError, IncomeSourceDetailsModel}
import play.api.libs.json.{JsValue, Json}
import play.mvc.Http.Status
import testConstants.BaseIntegrationTestConstants.testNino
import testConstants.BaseTestConstants.testMtdId
import testConstants.BusinessDetailsTestConstants.{testBusinessDetailsJson, testBusinessDetailsModel, testBusinessDetailsToJson, testMinimumBusinessDetailsJson, testMinimumBusinessDetailsModel, testMinimumBusinessDetailsToJson}
import testConstants.PropertyDetailsTestConstants.{testPropertyDetailsJson, testPropertyDetailsModel, testPropertyDetailsToJson}
import uk.gov.hmrc.http.HttpResponse

object IncomeSourceDetailsTestConstants {

  val testIncomeSourceDetailsModel = IncomeSourceDetailsModel(
    nino = testNino,
    mtdbsa = testMtdId,
    yearOfMigration = Some("2019"),
    businesses = List(testBusinessDetailsModel, testMinimumBusinessDetailsModel),
    property = Some(testPropertyDetailsModel)
  )

  val testMinimumIncomeSourceDetailsModel = IncomeSourceDetailsModel(
    nino = testNino,
    mtdbsa = testMtdId,
    yearOfMigration = None,
    businesses = List(),
    property = None
  )

  val testIncomeSourceDetailsJson: JsValue = Json.obj(
    "safeId" -> "XAIT12345678908",
    "nino" -> testNino,
    "mtdbsa" -> testMtdId,
    "yearOfMigration" -> "2019",
    "businessData" -> Json.arr(testBusinessDetailsJson, testMinimumBusinessDetailsJson),
    "propertyData" -> Json.arr(testPropertyDetailsJson)
  )

  val testIncomeSourceDetailsToJson = Json.obj(
    "nino" -> testNino,
    "mtdbsa" -> testMtdId,
    "yearOfMigration" -> "2019",
    "businesses" -> Json.arr(
      testBusinessDetailsToJson,
      testMinimumBusinessDetailsToJson),
    "property" -> testPropertyDetailsToJson
  )

  val testMinimumIncomeSourceDetailsJson = Json.obj(
    "nino" -> testNino,
    "mtdbsa" -> testMtdId
  )


  val testIncomeSourceDetailsError = IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Dummy error message")

  val successResponse = HttpResponse(Status.OK, Some(testIncomeSourceDetailsJson))
  val badJson = HttpResponse(Status.OK, Some(Json.toJson("{}")))
  val badResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, responseString = Some("Dummy error message"))
}
