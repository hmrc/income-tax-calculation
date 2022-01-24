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

package assets

import models.liabilitycalculation._
import models.liabilitycalculation.taxcalculation._

object GetCalculationDetailsConstants {

  val successModelFull = LiabilityCalculationResponse(
    inputs = Inputs(personalInformation = PersonalInformation(
      taxRegime = "UK", class2VoluntaryContributions = Some(true)
    )),
    messages = Some(Messages(
      info = Some(Seq(Message(id = "infoId1", text = "info msg text1"))),
      warnings = Some(Seq(Message(id = "warnId1", text = "warn msg text1"))),
      errors = Some(Seq(Message(id = "errorId1", text = "error msg text1")))
    )),
    calculation = Some(Calculation(
      allowancesAndDeductions = Some(AllowancesAndDeductions(
        personalAllowance = Some(12500),
        reducedPersonalAllowance = Some(12500),
        marriageAllowanceTransferOut = Some(MarriageAllowanceTransferOut(
          personalAllowanceBeforeTransferOut = 5000.99,
          transferredOutAmount = 5000.99)),
        pensionContributions = Some(5000.99),
        lossesAppliedToGeneralIncome = Some(12500),
        giftOfInvestmentsAndPropertyToCharity = Some(12500),
        grossAnnuityPayments = Some(5000.99),
        qualifyingLoanInterestFromInvestments = Some(5000.99),
        postCessationTradeReceipts = Some(5000.99),
        paymentsToTradeUnionsForDeathBenefits = Some(5000.99))),
      chargeableEventGainsIncome = Some(ChargeableEventGainsIncome(
        totalOfAllGains = 12500
      )),
      dividendsIncome = Some(DividendsIncome(chargeableForeignDividends = Some(12500))),
      employmentAndPensionsIncome = Some(EmploymentAndPensionsIncome(
        totalPayeEmploymentAndLumpSumIncome = Some(5000.99),
        totalBenefitsInKind = Some(5000.99),
        totalOccupationalPensionIncome = Some(5000.99)
      )),
      employmentExpenses = Some(EmploymentExpenses(totalEmploymentExpenses = Some(5000.99))),
      foreignIncome = Some(ForeignIncome(
        chargeableOverseasPensionsStateBenefitsRoyalties = Some(5000.99),
        chargeableAllOtherIncomeReceivedWhilstAbroad = Some(5000.99),
        overseasIncomeAndGains = Some(OverseasIncomeAndGains(gainAmount = 5000.99)),
        totalForeignBenefitsAndGifts = Some(5000.99)
      )),
      giftAid = Some(GiftAid(
        grossGiftAidPayments = 12500,
        giftAidTax = 5000.99
      )),
      incomeSummaryTotals = Some(IncomeSummaryTotals(
        totalSelfEmploymentProfit = Some(12500),
        totalPropertyProfit = Some(12500),
        totalFHLPropertyProfit = Some(12500),
        totalForeignPropertyProfit = Some(12500),
        totalEeaFhlProfit = Some(12500)
      )),
      marriageAllowanceTransferredIn = Some(MarriageAllowanceTransferredIn(amount = Some(5000.99))),
      reliefs = Some(Reliefs(reliefsClaimed = Seq(ReliefsClaimed(
        `type` = "vctSubscriptions",
        amountUsed = Some(5000.99)),
        ReliefsClaimed(
          `type` = "vctSubscriptions2",
          amountUsed = Some(5000.99)),
      ),
        residentialFinanceCosts = Some(ResidentialFinanceCosts(totalResidentialFinanceCostsRelief = 5000.99)),
        foreignTaxCreditRelief = Some(ForeignTaxCreditRelief(totalForeignTaxCreditRelief = 5000.99)),
        topSlicingRelief = Some(TopSlicingRelief(amount = Some(5000.99))))),
      savingsAndGainsIncome = Some(SavingsAndGainsIncome(
        chargeableForeignSavingsAndGains = Some(12500)
      )),
      shareSchemesIncome = Some(ShareSchemesIncome(
        totalIncome = 5000.99
      )),
      stateBenefitsIncome = Some(StateBenefitsIncome(totalStateBenefitsIncome = Some(5000.99))),
      taxCalculation = Some(TaxCalculation(
        incomeTax = IncomeTax(
          totalIncomeReceivedFromAllSources = 12500,
          totalAllowancesAndDeductions = 12500,
          totalTaxableIncome = 12500,
          payPensionsProfit = Some(PayPensionsProfit(
            taxBands = Seq(TaxBands(
              name = "SSR",
              rate = 20,
              bandLimit = 12500,
              apportionedBandLimit = 12500,
              income = 12500,
              taxAmount = 5000.99
            ))
          )),
          savingsAndGains = Some(SavingsAndGains(
            taxableIncome = 12500,
            taxBands = Seq(TaxBands(
              name = "SSR",
              rate = 20,
              bandLimit = 12500,
              apportionedBandLimit = 12500,
              income = 12500,
              taxAmount = 5000.99
            ))
          )),
          dividends = Some(Dividends(
            taxableIncome = 12500,
            taxBands = Seq(TaxBands(
              name = "SSR",
              rate = 20,
              bandLimit = 12500,
              apportionedBandLimit = 12500,
              income = 12500,
              taxAmount = 5000.99
            ))
          )),
          lumpSums = Some(LumpSums(
            taxBands = Seq(TaxBands(
              name = "SSR",
              rate = 20,
              bandLimit = 12500,
              apportionedBandLimit = 12500,
              income = 12500,
              taxAmount = 5000.99
            ))
          )),
          gainsOnLifePolicies = Some(GainsOnLifePolicies(
            taxBands = Seq(TaxBands(
              name = "SSR",
              rate = 20,
              bandLimit = 12500,
              apportionedBandLimit = 12500,
              income = 12500,
              taxAmount = 5000.99
            ))
          )),
          totalReliefs = Some(5000.99),
          totalNotionalTax = Some(5000.99),
          incomeTaxDueAfterTaxReductions = Some(5000.99),
          totalPensionSavingsTaxCharges = Some(5000.99),
          statePensionLumpSumCharges = Some(5000.99),
          payeUnderpaymentsCodedOut = Some(5000.99)
        ),
        nics = Some(Nics(
          class4Nics = Some(Class4Nics(nic4Bands = Seq(Nic4Bands(
            name = "ZRT",
            income = 12500,
            rate = 20,
            amount = 5000.99
          )))),
          class2Nics = Some(Class2Nics(amount = Some(5000.99)))
        )),
        capitalGainsTax = Some(CapitalGainsTax(
          totalCapitalGainsIncome = 5000.99,
          adjustments = Some(-99999999999.99),
          foreignTaxCreditRelief = Some(5000.99),
          taxOnGainsAlreadyPaid = Some(5000.99),
          capitalGainsTaxDue = 5000.99,
          capitalGainsOverpaid = Some(5000.99),
          residentialPropertyAndCarriedInterest = Some(ResidentialPropertyAndCarriedInterest(
            cgtTaxBands = Seq(CgtTaxBands(
              name = "lowerRate",
              rate = 20,
              income = 5000.99,
              taxAmount = 5000.99
            ),
              CgtTaxBands(
                name = "lowerRate2",
                rate = 21,
                income = 5000.99,
                taxAmount = 5000.99
              ))
          )),
          businessAssetsDisposalsAndInvestorsRel = Some(BusinessAssetsDisposalsAndInvestorsRel(
            taxableGains = Some(5000.99),
            rate = Some(20),
            taxAmount = Some(5000.99)
          ))
        )),
        totalStudentLoansRepaymentAmount = Some(5000.99),
        saUnderpaymentsCodedOut = Some(-99999999999.99),
        totalIncomeTaxAndNicsDue = -99999999999.99,
        totalTaxDeducted = Some(-99999999999.99)
      )),
      taxDeductedAtSource = Some(TaxDeductedAtSource(
        ukLandAndProperty = Some(5000.99),
        bbsi = Some(5000.99),
        cis = Some(5000.99),
        voidedIsa = Some(5000.99),
        payeEmployments = Some(5000.99),
        occupationalPensions = Some(5000.99),
        stateBenefits = Some(-99999999999.99),
        specialWithholdingTaxOrUkTaxPaid = Some(5000.99),
        inYearAdjustmentCodedInLaterTaxYear = Some(5000.99),
      )))),
    metadata = Metadata(
      calculationTimestamp = "2019-02-15T09:35:15.094Z",
      crystallised = true)
  )

  val successExpectedJsonFull = s"""
                                   |{
                                   |  "inputs": {
                                   |    "personalInformation": {
                                   |      "taxRegime": "UK",
                                   |      "class2VoluntaryContributions": true
                                   |    }
                                   |  },
                                   |  "messages": {
                                   |    "info": [
                                   |      {
                                   |        "id": "infoId1",
                                   |        "text": "info msg text1"
                                   |      }
                                   |    ],
                                   |    "warnings": [
                                   |      {
                                   |        "id": "warnId1",
                                   |        "text": "warn msg text1"
                                   |      }
                                   |    ],
                                   |    "errors": [
                                   |      {
                                   |        "id": "errorId1",
                                   |        "text": "error msg text1"
                                   |      }
                                   |    ]
                                   |  },
                                   |  "metadata" : {
                                   |    "calculationTimestamp" : "2019-02-15T09:35:15.094Z",
                                   |    "crystallised" : true
                                   |  },
                                   |  "calculation" : {
                                   |    "allowancesAndDeductions" : {
                                   |      "personalAllowance" : 12500,
                                   |      "marriageAllowanceTransferOut" : {
                                   |        "personalAllowanceBeforeTransferOut" : 5000.99,
                                   |        "transferredOutAmount" : 5000.99
                                   |      },
                                   |      "reducedPersonalAllowance" : 12500,
                                   |      "giftOfInvestmentsAndPropertyToCharity" : 12500,
                                   |      "lossesAppliedToGeneralIncome" : 12500,
                                   |      "qualifyingLoanInterestFromInvestments" : 5000.99,
                                   |      "post-cessationTradeReceipts" : 5000.99,
                                   |      "paymentsToTradeUnionsForDeathBenefits" : 5000.99,
                                   |      "grossAnnuityPayments" : 5000.99,
                                   |      "pensionContributions" : 5000.99
                                   |    },
                                   |    "reliefs" : {
                                   |      "residentialFinanceCosts" : {
                                   |        "totalResidentialFinanceCostsRelief" : 5000.99
                                   |      },
                                   |      "reliefsClaimed" : [ {
                                   |        "type" : "vctSubscriptions",
                                   |        "amountUsed" : 5000.99
                                   |      }, {
                                   |        "type" : "vctSubscriptions2",
                                   |        "amountUsed" : 5000.99
                                   |      } ],
                                   |      "foreignTaxCreditRelief" : {
                                   |        "totalForeignTaxCreditRelief" : 5000.99
                                   |      },
                                   |      "topSlicingRelief" : {
                                   |        "amount" : 5000.99
                                   |      }
                                   |    },
                                   |    "taxDeductedAtSource" : {
                                   |      "bbsi" : 5000.99,
                                   |      "ukLandAndProperty" : 5000.99,
                                   |      "cis" : 5000.99,
                                   |      "voidedIsa" : 5000.99,
                                   |      "payeEmployments" : 5000.99,
                                   |      "occupationalPensions" : 5000.99,
                                   |      "stateBenefits" : -99999999999.99,
                                   |      "specialWithholdingTaxOrUkTaxPaid" : 5000.99,
                                   |      "inYearAdjustmentCodedInLaterTaxYear" : 5000.99
                                   |    },
                                   |    "giftAid" : {
                                   |      "grossGiftAidPayments" : 12500,
                                   |      "giftAidTax" : 5000.99
                                   |    },
                                   |    "marriageAllowanceTransferredIn" : {
                                   |      "amount" : 5000.99
                                   |    },
                                   |    "employmentAndPensionsIncome" : {
                                   |      "totalPayeEmploymentAndLumpSumIncome" : 5000.99,
                                   |      "totalOccupationalPensionIncome" : 5000.99,
                                   |      "totalBenefitsInKind" : 5000.99
                                   |    },
                                   |    "employmentExpenses" : {
                                   |      "totalEmploymentExpenses" : 5000.99
                                   |    },
                                   |    "stateBenefitsIncome" : {
                                   |      "totalStateBenefitsIncome" : 5000.99
                                   |    },
                                   |    "shareSchemesIncome" : {
                                   |      "totalIncome" : 5000.99
                                   |    },
                                   |    "foreignIncome" : {
                                   |      "chargeableOverseasPensionsStateBenefitsRoyalties" : 5000.99,
                                   |      "chargeableAllOtherIncomeReceivedWhilstAbroad" : 5000.99,
                                   |      "overseasIncomeAndGains" : {
                                   |        "gainAmount" : 5000.99
                                   |      },
                                   |      "totalForeignBenefitsAndGifts" : 5000.99
                                   |    },
                                   |    "chargeableEventGainsIncome" : {
                                   |      "totalOfAllGains" : 12500
                                   |    },
                                   |    "savingsAndGainsIncome" : {
                                   |      "chargeableForeignSavingsAndGains" : 12500
                                   |    },
                                   |    "dividendsIncome" : {
                                   |      "chargeableForeignDividends" : 12500
                                   |    },
                                   |    "incomeSummaryTotals" : {
                                   |      "totalSelfEmploymentProfit" : 12500,
                                   |      "totalPropertyProfit" : 12500,
                                   |      "totalFHLPropertyProfit" : 12500,
                                   |      "totalForeignPropertyProfit" : 12500,
                                   |      "totalEeaFhlProfit" : 12500
                                   |    },
                                   |    "taxCalculation" : {
                                   |      "incomeTax" : {
                                   |        "totalIncomeReceivedFromAllSources" : 12500,
                                   |        "totalAllowancesAndDeductions" : 12500,
                                   |        "totalTaxableIncome" : 12500,
                                   |        "payPensionsProfit" : {
                                   |          "taxBands" : [
                                   |            {
                                   |              "name" : "SSR",
                                   |              "rate" : 20,
                                   |              "bandLimit" : 12500,
                                   |              "apportionedBandLimit" : 12500,
                                   |              "income" : 12500,
                                   |              "taxAmount" : 5000.99
                                   |            }
                                   |          ]
                                   |        },
                                   |        "savingsAndGains" : {
                                   |          "taxableIncome" : 12500,
                                   |          "taxBands" : [
                                   |            {
                                   |              "name" : "SSR",
                                   |              "rate" : 20,
                                   |              "bandLimit" : 12500,
                                   |              "apportionedBandLimit" : 12500,
                                   |              "income" : 12500,
                                   |              "taxAmount" : 5000.99
                                   |            }
                                   |          ]
                                   |        },
                                   |        "dividends" : {
                                   |          "taxableIncome" : 12500,
                                   |          "taxBands" : [
                                   |            {
                                   |              "name" : "SSR",
                                   |              "rate" : 20,
                                   |              "bandLimit" : 12500,
                                   |              "apportionedBandLimit" : 12500,
                                   |              "income" : 12500,
                                   |              "taxAmount" : 5000.99
                                   |            }
                                   |          ]
                                   |        },
                                   |        "lumpSums" : {
                                   |          "taxBands" : [
                                   |            {
                                   |              "name" : "SSR",
                                   |              "rate" : 20,
                                   |              "bandLimit" : 12500,
                                   |              "apportionedBandLimit" : 12500,
                                   |              "income" : 12500,
                                   |              "taxAmount" : 5000.99
                                   |            }
                                   |          ]
                                   |        },
                                   |        "gainsOnLifePolicies" : {
                                   |          "taxBands" : [
                                   |            {
                                   |              "name" : "SSR",
                                   |              "rate" : 20,
                                   |              "bandLimit" : 12500,
                                   |              "apportionedBandLimit" : 12500,
                                   |              "income" : 12500,
                                   |              "taxAmount" : 5000.99
                                   |            }
                                   |          ]
                                   |        },
                                   |        "totalReliefs" : 5000.99,
                                   |        "totalNotionalTax" : 5000.99,
                                   |        "incomeTaxDueAfterTaxReductions" : 5000.99,
                                   |        "totalPensionSavingsTaxCharges" : 5000.99,
                                   |        "statePensionLumpSumCharges" : 5000.99,
                                   |        "payeUnderpaymentsCodedOut" : 5000.99
                                   |      },
                                   |      "nics" : {
                                   |        "class2Nics" : {
                                   |          "amount" : 5000.99
                                   |        },
                                   |        "class4Nics" : {
                                   |          "nic4Bands" : [
                                   |            {
                                   |              "name" : "ZRT",
                                   |              "rate" : 20,
                                   |              "income" : 12500,
                                   |              "amount" : 5000.99
                                   |            }
                                   |          ]
                                   |        }
                                   |      },
                                   |      "saUnderpaymentsCodedOut" : -99999999999.99,
                                   |      "totalStudentLoansRepaymentAmount" : 5000.99,
                                   |      "totalTaxDeducted" : -99999999999.99,
                                   |      "totalIncomeTaxAndNicsDue" : -99999999999.99,
                                   |      "capitalGainsTax" : {
                                   |        "totalCapitalGainsIncome" : 5000.99,
                                   |        "businessAssetsDisposalsAndInvestorsRel" : {
                                   |          "taxableGains" : 5000.99,
                                   |          "rate" : 20,
                                   |          "taxAmount" : 5000.99
                                   |        },
                                   |        "residentialPropertyAndCarriedInterest" : {
                                   |          "cgtTaxBands" : [
                                   |            {
                                   |              "name" : "lowerRate",
                                   |              "rate" : 20,
                                   |              "income" : 5000.99,
                                   |              "taxAmount" : 5000.99
                                   |            },
                                   |            {
                                   |              "name" : "lowerRate2",
                                   |              "rate" : 21,
                                   |              "income" : 5000.99,
                                   |              "taxAmount" : 5000.99
                                   |            }
                                   |          ]
                                   |        },
                                   |        "adjustments" : -99999999999.99,
                                   |        "foreignTaxCreditRelief" : 5000.99,
                                   |        "taxOnGainsAlreadyPaid" : 5000.99,
                                   |        "capitalGainsTaxDue" : 5000.99,
                                   |        "capitalGainsOverpaid" : 5000.99
                                   |      }
                                   |    }
                                   |  }
                                   |}
                                   |""".stripMargin.trim
}
