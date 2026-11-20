/*
 * Copyright 2026 HM Revenue & Customs
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


package testConstants.hip

import enums.{CalculationReasonType, CalculationType, PlanType, TaxRegimeType}
import models.hip.calculation.taxLiabilitySummaryDataSA302.{
  BasicRateLimitExtension, BusinessAssetsDisposalAndInvestorsRelief, CgtTaxBands, Class2Nics, Class4Nics, Data,
  Dividends, GainsOnLifePolicies, GiftAid, MarriageAllowanceTransferDetails, Message, Messages, Metadata, Nic4Bands,
  Nics, OtherGains, PayPensionsProfit, PensionSavingsTaxCharges, RedundancyAndLumpSumsAndCompensation,
  ReliefForFinanceCosts, ResidentialProperty, SavingsAndGains, StatePensionLumpSum, StudentLoanRepayments,
  TaxBands, TaxLiabilitySummaryDataSA302Model
}

object GetTaxLiabilitySummaryDataSA302TestConstants {

  val successModelFull: TaxLiabilitySummaryDataSA302Model = TaxLiabilitySummaryDataSA302Model(
    metadata = Some(
      Metadata(
        calculationId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
        taxYear = 2027,
        calculationReason = CalculationReasonType.CustomerRequest,
        calculationTimestamp = "2026-09-15T09:35:15.094Z",
        calculationType = CalculationType.DF,
        taxRegime = TaxRegimeType.UK,
        uniqueTaxpayerReference = "1234567890"
      )
    ),
    data = Some(
      Data(
        payFromAllEmployments = Some(BigDecimal(1000.00)),
        benefitsAndExpensesReceived = Some(BigDecimal(300.00)),
        allowableExpenses = Some(BigDecimal(1000.00)),
        totalFromAllEmployments = Some(12500),
        shareSchemes = Some(BigDecimal(2000.00)),
        selfEmploymentProfit = Some(4000),
        profitFromPartnerships = Some(BigDecimal(2200.00)),
        interestFromPartnerships = Some(BigDecimal(400.00)),
        dividendsFromPartnerships = Some(BigDecimal(900.00)),
        ukLandAndPropertyProfit = Some(7000),
        foreignProperty = Some(600),
        foreignIncome = Some(BigDecimal(600.00)),
        dividendsFromForeignCompanies = Some(800),
        foreignSavings = Some(4000),
        foreignGainsOnLifePoliciesAsTaxPaid = Some(1000),
        interestFromUKBanks = Some(300),
        dividendsFromUKCompanies = Some(2000),
        ukPensionsAndStateBenefits = Some(BigDecimal(8000.25)),
        gainsOnLifeInsurancePolicies = Some(9000),
        gainsOnLifeInsurancePoliciesAsTaxPaid = Some(300),
        otherIncome = Some(BigDecimal(3500.00)),
        redundancyLumpSumsAndCompensation = Some(BigDecimal(12000.00)),
        totalIncomeReceived = Some(65000),
        paymentsToRetirementAnnuityContract = Some(BigDecimal(400.00)),
        incomeTaxRelief = Some(12500),
        capitalGainLosses = Some(4000),
        tradeUnionDeathBenefitsPayments = Some(BigDecimal(8000.12)),
        postCessationTradeReceipts = Some(BigDecimal(1000.33)),
        qualifyingLoanInterestPayments = Some(BigDecimal(550.00)),
        annuities = Some(BigDecimal(260.10)),
        giftOfInvestmentsAndPropertyToCharity = Some(6000),
        personalAllowance = Some(12500),
        reducedPersonalAllowance = Some(2000),
        marriageAllowanceTransfer = Some(BigDecimal(4300.00)),
        totalAllowancesAndDeductions = Some(4300),
        totalTaxableIncomeDue = Some(85000),
        basicRateLimitExtension = Some(
          BasicRateLimitExtension(
            extendedBasicRateBand = Some(BigDecimal(125.55)),
            pensionContributions = Some(BigDecimal(2200)),
            giftAidPayments = Some(540)
          )
        ),
        payPensionsProfit = Some(
          PayPensionsProfit(
            Some(
              Seq(
                TaxBands(
                  name = "SSR",
                  rate = BigDecimal(20),
                  income = 12500,
                  taxAmount = BigDecimal(4500.22)
                )
              )
            )
          )
        ),
        savingsAndGains = Some(
          SavingsAndGains(
            Some(
              Seq(
                TaxBands(
                  name = "SSR",
                  rate = BigDecimal(20),
                  income = 12500,
                  taxAmount = BigDecimal(3200.50)
                )
              )
            )
          )
        ),
        dividends = Some(
          Dividends(
            Some(
              Seq(
                TaxBands(
                  name = "SSR",
                  rate = BigDecimal(20),
                  income = 8000,
                  taxAmount = BigDecimal(6700.10)
                )
              )
            )
          )
        ),
        redundancyAndLumpSumsAndCompensation = Some(
          RedundancyAndLumpSumsAndCompensation(
            Some(
              Seq(
                TaxBands(
                  name = "SSR",
                  rate = BigDecimal(40),
                  income = 4000,
                  taxAmount = BigDecimal(1700.25)
                )
              )
            )
          )
        ),
        gainsOnLifePolicies = Some(
          GainsOnLifePolicies(
            Some(
              Seq(
                TaxBands(
                  name = "SSR",
                  rate = BigDecimal(22),
                  income = 3000,
                  taxAmount = BigDecimal(650.00)
                )
              )
            )
          )
        ),
        totalChargedTaxableIncome = Some(95000),
        incomeTaxDueAfterAllowancesAndReliefs = Some(BigDecimal(62000.00)),
        taxDueOnTransitionProfit = Some(BigDecimal(1000.34)),
        totalIncomeTaxDueAfterAllowancesAndReliefs = Some(BigDecimal(18000.00)),
        deficiencyRelief = Some(BigDecimal(200.00)),
        topSlicingRelief = Some(BigDecimal(350.00)),
        ventureCapitalTrustRelief = Some(BigDecimal(1000.00)),
        enterpriseInvestmentSchemeRelief = Some(BigDecimal(500.00)),
        seedEnterpriseInvestmentSchemeRelief = Some(BigDecimal(1200.10)),
        communityInvestmentTaxRelief = Some(BigDecimal(340.00)),
        maintenanceAndAlimonyPaid = Some(BigDecimal(2000.30)),
        marriageAllowanceTransferDetails = Some(
          MarriageAllowanceTransferDetails(
            amount = BigDecimal(5300.13),
            rate = BigDecimal(20),
            marriageAllowanceRelief = BigDecimal(4500.12)
          )
        ),
        reliefForFinanceCosts = Some(
          ReliefForFinanceCosts(
            amount = BigDecimal(1234.20),
            rate = BigDecimal(20),
            totalResidentialFinanceCostsRelief = BigDecimal(1500.00)
          )
        ),
        loanInterestFromInvestmentsRelief = Some(BigDecimal(1300.00)),
        basicRateDifferenceTaxReduction = Some(BigDecimal(3000.00)),
        foreignTaxCreditRelief = Some(BigDecimal(2300.00)),
        reliefClaimedOnQualifyingDistribution = Some(BigDecimal(4000.30)),
        notionalTaxFromGainsOnLifePolicies = Some(BigDecimal(300.00)),
        incomeTaxDueAfterTaxDeductions = Some(BigDecimal(18000.20)),
        giftAid = Some(
          GiftAid(
            incomeTaxDueOnGiftAidPayments = BigDecimal(340.00),
            rate = BigDecimal(20),
            grossGiftAidPayments = 1200
          )
        ),
        basicRateDifferenceTaxCharged = Some(BigDecimal(300.00)),
        pensionSavingsTaxCharges = Some(
          PensionSavingsTaxCharges(
            totalPensionSavingsCharges = Some(BigDecimal(4100.24)),
            totalTaxPaid = Some(BigDecimal(1400.00)),
            grossPensionCharges = Some(BigDecimal(5000.00))
          )
        ),
        statePensionLumpSum = Some(
          StatePensionLumpSum(
            amount = BigDecimal(3500.00),
            rate = BigDecimal(20),
            statePensionLumpSumCharges = BigDecimal(750.00)
          )
        ),
        highIncomeChildBenefitCharge = Some(BigDecimal(1400.00)),
        underpaidTaxForEarlierYears = Some(BigDecimal(400.00)),
        incomeTaxLiability = Some(BigDecimal(2400.00)),
        incomeTaxDue = Some(BigDecimal(1800.12)),
        nics = Some(
          Nics(
            totalClass2AndClass4NicsDue = BigDecimal(1234.00),
            class4Nics = Some(
              Class4Nics(
                totalClass4NicsDue = BigDecimal(650.00),
                nic4Bands = Seq(
                  Nic4Bands(
                    rate = BigDecimal(20),
                    income = 32000,
                    amount = BigDecimal(32000.00)
                  )
                )
              )
            ),
            class2Nics = Some(
              Class2Nics(
                amount = Some(BigDecimal(720.00))
              )
            )
          )
        ),
        studentLoanRepayments = Some(
          StudentLoanRepayments(
            planType = PlanType.One,
            amount = BigDecimal(5000.99)
          )
        ),
        taxDueOnAnnuityAndRoyaltyPayments = Some(BigDecimal(1200.00)),
        taxDeductedFromAllEmploymentsAndUkPensionsAndStateBenefits = Some(BigDecimal(3200.00)),
        saUnderpaymentsCodedOut = Some(BigDecimal(460.10)),
        balanceOfPayeTax = Some(BigDecimal(1220.00)),
        stateBenefits = Some(BigDecimal(12500.00)),
        cisAndTradingIncome = Some(BigDecimal(1100.00)),
        partnerships = Some(BigDecimal(400.00)),
        ukLandAndProperty = Some(BigDecimal(1600.00)),
        specialWithholdingTaxOrUkTaxPaid = Some(BigDecimal(800.00)),
        interestReceivedFromUkBanksAndBuildingSocieties = Some(BigDecimal(2600.00)),
        voidISAs = Some(BigDecimal(200.00)),
        underpaidTaxAdjustedInLaterTaxYear = Some(BigDecimal(1500.00)),
        totalTaxDeducted = Some(BigDecimal(2100.00)),
        incomeTaxOverPaid = Some(BigDecimal(3000.00)),
        incomeTaxAndClass2NicsDue = Some(BigDecimal(1450.00)),
        incomeTaxAndClass4NicsDue = Some(BigDecimal(780.00)),
        incomeTaxAndClass2NicsAndClass4NicsDue = Some(BigDecimal(2150.00)),
        totalIncomeTaxDue = Some(BigDecimal(64500.00)),
        businessAssetsDisposalAndInvestorsRelief = Some(
          BusinessAssetsDisposalAndInvestorsRelief(
            taxableGains = BigDecimal(1200.12),
            rate = BigDecimal(20),
            taxAmount = BigDecimal(300.10)
          )
        ),
        residentialProperty = Some(
          ResidentialProperty(
            cgtTaxBands = Some(
              Seq(
                CgtTaxBands(
                  name = "lowerRate",
                  rate = BigDecimal(20),
                  income = BigDecimal(5000.99),
                  taxAmount = BigDecimal(1700.20)
                )
              )
            )
          )
        ),
        otherGains = Some(
          OtherGains(
            Some(
              Seq(
                CgtTaxBands(
                  name = "lowerRate",
                  rate = BigDecimal(20),
                  income = BigDecimal(3300.00),
                  taxAmount = BigDecimal(615.10)
                )
              )
            )
          )
        ),
        taxableCapitalGains = Some(BigDecimal(1100.20)),
        capitalGainsTax = Some(BigDecimal(200.00)),
        capitalGainsTaxAdjustment = Some(BigDecimal(35.00)),
        capitalGainsTaxDue = Some(BigDecimal(940.00)),
        foreignTaxCreditReliefOnCapitalGains = Some(BigDecimal(100.00)),
        capitalGainsTaxLiability = Some(BigDecimal(120.00)),
        taxOnCapitalGainsAlreadyPaid = Some(BigDecimal(300.00)),
        totalCapitalGainsTaxDue = Some(BigDecimal(200.00)),
        capitalGainsTaxOverpaid = Some(BigDecimal(140.00)),
        incomeTaxAndNics2AndCgtDue = Some(BigDecimal(102.10)),
        incomeTaxAndNics4AndCgtDue = Some(BigDecimal(300.50)),
        incomeTaxAndNics2AndNics4AndCgtDue = Some(BigDecimal(400.00)),
        incomeTaxAndCgtDue = Some(BigDecimal(1200.00)),
        incomeTaxOverpaid = Some(BigDecimal(320.10)),
        totalTaxAndNationalInsuranceContributionsDue = Some(BigDecimal(2500.30)),
        totalTaxDue = Some(BigDecimal(19300.30))
      )
    ),
    messages = Some(
      Messages(
        info = Some(
          Seq(
            Message(
              id = "C22201",
              text = "Some message content"
            )
          )
        )
      )
    )
  )

}
