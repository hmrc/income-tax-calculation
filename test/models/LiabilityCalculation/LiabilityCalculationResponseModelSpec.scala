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

import models.LiabilityCalculation.taxCalculation._
import models.LiabilityCalculation.taxDeductedAtSource.TaxDeductedAtSource
import play.api.http.Status
import play.api.libs.json._
import testUtils.TestSuite

import scala.io.Source

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
          taxCalculation = TaxCalculation(),
          taxDeductedAtSource = TaxDeductedAtSource()
        ),
        metadata = Metadata(
          calculationTimestamp = None,
          crystallised = None)
      )
      val expectedJson = s"""
                            |{
                            |  "metadata" : { },
                            |  "calculation" : {
                            |    "allowancesAndDeductions" : {
                            |      "marriageAllowanceTransferOut" : { }
                            |    },
                            |    "giftAid" : { },
                            |    "reliefs" : {
                            |      "residentialFinanceCosts" : { },
                            |      "reliefsClaimed" : [ { } ],
                            |      "foreignTaxCreditRelief" : { },
                            |      "topSlicingRelief" : { }
                            |    },
                            |    "taxDeductedAtSource" : { },
                            |    "marriageAllowanceTransferredIn" : { },
                            |    "employmentAndPensionsIncome" : { },
                            |    "employmentExpenses" : { },
                            |    "stateBenefitsIncome" : { },
                            |    "shareSchemesIncome" : { },
                            |    "foreignIncome" : {
                            |      "overseasIncomeAndGains" : { }
                            |    },
                            |    "chargeableEventGainsIncome" : { },
                            |    "savingsAndGainsIncome" : { },
                            |    "dividendsIncome" : { },
                            |    "incomeSummaryTotals" : { },
                            |    "taxCalculation" : {
                            |      "incomeTax" : {
                            |        "payPensionsProfit" : {
                            |          "taxBands" :[]
                            |        },
                            |        "savingsAndGains" : {
                            |          "taxBands" : [ ]
                            |        },
                            |        "lumpSums" : {
                            |          "taxBands" : [ ]
                            |        },
                            |        "dividends" : {
                            |          "taxBands" : [ ]
                            |        },
                            |        "gainsOnLifePolicies" : {
                            |          "taxBands" : [ ]
                            |        }
                            |      },
                            |      "nics" : {
                            |        "class4Nics" : {
                            |          "nic4Bands" : [ ]
                            |        },
                            |        "class2Nics" : { }
                            |      },
                            |      "capitalGainsTax" : {
                            |        "residentialPropertyAndCarriedInterest" : {
                            |          "cgtTaxBands" : [ ]
                            |        },
                            |        "businessAssetsDisposalsAndInvestorsRel" : { }
                            |      }
                            |    }
                            |  }
                            |}
                            |""".stripMargin.trim


      "be translated to Json correctly" in {
        Json.toJson(successModelMinimal) mustBe Json.parse(expectedJson)
      }
      "should convert from json to model" in {
        val calcResponse = Json.fromJson[LiabilityCalculationResponse](Json.parse(expectedJson))
        Json.toJson(calcResponse.get) mustBe Json.parse(expectedJson)
      }
    }

    "successful successModelFull" should {
      val successModelFull = LiabilityCalculationResponse(
        calculation = Calculation(
          allowancesAndDeductions = AllowancesAndDeductions(
            personalAllowance = Some(12500),
            reducedPersonalAllowance = Some(12500),
            marriageAllowanceTransferOut = MarriageAllowanceTransferOut(
              personalAllowanceBeforeTransferOut = Some(5000.99),
              transferredOutAmount = Some(5000.99)),
            pensionContributions = Some(5000.99),
            lossesAppliedToGeneralIncome = Some(12500),
            giftOfInvestmentsAndPropertyToCharity = Some(12500),
            grossAnnuityPayments = Some(5000.99),
            qualifyingLoanInterestFromInvestments = Some(5000.99),
            postCessationTradeReceipts = Some(5000.99),
            paymentsToTradeUnionsForDeathBenefits = Some(5000.99)),
          chargeableEventGainsIncome = ChargeableEventGainsIncome(
            totalOfAllGains = Some(12500)
          ),
          dividendsIncome = DividendsIncome(chargeableForeignDividends = Some(12500)),
          employmentAndPensionsIncome = EmploymentAndPensionsIncome(
            totalPayeEmploymentAndLumpSumIncome = Some(5000.99),
            totalBenefitsInKind = Some(5000.99),
            totalOccupationalPensionIncome = Some(5000.99)
          ),
          employmentExpenses = EmploymentExpenses(totalEmploymentExpenses = Some(5000.99)),
          foreignIncome = ForeignIncome(
            chargeableOverseasPensionsStateBenefitsRoyalties = Some(5000.99),
            chargeableAllOtherIncomeReceivedWhilstAbroad = Some(5000.99),
            overseasIncomeAndGains = OverseasIncomeAndGains(gainAmount = Some(5000.99)),
            totalForeignBenefitsAndGifts = Some(5000.99)
          ),
          giftAid = GiftAid(
            grossGiftAidPayments = Some(12500),
            giftAidTax = Some(5000.99)
          ),
          incomeSummaryTotals = IncomeSummaryTotals(
            totalSelfEmploymentProfit = Some(12500),
            totalPropertyProfit = Some(12500),
            totalFHLPropertyProfit = Some(12500),
            totalForeignPropertyProfit = Some(12500),
            totalEeaFhlProfit = Some(12500)
          ),
          marriageAllowanceTransferredIn = MarriageAllowanceTransferredIn(amount = Some(5000.99)),
          reliefs = Reliefs(reliefsClaimed = Seq(ReliefsClaimed(
            `type` = Some("vctSubscriptions"),
            amountUsed = Some(5000.99)),
            ReliefsClaimed(
              `type` = Some("vctSubscriptions2"),
              amountUsed = Some(5000.99)),
          ),
            residentialFinanceCosts = ResidentialFinanceCosts(totalResidentialFinanceCostsRelief = Some(5000.99)),
            foreignTaxCreditRelief = ForeignTaxCreditRelief(totalForeignTaxCreditRelief = Some(5000.99)),
            topSlicingRelief = TopSlicingRelief(amount = Some(5000.99))),
          savingsAndGainsIncome = SavingsAndGainsIncome(
            chargeableForeignSavingsAndGains = Some(12500)
          ),
          shareSchemesIncome = ShareSchemesIncome(
            totalIncome = Some(5000.99)
          ),
          stateBenefitsIncome = StateBenefitsIncome(totalStateBenefitsIncome = Some(5000.99)),
          taxCalculation = TaxCalculation(
            incomeTax = IncomeTax(
              totalIncomeReceivedFromAllSources = Some(12500),
              totalAllowancesAndDeductions = Some(12500),
              totalTaxableIncome = Some(12500),
              payPensionsProfit = PayPensionsProfit(
                taxBands = Seq(TaxBands(
                  name = Some("SSR"),
                  rate = Some(20),
                  bandLimit = Some(12500),
                  apportionedBandLimit = Some(12500),
                  income = Some(12500),
                  taxAmount = Some(5000.99)
                ))
              ),
              savingsAndGains = models.LiabilityCalculation.taxCalculation.SavingsAndGains(
                taxableIncome = Some(12500),
                taxBands = Seq(TaxBands(
                  name = Some("SSR"),
                  rate = Some(20),
                  bandLimit = Some(12500),
                  apportionedBandLimit = Some(12500),
                  income = Some(12500),
                  taxAmount = Some(5000.99)
                ))
              ),
              dividends = Dividends(
                taxableIncome = Some(12500),
                taxBands = Seq(TaxBands(
                  name = Some("SSR"),
                  rate = Some(20),
                  bandLimit = Some(12500),
                  apportionedBandLimit = Some(12500),
                  income = Some(12500),
                  taxAmount = Some(5000.99)
                ))
              ),
              lumpSums = LumpSums(
                taxBands = Seq(TaxBands(
                  name = Some("SSR"),
                  rate = Some(20),
                  bandLimit = Some(12500),
                  apportionedBandLimit = Some(12500),
                  income = Some(12500),
                  taxAmount = Some(5000.99)
                ))
              ),
              gainsOnLifePolicies = GainsOnLifePolicies(
                taxBands = Seq(TaxBands(
                  name = Some("SSR"),
                  rate = Some(20),
                  bandLimit = Some(12500),
                  apportionedBandLimit = Some(12500),
                  income = Some(12500),
                  taxAmount = Some(5000.99)
                ))
              ),
              totalReliefs = Some(5000.99),
              totalNotionalTax = Some(5000.99),
              incomeTaxDueAfterTaxReductions = Some(5000.99),
              totalPensionSavingsTaxCharges = Some(5000.99),
              statePensionLumpSumCharges = Some(5000.99),
              payeUnderpaymentsCodedOut = Some(5000.99)
            ),
            nics = Nics(
              class4Nics = Class4Nics(nic4Bands = Seq(Nic4Bands(
                name = Some("ZRT"),
                income = Some(12500),
                rate = Some(20),
                amount = Some(5000.99)
              ))),
              class2Nics = Class2Nics(amount = Some(5000.99))
            ),
            capitalGainsTax = CapitalGainsTax(
              totalCapitalGainsIncome = Some(5000.99),
              adjustments = Some(-99999999999.99),
              foreignTaxCreditRelief = Some(5000.99),
              taxOnGainsAlreadyPaid = Some(5000.99),
              capitalGainsTaxDue = Some(5000.99),
              capitalGainsOverpaid = Some(5000.99),
              residentialPropertyAndCarriedInterest = ResidentialPropertyAndCarriedInterest(
                cgtTaxBands = Seq(CgtTaxBands(
                  name = Some("lowerRate"),
                  rate = Some(20),
                  income = Some(5000.99),
                  taxAmount = Some(5000.99)
                ),
                  CgtTaxBands(
                    name = Some("lowerRate2"),
                    rate = Some(21),
                    income = Some(5000.99),
                    taxAmount = Some(5000.99)
                  ))
              ),
              businessAssetsDisposalsAndInvestorsRel = BusinessAssetsDisposalsAndInvestorsRel(
                taxableGains = Some(5000.99),
                rate = Some(20),
                taxAmount = Some(5000.99)
              )
            ),
            totalStudentLoansRepaymentAmount = Some(5000.99),
            saUnderpaymentsCodedOut = Some(-99999999999.99),
            totalIncomeTaxAndNicsDue = Some(-99999999999.99),
            totalTaxDeducted = Some(-99999999999.99)
          ),
          taxDeductedAtSource = TaxDeductedAtSource(
            ukLandAndProperty = Some(5000.99),
            bbsi = Some(5000.99),
            cis = Some(5000.99),
            voidedIsa = Some(5000.99),
            payeEmployments = Some(5000.99),
            occupationalPensions = Some(5000.99),
            stateBenefits = Some(-99999999999.99),
            specialWithholdingTaxOrUkTaxPaid = Some(5000.99),
            inYearAdjustmentCodedInLaterTaxYear = Some(5000.99),
          )),
        metadata = Metadata(
          calculationTimestamp = Some("2019-02-15T09:35:15.094Z"),
          crystallised = Some(true))
      )

      val source = Source.fromURL(getClass.getResource("/liabilityResponsePruned.json"))
      val expectedJsonPruned = try source.mkString finally source.close()

      "be translated to Json correctly" in {
        Json.toJson(successModelFull) mustBe Json.parse(expectedJsonPruned)
      }

      "should convert from json to model" in {
        val calcResponse = Json.fromJson[LiabilityCalculationResponse](Json.parse(expectedJsonPruned))
        Json.toJson(calcResponse.get) mustBe Json.parse(expectedJsonPruned)
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
