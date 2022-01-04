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

package models.LiabilityCalculation

import models.LiabilityCalculation.taxCalculation.TaxCalculation
import models.LiabilityCalculation.reliefs.Reliefs
import play.api.http.Status
import play.api.libs.json.Json
import testUtils.TestSuite

class LiabilityCalculationResponseModelSpec extends TestSuite {

  "LastTaxCalculationResponseMode model" when {
    "successful successModelMinimal" should {
      val successModelMinimal = LiabilityCalculationResponse(
        calculation = Calculation(
          allowancesAndDeductions = AllowancesAndDeductions(),
          chargeableEventGainsIncome = ChargeableEventGainsIncome(),
          dividendsIncome = DividendsIncome(),
          employmentAndPensionsIncome = EmploymentAndPensionsIncome(),
          employmentExpenses = EmploymentExpenses(),
          foreignIncome = ForeignIncome(),
          giftAid = GiftAid(),
          incomeSummaryTotals = IncomeSummaryTotals(),
          marriageAllowanceTransferredIn = MarriageAllowanceTransferredIn(),
          reliefs = Reliefs(),
          savingsAndGainsIncome = SavingsAndGainsIncome(),
          shareSchemesIncome = ShareSchemesIncome(),
          stateBenefitsIncome = StateBenefitsIncome(),
          taxCalculation = TaxCalculation()),
        metadata = Metadata(
          calculationTimestamp = None,
          crystallised = None)
      )
      val expectedJson = s"""
                            |{
                            |  "calculation" : {
                            |    "allowancesAndDeductions" : {
                            |      "marriageAllowanceTransferOut" : { }
                            |    },
                            |    "chargeableEventGainsIncome" : { },
                            |    "dividendsIncome" : { },
                            |    "employmentAndPensionsIncome" : { },
                            |    "employmentExpenses" : { },
                            |    "foreignIncome" : {
                            |      "overseasIncomeAndGains" : { }
                            |    },
                            |    "giftAid" : { },
                            |    "incomeSummaryTotals" : { },
                            |    "marriageAllowanceTransferredIn" : { },
                            |    "reliefs" : {
                            |      "reliefsClaimed" : [ { } ],
                            |      "residentialFinanceCosts" : { },
                            |      "foreignTaxCreditRelief" : { },
                            |      "topSlicingRelief" : { }
                            |    },
                            |    "savingsAndGainsIncome" : { },
                            |    "shareSchemesIncome" : { },
                            |    "stateBenefitsIncome" : { },
                            |    "taxCalculation" : {
                            |      "incomeTax" : {
                            |        "dividends" : { },
                            |        "savingsAndGains" : { }
                            |      }
                            |    }
                            |  },
                            |  "metadata" : { }
                            |}
                            |""".stripMargin.trim


      "be translated to Json correctly" in {
        Json.prettyPrint(Json.toJson(successModelMinimal)) mustBe expectedJson
      }
      "should convert from json to model" in {
//        println(Json.toJson(successModelMinimal))
        val calcResponse = Json.fromJson[LiabilityCalculationResponse](Json.toJson(successModelMinimal))
//        println(calcResponse)
        Json.prettyPrint(Json.toJson(calcResponse.get)) mustBe expectedJson
      }
    }

//    "successful successModelFull" should {
//      val successModelFull = LiabilityCalculationResponse(
//        calculation = Calculation(
//          allowancesAndDeductions = AllowancesAndDeductions(),
//          chargeableEventGainsIncome = ChargeableEventGainsIncome(),
//          dividendsIncome = DividendsIncome(),
//          employmentAndPensionsIncome = EmploymentAndPensionsIncome(),
//          employmentExpenses = EmploymentExpenses(),
//          foreignIncome = ForeignIncome(),
//          giftAid = GiftAid(),
//          incomeSummaryTotals = IncomeSummaryTotals(),
//          marriageAllowanceTransferredIn = MarriageAllowanceTransferredIn(),
//          reliefs = Reliefs(),
//          savingsAndGainsIncome = SavingsAndGainsIncome(),
//          shareSchemesIncome = ShareSchemesIncome(),
//          stateBenefitsIncome = StateBenefitsIncome(),
//          taxCalculation = TaxCalculation()),
//        metadata = Metadata(
//          calculationTimestamp = Some("2019-02-15T09:35:15.094Z"),
//          crystallised = Some(true))
//      )
//      val expectedJson = s"""
//                            |{
//                            |  "calculation" : {
//                            |    "allowancesAndDeductions" : {
//                            |      "marriageAllowanceTransferOut" : { }
//                            |    },
//                            |    "chargeableEventGainsIncome" : { },
//                            |    "dividendsIncome" : { },
//                            |    "employmentAndPensionsIncome" : { },
//                            |    "employmentExpenses" : { },
//                            |    "foreignIncome" : {
//                            |      "overseasIncomeAndGains" : { }
//                            |    },
//                            |    "giftAid" : { },
//                            |    "incomeSummaryTotals" : { },
//                            |    "marriageAllowanceTransferredIn" : { },
//                            |    "reliefs" : { },
//                            |    "savingsAndGainsIncome" : { },
//                            |    "shareSchemesIncome" : { },
//                            |    "stateBenefitsIncome" : { },
//                            |    "taxCalculation" : {
//                            |      "incomeTax" : {
//                            |        "dividends" : { },
//                            |        "savingsAndGains" : { }
//                            |      }
//                            |    }
//                            |  },
//                            |  "metadata" : {
//                            |    "calculationTimestamp" : "2019-02-15T09:35:15.094Z",
//                            |    "crystallised" : true
//                            |  }
//                            |}
//                            |""".stripMargin.trim
//
//
//      "be translated to Json correctly" in {
//        Json.prettyPrint(Json.toJson(successModelFull)) mustBe expectedJson
//      }
//      "should convert from json to model" in {
//        println(Json.toJson(successModelFull))
//        val calcResponse = Json.fromJson[LiabilityCalculationResponse](Json.toJson(successModelFull))
//        println(calcResponse)
//        Json.prettyPrint(Json.toJson(calcResponse.get)) mustBe expectedJson
//      }
//    }

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
