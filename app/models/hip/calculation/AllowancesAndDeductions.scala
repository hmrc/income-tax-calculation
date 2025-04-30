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

package models.hip.calculation

import play.api.libs.json.{Json, OFormat}

case class AllowancesAndDeductions(personalAllowance: Option[Int] = None,
                                   marriageAllowanceTransferOut: Option[MarriageAllowanceTransferOut] = None,
                                   reducedPersonalAllowance: Option[Int] = None,
                                   giftOfInvestmentsAndPropertyToCharity: Option[Int] = None,
                                   blindPersonsAllowance: Option[Int] = None,
                                   lossesAppliedToGeneralIncome: Option[Int] = None,
                                   cgtLossSetAgainstInYearGeneralIncome: Option[Int] = None,
                                   qualifyingLoanInterestFromInvestments: Option[BigDecimal] = None,
                                   postCessationTradeReceipts: Option[BigDecimal] = None,
                                   paymentsToTradeUnionsForDeathBenefits: Option[BigDecimal] = None,
                                   grossAnnuityPayments: Option[BigDecimal] = None,
                                   annuityPayments: Option[AnnuityPayments] = None,
                                   pensionContributions: Option[BigDecimal] = None,
                                   pensionContributionsDetail: Option[PensionContributionsDetail] = None)

object AllowancesAndDeductions {
  implicit val format: OFormat[AllowancesAndDeductions] = Json.format[AllowancesAndDeductions]
}

case class MarriageAllowanceTransferOut(
                                         personalAllowanceBeforeTransferOut: BigDecimal,
                                         transferredOutAmount: BigDecimal
                                       )

object MarriageAllowanceTransferOut {
  implicit val format: OFormat[MarriageAllowanceTransferOut] = Json.format[MarriageAllowanceTransferOut]
}

case class AnnuityPayments(reliefClaimed: Option[BigDecimal], rate: Option[BigDecimal])

object AnnuityPayments {
  implicit val format: OFormat[AnnuityPayments] = Json.format[AnnuityPayments]
}

case class PensionContributionsDetail(retirementAnnuityPayments: Option[BigDecimal],
                                      paymentToEmployersSchemeNoTaxRelief: Option[BigDecimal],
                                      overseasPensionSchemeContributions: Option[BigDecimal])

object PensionContributionsDetail {
  implicit val format: OFormat[PensionContributionsDetail] = Json.format[PensionContributionsDetail]
}

