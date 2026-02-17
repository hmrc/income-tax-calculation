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

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class AllowancesAndDeductions(personalAllowance: Option[Int] = None,
                                   marriageAllowanceTransferOut: Option[MarriageAllowanceTransferOut] = None,
                                   reducedPersonalAllowance: Option[Int] = None,
                                   giftOfInvestmentsAndPropertyToCharity: Option[Int] = None,
                                   lossesAppliedToGeneralIncome: Option[Int] = None,
                                   totalPartnershipLossesBroughtForward: Option[BigDecimal] = None,
                                   qualifyingLoanInterestFromInvestments: Option[BigDecimal] = None,
                                   postCessationTradeReceipts: Option[BigDecimal] = None,
                                   paymentsToTradeUnionsForDeathBenefits: Option[BigDecimal] = None,
                                   grossAnnuityPayments: Option[BigDecimal] = None,
                                   pensionContributions: Option[BigDecimal] = None)

object AllowancesAndDeductions {
  implicit val reads: Reads[AllowancesAndDeductions] =
    ((__ \ "personalAllowance").readNullable[Int] and
      (__ \ "marriageAllowanceTransferOut").readNullable[MarriageAllowanceTransferOut] and
      (__ \ "reducedPersonalAllowance").readNullable[Int] and
      (__ \ "giftOfInvestmentsAndPropertyToCharity").readNullable[Int] and
      (__ \ "lossesAppliedToGeneralIncome").readNullable[Int] and
      (__ \ "totalPartnershipLossesBroughtForward").readNullable[BigDecimal] and
      (__ \ "qualifyingLoanInterestFromInvestments").readNullable[BigDecimal] and
      (__ \ "post-cessationTradeReceipts").readNullable[BigDecimal] and
      (__ \ "paymentsToTradeUnionsForDeathBenefits").readNullable[BigDecimal] and
      (__ \ "grossAnnuityPayments").readNullable[BigDecimal] and
      (__ \ "pensionContributions").readNullable[BigDecimal])(AllowancesAndDeductions.apply _)

  implicit val writes: OWrites[AllowancesAndDeductions] = (
    (JsPath \ "personalAllowance").writeNullable[Int] and
      (JsPath \ "marriageAllowanceTransferOut").writeNullable[MarriageAllowanceTransferOut] and
      (JsPath \ "reducedPersonalAllowance").writeNullable[Int] and
      (JsPath \ "giftOfInvestmentsAndPropertyToCharity").writeNullable[Int] and
      (JsPath \ "lossesAppliedToGeneralIncome").writeNullable[Int] and
      (JsPath \ "totalPartnershipLossesBroughtForward").writeNullable[BigDecimal] and
      (JsPath \ "qualifyingLoanInterestFromInvestments").writeNullable[BigDecimal] and
      (JsPath \ "post-cessationTradeReceipts").writeNullable[BigDecimal] and
      (JsPath \ "paymentsToTradeUnionsForDeathBenefits").writeNullable[BigDecimal] and
      (JsPath \ "grossAnnuityPayments").writeNullable[BigDecimal] and
      (JsPath \ "pensionContributions").writeNullable[BigDecimal]
    )(aad =>
    (
      aad.personalAllowance,
      aad.marriageAllowanceTransferOut,
      aad.reducedPersonalAllowance,
      aad.giftOfInvestmentsAndPropertyToCharity,
      aad.lossesAppliedToGeneralIncome,
      aad.totalPartnershipLossesBroughtForward,
      aad.qualifyingLoanInterestFromInvestments,
      aad.postCessationTradeReceipts,
      aad.paymentsToTradeUnionsForDeathBenefits,
      aad.grossAnnuityPayments,
      aad.pensionContributions
    )
  )
}

case class MarriageAllowanceTransferOut(
                                         personalAllowanceBeforeTransferOut: BigDecimal,
                                         transferredOutAmount: BigDecimal
                                       )

object MarriageAllowanceTransferOut {
  implicit val format: OFormat[MarriageAllowanceTransferOut] = Json.format[MarriageAllowanceTransferOut]
}




