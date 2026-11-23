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

package models.hip.taxLiabilitySummaryDataSA302

import enums.{CalculationReasonType, CalculationType, PlanType, TaxRegimeType}
import play.api.libs.json.{Json, OFormat}

case class TaxLiabilitySummaryDataSA302Model(
                                              metadata: Option[Metadata],
                                              data: Option[Data],
                                              messages: Option[Messages])

object TaxLiabilitySummaryDataSA302Model {
  given format: OFormat[TaxLiabilitySummaryDataSA302Model] = Json.format[TaxLiabilitySummaryDataSA302Model]
}

case class Metadata(
                     calculationId: String,
                     taxYear: Int,
                     calculationReason: CalculationReasonType,
                     calculationTimestamp: String,
                     calculationType: CalculationType,
                     taxRegime: TaxRegimeType,
                     uniqueTaxpayerReference: String
                   )

object Metadata {
  implicit val format: OFormat[Metadata] = Json.format[Metadata]
}

case class Data(
                 payFromAllEmployments: Option[BigDecimal],
                 benefitsAndExpensesReceived: Option[BigDecimal],
                 allowableExpenses: Option[BigDecimal],
                 totalFromAllEmployments: Option[Int],
                 shareSchemes: Option[BigDecimal],
                 selfEmploymentProfit: Option[Int],
                 profitFromPartnerships: Option[BigDecimal],
                 interestFromPartnerships: Option[BigDecimal],
                 dividendsFromPartnerships: Option[BigDecimal],
                 ukLandAndPropertyProfit: Option[Int],
                 foreignProperty: Option[Int],
                 foreignIncome: Option[BigDecimal],
                 dividendsFromForeignCompanies: Option[Int],
                 foreignSavings: Option[Int],
                 foreignGainsOnLifePoliciesAsTaxPaid: Option[Int],
                 interestFromUKBanks: Option[Int],
                 dividendsFromUKCompanies: Option[Int],
                 ukPensionsAndStateBenefits: Option[BigDecimal],
                 gainsOnLifeInsurancePolicies: Option[Int],
                 gainsOnLifeInsurancePoliciesAsTaxPaid: Option[Int],
                 otherIncome: Option[BigDecimal],
                 redundancyLumpSumsAndCompensation: Option[BigDecimal],
                 totalIncomeReceived: Option[Int],
                 paymentsToRetirementAnnuityContract: Option[BigDecimal],
                 incomeTaxRelief: Option[Int],
                 capitalGainLosses: Option[Int],
                 tradeUnionDeathBenefitsPayments: Option[BigDecimal],
                 postCessationTradeReceipts: Option[BigDecimal],
                 qualifyingLoanInterestPayments: Option[BigDecimal],
                 annuities: Option[BigDecimal],
                 giftOfInvestmentsAndPropertyToCharity: Option[Int],
                 personalAllowance: Option[Int],
                 reducedPersonalAllowance: Option[Int],
                 marriageAllowanceTransfer: Option[BigDecimal],
                 totalAllowancesAndDeductions: Option[Int],
                 totalTaxableIncomeDue: Option[Int],
                 basicRateLimitExtension: Option[BasicRateLimitExtension],
                 payPensionsProfit: Option[PayPensionsProfit],
                 savingsAndGains: Option[SavingsAndGains],
                 dividends: Option[Dividends],
                 redundancyAndLumpSumsAndCompensation: Option[RedundancyAndLumpSumsAndCompensation],
                 gainsOnLifePolicies: Option[GainsOnLifePolicies],
                 totalChargedTaxableIncome: Option[Int],
                 incomeTaxDueAfterAllowancesAndReliefs: Option[BigDecimal],
                 taxDueOnTransitionProfit: Option[BigDecimal],
                 totalIncomeTaxDueAfterAllowancesAndReliefs: Option[BigDecimal],
                 deficiencyRelief: Option[BigDecimal],
                 topSlicingRelief: Option[BigDecimal],
                 ventureCapitalTrustRelief: Option[BigDecimal],
                 enterpriseInvestmentSchemeRelief: Option[BigDecimal],
                 seedEnterpriseInvestmentSchemeRelief: Option[BigDecimal],
                 communityInvestmentTaxRelief: Option[BigDecimal],
                 maintenanceAndAlimonyPaid: Option[BigDecimal],
                 marriageAllowanceTransferDetails: Option[MarriageAllowanceTransferDetails],
                 reliefForFinanceCosts: Option[ReliefForFinanceCosts],
                 loanInterestFromInvestmentsRelief: Option[BigDecimal],
                 basicRateDifferenceTaxReduction: Option[BigDecimal],
                 foreignTaxCreditRelief: Option[BigDecimal],
                 reliefClaimedOnQualifyingDistribution: Option[BigDecimal],
                 notionalTaxFromGainsOnLifePolicies: Option[BigDecimal],
                 incomeTaxDueAfterTaxDeductions: Option[BigDecimal],
                 giftAid: Option[GiftAid],
                 basicRateDifferenceTaxCharged: Option[BigDecimal],
                 pensionSavingsTaxCharges: Option[PensionSavingsTaxCharges],
                 statePensionLumpSum: Option[StatePensionLumpSum],
                 highIncomeChildBenefitCharge: Option[BigDecimal],
                 underpaidTaxForEarlierYears: Option[BigDecimal],
                 incomeTaxLiability: Option[BigDecimal],
                 incomeTaxDue: Option[BigDecimal],
                 nics: Option[Nics],
                 studentLoanRepayments: Option[StudentLoanRepayments],
                 taxDueOnAnnuityAndRoyaltyPayments: Option[BigDecimal],
                 taxDeductedFromAllEmploymentsAndUkPensionsAndStateBenefits: Option[BigDecimal],
                 saUnderpaymentsCodedOut: Option[BigDecimal],
                 balanceOfPayeTax: Option[BigDecimal],
                 stateBenefits: Option[BigDecimal],
                 cisAndTradingIncome: Option[BigDecimal],
                 partnerships: Option[BigDecimal],
                 ukLandAndProperty: Option[BigDecimal],
                 specialWithholdingTaxOrUkTaxPaid: Option[BigDecimal],
                 interestReceivedFromUkBanksAndBuildingSocieties: Option[BigDecimal],
                 voidISAs: Option[BigDecimal],
                 underpaidTaxAdjustedInLaterTaxYear: Option[BigDecimal],
                 totalTaxDeducted: Option[BigDecimal],
                 incomeTaxOverPaid: Option[BigDecimal],
                 incomeTaxAndClass2NicsDue: Option[BigDecimal],
                 incomeTaxAndClass4NicsDue: Option[BigDecimal],
                 incomeTaxAndClass2NicsAndClass4NicsDue: Option[BigDecimal],
                 totalIncomeTaxDue: Option[BigDecimal],
                 businessAssetsDisposalAndInvestorsRelief: Option[BusinessAssetsDisposalAndInvestorsRelief],
                 residentialProperty: Option[ResidentialProperty],
                 otherGains: Option[OtherGains],
                 taxableCapitalGains: Option[BigDecimal],
                 capitalGainsTax: Option[BigDecimal],
                 capitalGainsTaxAdjustment: Option[BigDecimal],
                 capitalGainsTaxDue: Option[BigDecimal],
                 foreignTaxCreditReliefOnCapitalGains: Option[BigDecimal],
                 capitalGainsTaxLiability: Option[BigDecimal],
                 taxOnCapitalGainsAlreadyPaid: Option[BigDecimal],
                 totalCapitalGainsTaxDue: Option[BigDecimal],
                 capitalGainsTaxOverpaid: Option[BigDecimal],
                 incomeTaxAndNics2AndCgtDue: Option[BigDecimal],
                 incomeTaxAndNics4AndCgtDue: Option[BigDecimal],
                 incomeTaxAndNics2AndNics4AndCgtDue: Option[BigDecimal],
                 incomeTaxAndCgtDue: Option[BigDecimal],
                 incomeTaxOverpaid: Option[BigDecimal],
                 totalTaxAndNationalInsuranceContributionsDue: Option[BigDecimal],
                 totalTaxDue: Option[BigDecimal]
               )

object Data {
  given format: OFormat[Data] = Json.format[Data]
}

case class BasicRateLimitExtension(
                                    extendedBasicRateBand: Option[BigDecimal],
                                    pensionContributions: Option[BigDecimal],
                                    giftAidPayments: Option[Int]
                                  )

object BasicRateLimitExtension {
  given format: OFormat[BasicRateLimitExtension] = Json.format[BasicRateLimitExtension]
}

case class TaxBands(
                     name: String,
                     rate: BigDecimal,
                     income: Int,
                     taxAmount: BigDecimal
                   )

object TaxBands {
  given format: OFormat[TaxBands] = Json.format[TaxBands]
}

case class PayPensionsProfit(taxBands: Option[Seq[TaxBands]])

object PayPensionsProfit {
  given format: OFormat[PayPensionsProfit] = Json.format[PayPensionsProfit]
}

case class SavingsAndGains(taxBands: Option[Seq[TaxBands]])

object SavingsAndGains {
  given format: OFormat[SavingsAndGains] = Json.format[SavingsAndGains]
}

case class Dividends(taxBands: Option[Seq[TaxBands]])

object Dividends {
  given format: OFormat[Dividends] = Json.format[Dividends]
}

case class RedundancyAndLumpSumsAndCompensation(taxBands: Option[Seq[TaxBands]])

object RedundancyAndLumpSumsAndCompensation {
  given format: OFormat[RedundancyAndLumpSumsAndCompensation] = Json.format[RedundancyAndLumpSumsAndCompensation]
}

case class GainsOnLifePolicies(taxBands: Option[Seq[TaxBands]])

object GainsOnLifePolicies {
  given format: OFormat[GainsOnLifePolicies] = Json.format[GainsOnLifePolicies]
}

case class MarriageAllowanceTransferDetails(
                                             amount: BigDecimal,
                                             rate: BigDecimal,
                                             marriageAllowanceRelief: BigDecimal
                                           )

object MarriageAllowanceTransferDetails {
  given format: OFormat[MarriageAllowanceTransferDetails] = Json.format[MarriageAllowanceTransferDetails]
}

case class ReliefForFinanceCosts(
                                  amount: BigDecimal,
                                  rate: BigDecimal,
                                  totalResidentialFinanceCostsRelief: BigDecimal
                                )

object ReliefForFinanceCosts {
  given format: OFormat[ReliefForFinanceCosts] = Json.format[ReliefForFinanceCosts]
}

case class GiftAid(
                    incomeTaxDueOnGiftAidPayments: BigDecimal,
                    rate: BigDecimal,
                    grossGiftAidPayments: Int
                  )

object GiftAid {
  given format: OFormat[GiftAid] = Json.format[GiftAid]
}

case class PensionSavingsTaxCharges(totalPensionSavingsCharges: Option[BigDecimal],
                                    totalTaxPaid: Option[BigDecimal],
                                    grossPensionCharges: Option[BigDecimal])

object PensionSavingsTaxCharges {
  given format: OFormat[PensionSavingsTaxCharges] = Json.format[PensionSavingsTaxCharges]
}

case class StatePensionLumpSum(
                                amount: BigDecimal,
                                rate: BigDecimal,
                                statePensionLumpSumCharges: BigDecimal
                              )

object StatePensionLumpSum {
  given format: OFormat[StatePensionLumpSum] = Json.format[StatePensionLumpSum]
}

case class Class2Nics(amount: Option[BigDecimal])

object Class2Nics {
  given format: OFormat[Class2Nics] = Json.format[Class2Nics]
}

case class Nic4Bands(
                      rate: BigDecimal,
                      income: Int,
                      amount: BigDecimal
                    )

object Nic4Bands {
  given format: OFormat[Nic4Bands] = Json.format[Nic4Bands]
}

case class Class4Nics(
                       totalClass4NicsDue: BigDecimal,
                       nic4Bands: Seq[Nic4Bands]
                     )

object Class4Nics {
  given format: OFormat[Class4Nics] = Json.format[Class4Nics]
}

case class Nics(
                 totalClass2AndClass4NicsDue: BigDecimal,
                 class4Nics: Option[Class4Nics],
                 class2Nics: Option[Class2Nics]
               )

object Nics {
  given format: OFormat[Nics] = Json.format[Nics]
}

case class StudentLoanRepayments(
                                  planType: PlanType,
                                  amount: BigDecimal
                                )

object StudentLoanRepayments {
  given format: OFormat[StudentLoanRepayments] = Json.format[StudentLoanRepayments]
}

case class BusinessAssetsDisposalAndInvestorsRelief(
                                                     taxableGains: BigDecimal,
                                                     rate: BigDecimal,
                                                     taxAmount: BigDecimal
                                                   )

object BusinessAssetsDisposalAndInvestorsRelief {
  given format: OFormat[BusinessAssetsDisposalAndInvestorsRelief] = Json.format[BusinessAssetsDisposalAndInvestorsRelief]
}

case class CgtTaxBands(name: String,
                       rate: BigDecimal,
                       income: BigDecimal,
                       taxAmount: BigDecimal
                      )

object CgtTaxBands {
  given format: OFormat[CgtTaxBands] = Json.format[CgtTaxBands]
}

case class ResidentialProperty(cgtTaxBands: Option[Seq[CgtTaxBands]])

object ResidentialProperty {
  given format: OFormat[ResidentialProperty] = Json.format[ResidentialProperty]
}

case class OtherGains(
                       cgtTaxBands: Option[Seq[CgtTaxBands]]
                     )

object OtherGains {
  given format: OFormat[OtherGains] = Json.format[OtherGains]
}

case class Message(id: String, text: String)

object Message {
  given format: OFormat[Message] = Json.format[Message]
}

case class Messages(info: Option[Seq[Message]])

object Messages {
  given format: OFormat[Messages] = Json.format[Messages]
}