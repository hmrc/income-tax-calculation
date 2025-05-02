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

case class Calculation(allowancesAndDeductions: Option[AllowancesAndDeductions],
                       reliefs: Option[Reliefs],
                       taxDeductedAtSource: Option[TaxDeductedAtSource],
                       giftAid: Option[GiftAid],
                       marriageAllowanceTransferredIn: Option[MarriageAllowanceTransferredIn],
                       studentLoans: Option[Seq[StudentLoans]],
                       employmentAndPensionsIncome: Option[EmploymentAndPensionsIncome],
                       employmentExpenses: Option[EmploymentExpenses],
                       stateBenefitsIncome: Option[StateBenefitsIncome],
                       shareSchemesIncome: Option[ShareSchemesIncome],
                       foreignIncome: Option[ForeignIncome],
                       chargeableEventGainsIncome: Option[ChargeableEventGainsIncome],
                       savingsAndGainsIncome: Option[SavingsAndGainsIncome],
                       dividendsIncome: Option[DividendsIncome],
                       incomeSummaryTotals: Option[IncomeSummaryTotals])

case class GiftAid(
                    grossGiftAidPayments: Int,
                    rate: BigDecimal,
                    giftAidTax: BigDecimal)


object GiftAid {
  implicit val writes: Writes[GiftAid] = Json.writes[GiftAid]

  implicit val reads: Reads[GiftAid] = (
    (__ \ "grossGiftAidPayments").read[Int] and
      (__ \ "rate").read[BigDecimal] and
      (__ \ "giftAidTax").read[BigDecimal])(GiftAid.apply _)
}

case class MarriageAllowanceTransferredIn(amount: Option[BigDecimal] = None)

object MarriageAllowanceTransferredIn {
  implicit val format: OFormat[MarriageAllowanceTransferredIn] = Json.format[MarriageAllowanceTransferredIn]
}

case class StudentLoans(planType: String,
                       studentLoanTotalIncomeAmount: BigDecimal,
                       studentLoanChargeableIncomeAmount: BigDecimal,
                       studentLoanRepaymentAmount: BigDecimal,
                       studentLoanRepaymentAmountNetOfDeductions: BigDecimal,
                       studentLoanApportionedIncomeThreshold: Int,
                       studentLoanRate: BigDecimal)

object StudentLoans {
  implicit val writes: Writes[StudentLoans] = Json.writes[StudentLoans]

  implicit val reads: Reads[StudentLoans] = (
    (__ \ "planType").read[String] and
      (__ \ "studentLoanTotalIncomeAmount").read[BigDecimal] and
      (__ \ "studentLoanChargeableIncomeAmount").read[BigDecimal] and
      (__ \ "studentLoanRepaymentAmount").read[BigDecimal] and
      (__ \ "studentLoanRepaymentAmountNetOfDeductions").read[BigDecimal] and
      (__ \ "studentLoanApportionedIncomeThreshold").read[Int] and
      (__ \ "studentLoanRate").read[BigDecimal])(StudentLoans.apply _)
}

case class EmploymentAndPensionsIncome(
                                        totalPayeEmploymentAndLumpSumIncome: Option[BigDecimal] = None,
                                        totalOccupationalPensionIncome: Option[BigDecimal] = None,
                                        totalBenefitsInKind: Option[BigDecimal] = None)

object EmploymentAndPensionsIncome {
  implicit val writes: Writes[EmploymentAndPensionsIncome] = Json.writes[EmploymentAndPensionsIncome]

  implicit val reads: Reads[EmploymentAndPensionsIncome] = (
    (__ \ "totalPayeEmploymentAndLumpSumIncome").readNullable[BigDecimal] and
      (__ \ "totalOccupationalPensionIncome").readNullable[BigDecimal] and
      (__ \ "totalBenefitsInKind").readNullable[BigDecimal])(EmploymentAndPensionsIncome.apply _)
}

case class EmploymentExpenses(totalEmploymentExpenses: Option[BigDecimal] = None)

object EmploymentExpenses {
  implicit val format: OFormat[EmploymentExpenses] = Json.format[EmploymentExpenses]
}

case class StateBenefitsIncome(totalStateBenefitsIncome: Option[BigDecimal] = None,
                               totalStateBenefitsIncomeExcStatePensionLumpSum: Option[BigDecimal] = None)

object StateBenefitsIncome {
  implicit val writes: Writes[StateBenefitsIncome] = Json.writes[StateBenefitsIncome]

  implicit val reads: Reads[StateBenefitsIncome] = (
    (__ \ "totalStateBenefitsIncome").readNullable[BigDecimal] and
      (__ \ "totalStateBenefitsIncomeExcStatePensionLumpSum").readNullable[BigDecimal])(StateBenefitsIncome.apply _)
}

case class ShareSchemesIncome(totalIncome: BigDecimal)

object ShareSchemesIncome {
  implicit val format: OFormat[ShareSchemesIncome] = Json.format[ShareSchemesIncome]
}

case class ForeignIncome(
                          chargeableOverseasPensionsStateBenefitsRoyalties: Option[BigDecimal] = None,
                          chargeableAllOtherIncomeReceivedWhilstAbroad: Option[BigDecimal] = None,
                          overseasIncomeAndGains: Option[OverseasIncomeAndGains],
                          totalForeignBenefitsAndGifts: Option[BigDecimal] = None
                        )

object ForeignIncome {
  implicit val writes: Writes[ForeignIncome] = Json.writes[ForeignIncome]

  implicit val reads: Reads[ForeignIncome] = (
    (__ \ "chargeableOverseasPensionsStateBenefitsRoyalties").readNullable[BigDecimal] and
      (__ \ "chargeableAllOtherIncomeReceivedWhilstAbroad").readNullable[BigDecimal] and
      (__ \ "overseasIncomeAndGains").readNullable[OverseasIncomeAndGains] and
      (__ \ "totalForeignBenefitsAndGifts").readNullable[BigDecimal])(ForeignIncome.apply _)
}

case class OverseasIncomeAndGains(gainAmount: BigDecimal)

object OverseasIncomeAndGains {
  implicit val format: OFormat[OverseasIncomeAndGains] = Json.format[OverseasIncomeAndGains]
}

case class ChargeableEventGainsIncome(totalOfAllGains: Int)

object ChargeableEventGainsIncome {
  implicit val format: OFormat[ChargeableEventGainsIncome] = Json.format[ChargeableEventGainsIncome]
}

case class SavingsAndGainsIncome(chargeableForeignSavingsAndGains: Option[Int] = None)

object SavingsAndGainsIncome {
  implicit val format: OFormat[SavingsAndGainsIncome] = Json.format[SavingsAndGainsIncome]
}

case class DividendsIncome(chargeableForeignDividends: Option[Int] = None)

object DividendsIncome {
  implicit val format: OFormat[DividendsIncome] = Json.format[DividendsIncome]
}

case class IncomeSummaryTotals(
                                totalSelfEmploymentProfit: Option[Int] = None,
                                totalPropertyProfit: Option[Int] = None,
                                totalForeignPropertyProfit: Option[Int] = None,
                              )

object IncomeSummaryTotals {
  implicit val writes: Writes[IncomeSummaryTotals] = Json.writes[IncomeSummaryTotals]

  implicit val reads: Reads[IncomeSummaryTotals] = (
    (__ \ "totalSelfEmploymentProfit").readNullable[Int] and
      (__ \ "totalPropertyProfit").readNullable[Int] and
      (__ \ "totalForeignPropertyProfit").readNullable[Int])(IncomeSummaryTotals.apply _)

}

