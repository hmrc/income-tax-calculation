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

package models.hip.calculation.taxLiabilitySummaryDataSA302

import play.api.libs.json.{Json, OFormat}
import enums.{CalculationReasonType, CalculationType, TaxRegimeType}
import models.calculation.GiftAid

import java.time.LocalDate

case class TaxLiabilitySummaryDataSA302ResponseModel(
                                        metadata: Option[Metadata],
                                        data: Option[Data],
                                        messages: Option[Messages]
                                      )

object TaxLiabilitySummaryDataSA302ResponseModel {
  implicit val format: OFormat[TaxLiabilitySummaryDataSA302ResponseModel] = Json.format[TaxLiabilitySummaryDataSA302ResponseModel]
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
//               TODO create BasicRateLimitExtension
//                 basicRateLimitExtension: Option[BasicRateLimitExtension],
//                 TODO create PayPensionsProfit
//                 payPensionsProfit: Option[PayPensionsProfit],
//                 TODO create SavingsAndGains
//                 savingsAndGains: Option[SavingsAndGains],
//                 TODO create Dividends
//                 dividends: Option[Dividends],
//                 TODO create RedundancyAndLumpSumsAndCompensation
//                 redundancyAndLumpSumsAndCompensation: Option[RedundancyAndLumpSumsAndCompensation],
//                 TODO create GainsOnLifePolicies
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
//                TODO create MarriageAllowanceTransferDetails
//                 marriageAllowanceTransferDetails: Option[MarriageAllowanceTransferDetails],
//                 TODO create ReliefForFinanceCosts
//                 reliefForFinanceCosts: Option[ReliefForFinanceCosts],
                 loanInterestFromInvestmentsRelief: Option[BigDecimal],
                 basicRateDifferenceTaxReduction: Option[BigDecimal],
                 foreignTaxCreditRelief: Option[BigDecimal],
                 reliefClaimedOnQualifyingDistribution: Option[BigDecimal],
                 notionalTaxFromGainsOnLifePolicies: Option[BigDecimal],
                 incomeTaxDueAfterTaxDeductions: Option[BigDecimal],
//                TODO create GiftAid
//                 giftAid: Option[GiftAid],
                 basicRateDifferenceTaxCharged: Option[BigDecimal],
//                TODO create PensionSavingsTaxCharges
//                 pensionSavingsTaxCharges: Option[PensionSavingsTaxCharges],
//                 TODO create StatePensionLumpSum
                 statePensionLumpSum: Option[StatePensionLumpSum],
                 highIncomeChildBenefitCharge: Option[BigDecimal],
                 underpaidTaxForEarlierYears: Option[BigDecimal],
                 incomeTaxLiability: Option[BigDecimal],
                 incomeTaxDue: Option[BigDecimal],
//                 TODO create Nics
//                 nics: Option[Nics],
//                TODO create StudentLoanRepayments
//                 studentLoanRepayments: Option[StudentLoanRepayments],
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
//                 TODO create BusinessAssetsDisposalAndInvestorsRelief
//                 businessAssetsDisposalAndInvestorsRelief: Option[BusinessAssetsDisposalAndInvestorsRelief],
//                 TODO create ResidentialProperty
//                 residentialProperty: Option[ResidentialProperty],
//                 TODO create OtherGains
//                 otherGains: Option[OtherGains],
                 taxableCapitalGains: Option[BigDecimal],
                 capitalGainsTax: Option[capitalGainsTax],
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
  implicit val format: OFormat[Data] = Json.format[Data]
}

case class Message(id: String, text: String)

object Message {
  implicit val format: OFormat[Message] = Json.format[Message]
}

case class CustomerServiceMessage(text: String)

object CustomerServiceMessage {
  implicit val format: OFormat[CustomerServiceMessage] = Json.format[CustomerServiceMessage]
}

case class Messages(info: Option[Seq[Message]] = None,
                     warnings: Option[Seq[Message]] = None,
                     errors: Option[Seq[Message]] = None,
                     customerServiceMessages: Option[Seq[CustomerServiceMessage]] = None)

object Messages {
  implicit val format: OFormat[Messages] = Json.format[Messages]
}