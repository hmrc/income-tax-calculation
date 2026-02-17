/*
 * Copyright 2025 HM Revenue & Customs
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

import enums.IsMTD
import models.hip.calculation.*
import models.hip.calculation.taxCalculation.*
import models.hip.{CalculationHipResponseModel, Inputs, Metadata, PersonalInformation}

import java.time.LocalDate

object GetCalculationDetailsTestConstants {

  val successFullModelGetCalculationDetailsHip: CalculationHipResponseModel =
    CalculationHipResponseModel(
      metadata = Metadata(
        calculationTimestamp = Some("2022-07-15T09:35:15.094Z"),
        calculationType = "IY",
        calculationReason = "Calculation reason",
        periodFrom = LocalDate.of(2023, 3, 2),
        periodTo = LocalDate.of(2023, 3, 10)
      ),
      inputs = Inputs(
        personalInformation = PersonalInformation(
          taxRegime = "taxRegime",
          class2VoluntaryContributions = Some(true)
        )
      ),
      calculation = Some(Calculation(
        allowancesAndDeductions = Some(AllowancesAndDeductions(
          personalAllowance = Some(50000),
          marriageAllowanceTransferOut = Some(MarriageAllowanceTransferOut(
            personalAllowanceBeforeTransferOut = 1000.00,
            transferredOutAmount = 50.56
          )),
          reducedPersonalAllowance = Some(10),
          giftOfInvestmentsAndPropertyToCharity = Some(555),
          lossesAppliedToGeneralIncome = Some(345),
          qualifyingLoanInterestFromInvestments = Some(150000),
          postCessationTradeReceipts = Some(123.56),
          paymentsToTradeUnionsForDeathBenefits = Some(10),
          grossAnnuityPayments = Some(23.21),
          pensionContributions = Some(1234.78)
        )),
        reliefs = Some(Reliefs(Some(ResidentialFinanceCosts(
          totalResidentialFinanceCostsRelief = 123
        )),
          reliefsClaimed = Some(Seq(ReliefsClaimed(
            `type` = "type",
            amountUsed = Some(50)
          ))),
          foreignTaxCreditRelief = Some(ForeignTaxCreditRelief(
            totalForeignTaxCreditRelief = 500.34
          )),
          topSlicingRelief = Some(TopSlicingRelief(
            amount = Some(345.89)
          )),
          giftAidTaxReductionWhereBasicRateDiffers = Some(GiftAidTaxReductionWhereBasicRateDiffers(
            amount = Some(300)
          ))
        )),
        taxDeductedAtSource = Some(TaxDeductedAtSource(
          bbsi = Some(200),
          ukLandAndProperty = Some(300),
          cis = Some(400),
          voidedIsa = Some(500),
          payeEmployments = Some(100),
          occupationalPensions = Some(200),
          stateBenefits = Some(300),
          specialWithholdingTaxOrUkTaxPaid = Some(400),
          inYearAdjustmentCodedInLaterTaxYear = Some(500),
          taxTakenOffTradingIncome = Some(100)
        )),
        giftAid = Some(GiftAid(
          grossGiftAidPayments = 30,
          giftAidTax = 123.45
        )),
        marriageAllowanceTransferredIn = Some(MarriageAllowanceTransferredIn(
          amount = Some(321.11)
        )),
        studentLoans = Some(Seq(StudentLoan(
          planType = "planType",
          studentLoanTotalIncomeAmount = 67321,
          studentLoanChargeableIncomeAmount = 10000.99,
          studentLoanRepaymentAmount = 100.11,
          studentLoanRepaymentAmountNetOfDeductions = 200,
          studentLoanApportionedIncomeThreshold = 10,
          studentLoanRate = 10
        ))),
        employmentAndPensionsIncome = Some(EmploymentAndPensionsIncome(
          totalPayeEmploymentAndLumpSumIncome = Some(12456.23),
          totalOccupationalPensionIncome = Some(100),
          totalBenefitsInKind = Some(50)
        )),
        employmentExpenses = Some(EmploymentExpenses(
          totalEmploymentExpenses = Some(500)
        )),
        stateBenefitsIncome = Some(StateBenefitsIncome(
          totalStateBenefitsIncome = Some(1050),
          totalStateBenefitsIncomeExcStatePensionLumpSum = Some(2050)
        )),
        shareSchemesIncome = Some(ShareSchemesIncome(
          totalIncome = 50000.50
        )),
        foreignIncome = Some(ForeignIncome(
          chargeableOverseasPensionsStateBenefitsRoyalties = Some(100),
          chargeableAllOtherIncomeReceivedWhilstAbroad = Some(200),
          overseasIncomeAndGains = Some(OverseasIncomeAndGains(
            gainAmount = 300
          )),
          totalForeignBenefitsAndGifts = Some(400)
        )),
        chargeableEventGainsIncome = Some(ChargeableEventGainsIncome(
          totalOfAllGains = 40000
        )),
        savingsAndGainsIncome = Some(SavingsAndGainsIncome(
          chargeableForeignSavingsAndGains = Some(10)
        )),
        dividendsIncome = Some(DividendsIncome(
          totalUkDividends = Some(1000), chargeableForeignDividends = Some(500)
        )),
        partnerIncome = Some(PartnerIncome(
          totalPartnerIncome = 5000.99
        )),
        incomeSummaryTotals = Some(IncomeSummaryTotals(
          totalSelfEmploymentProfit = Some(50),
          totalPropertyProfit = Some(50),
          totalForeignPropertyProfit = Some(50)
        )),
        taxCalculation = Some(TaxCalculation(
          incomeTax = IncomeTax(
            totalIncomeReceivedFromAllSources = 50000,
            totalAllowancesAndDeductions = 10000,
            totalTaxableIncome = 25000,
            payPensionsProfit = Some(PayPensionsProfit(
              taxBands = Some(Seq(TaxBands(
                name = "name",
                rate = 50,
                bandLimit = 15,
                apportionedBandLimit = 5,
                income = 2300,
                taxAmount = 150.11
              )))
            )),
            savingsAndGains = Some(SavingsAndGains(
              taxableIncome = 4000,
              taxBands = Some(Seq(TaxBands(
                name = "name1",
                rate = 100.11,
                bandLimit = 20,
                apportionedBandLimit = 10,
                income = 5000,
                taxAmount = 150
              )))
            )),
            dividends = Some(Dividends(
              taxableIncome = 15000,
              taxBands = Some(Seq(TaxBands(
                name = "name2",
                rate = 200.22,
                bandLimit = 15,
                apportionedBandLimit = 10,
                income = 10000,
                taxAmount = 2000
              )))
            )),
            lumpSums = Some(LumpSums(
              taxBands = Some(Seq(TaxBands(
                name = "name3",
                rate = 333.33,
                bandLimit = 30,
                apportionedBandLimit = 15,
                income = 150000,
                taxAmount = 50000
              )))
            )),
            gainsOnLifePolicies = Some(GainsOnLifePolicies(
              taxBands = Some(Seq(TaxBands(
                name = "name4",
                rate = 444.44,
                bandLimit = 40,
                apportionedBandLimit = 15,
                income = 75000,
                taxAmount = 15000
              )))
            )),
            totalReliefs = Some(1500.11),
            totalNotionalTax = Some(250.11),
            incomeTaxDueAfterTaxReductions = Some(50),
            totalPensionSavingsTaxCharges = Some(500),
            statePensionLumpSumCharges = Some(200.22),
            payeUnderpaymentsCodedOut = Some(500.11),
            giftAidTaxChargeWhereBasicRateDiffers = Some(1000.22),
            incomeTaxChargedOnTransitionProfits = Some(400.22)
          ),
          nics = Some(Nics(
            class2Nics = Some(Class2Nics(
              amount = Some(4500.11)
            )),
            class4Nics = Some(Class4Nics(
              nic4Bands = Seq(Nic4Bands(
                name = "nic 4 name",
                rate = 111.11,
                income = 45000,
                amount = 2400
              ))
            ))
          )),
          capitalGainsTax = Some(CapitalGainsTax(
            totalTaxableGains = 3500.11,
            adjustments = Some(200),
            foreignTaxCreditRelief = Some(200),
            taxOnGainsAlreadyPaid = Some(50),
            capitalGainsTaxDue = 100,
            capitalGainsOverpaid = Some(10),
            residentialPropertyAndCarriedInterest = Some(ResidentialPropertyAndCarriedInterest(
              cgtTaxBands = Some(Seq(CgtTaxBands(
                name = "cgtTaxBand",
                rate = 111,
                income = 2500,
                taxAmount = 100
              )))
            )),
            otherGains = Some(OtherGains(
              cgtTaxBands = Some(Seq(CgtTaxBands(
                name = "cgtTaxBand2",
                rate = 222,
                income = 4500,
                taxAmount = 200
              )))
            )),
            businessAssetsDisposalsAndInvestorsRel = Some(BusinessAssetsDisposalsAndInvestorsRel(
              taxableGains = Some(200),
              rate = Some(30),
              taxAmount = Some(450)
            ))
          )),
          totalStudentLoansRepaymentAmount = Some(1050),
          saUnderpaymentsCodedOut = Some(100),
          totalAnnuityPaymentsTaxCharged = Some(50),
          totalRoyaltyPaymentsTaxCharged = Some(50),
          totalIncomeTaxAndNicsDue = 2000,
          totalTaxDeducted = Some(5000),
          totalIncomeTaxAndNicsAndCgt = Some(2500)
        )),
        endOfYearEstimate = Some(EndOfYearEstimate(
          incomeSource = Some(List(IncomeSource(
            incomeSourceType = "incomeSource",
            incomeSourceName = Some("incomeSourceName"),
            taxableIncome = 4500
          ))),
          totalAllowancesAndDeductions = Some(400),
          totalEstimatedIncome = Some(400),
          totalTaxableIncome = Some(200),
          incomeTaxAmount = Some(200.13),
          nic2 = Some(100),
          nic4 = Some(100),
          totalNicAmount = Some(200),
          totalTaxDeductedBeforeCodingOut = Some(150),
          saUnderpaymentsCodedOut = Some(50),
          totalStudentLoansRepaymentAmount = Some(400),
          totalAnnuityPaymentsTaxCharged = Some(250),
          totalRoyaltyPaymentsTaxCharged = Some(250),
          totalTaxDeducted = Some(100),
          incomeTaxNicAmount = Some(100),
          cgtAmount = Some(50),
          incomeTaxNicAndCgtAmount = Some(150)
        )),
        pensionSavingsTaxCharges = Some(PensionSavingsTaxCharges(
          totalPensionCharges = Some(400),
          totalTaxPaid = Some(1000.50),
          totalPensionChargesDue = Some(100.23)
        )),
        otherIncome = Some(OtherIncome(
          totalOtherIncome = 2000
        )),
        transitionProfit = Some(TransitionProfit(
          totalTaxableTransitionProfit = Some(300)
        )),
        highIncomeChildBenefitCharge = None
      )),
      messages = None,
      submissionChannel = None
    )

}
