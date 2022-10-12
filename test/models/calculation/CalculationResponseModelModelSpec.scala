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

package models.calculation

import play.api.http.Status
import play.api.libs.json._
import testConstants.GetCalculationDetailsConstants._
import testUtils.TestSuite

import java.time.LocalDate

class CalculationResponseModelModelSpec extends TestSuite {

  "LastTaxCalculationResponseMode model" when {
    "successful successModelMinimal" should {
      val taxYear = 2020
      val successModelMinimal = CalculationResponseModel(
        inputs = Inputs(personalInformation = PersonalInformation(taxRegime = "UK", class2VoluntaryContributions = None)),
        messages = None,
        calculation = None,
        metadata = Metadata(
          calculationTimestamp = Some("2019-02-15T09:35:15.094Z"),
          crystallised = Some(true),
          calculationReason = Some("customerRequest"),
          periodFrom = Some(LocalDate.of(taxYear-1,1,1)),
          periodTo = Some(LocalDate.of(taxYear,1,1)))
      )

      val expectedJson = s"""
                            |{
                            |  "inputs" : { "personalInformation" : { "taxRegime" : "UK" } },
                            |  "metadata" : {
                            |    "calculationTimestamp" : "2019-02-15T09:35:15.094Z",
                            |    "crystallised" : true,
                            |    "calculationReason": "customerRequest",
                            |    "periodFrom": "2019-01-01",
                            |    "periodTo": "2020-01-01"
                            |  }
                            |}
                            |""".stripMargin.trim

      "be translated to Json correctly" in {
        Json.toJson(successModelMinimal) mustBe Json.parse(expectedJson)
      }
      "should convert from json to model" in {
        val calcResponse = Json.fromJson[CalculationResponseModel](Json.parse(expectedJson))
        Json.toJson(calcResponse.get) mustBe Json.parse(expectedJson)
      }
    }

    "successful with zero length or null arrays" should {
      "be translated to Json correctly" in {
        Json.toJson(arrayTestFull) mustBe Json.parse(successCalcDetailsNullArraysExpectedJsonFull)
      }
      "should convert from json to model" in {
        val calcModel = Json.fromJson[CalculationResponseModel](Json.parse(successCalcDetailsNullArraysExpectedJsonFull))
        Json.toJson(calcModel.get) mustBe Json.parse(successCalcDetailsNullArraysExpectedJsonFull)
      }
    }

    "successful successModelFull" should {

      "be translated to Json correctly" in {
        Json.toJson(successModelFull) mustBe Json.parse(successCalcDetailsExpectedJsonFull)
      }

      "should convert from json to model" in {
        val calcResponse = Json.fromJson[CalculationResponseModel](Json.parse(successCalcDetailsExpectedJsonFull))
        Json.toJson(calcResponse.get) mustBe Json.parse(successCalcDetailsExpectedJsonFull)
      }
    }

    "not successful" should {
      val errorStatus = 500
      val errorMessage = "Error Message"
      val errorModel = LiabilityCalculationError(Status.INTERNAL_SERVER_ERROR, "Error Message")

      "have the correct Status (500)" in {
        errorModel.status mustBe Status.INTERNAL_SERVER_ERROR
      }
      "have the correct message" in {
        errorModel.message mustBe "Error Message"
      }
      "be translated into Json correctly" in {
        Json.prettyPrint(Json.toJson(errorModel)) mustBe
          (s"""
              |{
              |  "status" : $errorStatus,
              |  "message" : "$errorMessage"
              |}
           """.stripMargin.trim)
      }
    }
  }

}
