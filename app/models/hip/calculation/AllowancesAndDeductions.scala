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
      (__ \ "qualifyingLoanInterestFromInvestments").readNullable[BigDecimal] and
      (__ \ "postCessationTradeReceipts").readNullable[BigDecimal] and
      (__ \ "paymentsToTradeUnionsForDeathBenefits").readNullable[BigDecimal] and
      (__ \ "grossAnnuityPayments").readNullable[BigDecimal] and
      (__ \ "pensionContributions").readNullable[BigDecimal])(AllowancesAndDeductions.apply _)

  implicit val writes: Writes[AllowancesAndDeductions] = Json.writes[AllowancesAndDeductions]
}

case class MarriageAllowanceTransferOut(
                                         personalAllowanceBeforeTransferOut: BigDecimal,
                                         transferredOutAmount: BigDecimal
                                       )

object MarriageAllowanceTransferOut {
  implicit val format: OFormat[MarriageAllowanceTransferOut] = Json.format[MarriageAllowanceTransferOut]
}




