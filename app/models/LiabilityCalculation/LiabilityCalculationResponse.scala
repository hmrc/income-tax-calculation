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

import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.LiabilityCalculation.taxCalculation.TaxCalculation
import models.LiabilityCalculation.taxDeductedAtSource.TaxDeductedAtSource

sealed trait LiabilityCalculationResponseModel

case class LiabilityCalculationError(status: Int, message: String) extends LiabilityCalculationResponseModel
object LiabilityCalculationError {
  implicit val format: OFormat[LiabilityCalculationError] = Json.format[LiabilityCalculationError]
}

case class LiabilityCalculationResponse(metadata: Metadata = Metadata(),
                                        calculation: Calculation = Calculation()
                                       ) extends LiabilityCalculationResponseModel
object LiabilityCalculationResponse {
  implicit val format: OFormat[LiabilityCalculationResponse] = Json.format[LiabilityCalculationResponse]
}

case class Metadata(calculationTimestamp: Option[String] = None, crystallised: Option[Boolean] = None)
object Metadata {
  implicit val format: OFormat[Metadata] = Json.format[Metadata]
}

case class Calculation(
                        allowancesAndDeductions: AllowancesAndDeductions = AllowancesAndDeductions(),
                        reliefs: Reliefs = Reliefs(),
                        taxDeductedAtSource: TaxDeductedAtSource = TaxDeductedAtSource(),
                        giftAid: GiftAid = GiftAid(),
                        marriageAllowanceTransferredIn: MarriageAllowanceTransferredIn = MarriageAllowanceTransferredIn(),
                        employmentAndPensionsIncome: EmploymentAndPensionsIncome = EmploymentAndPensionsIncome(),
                        employmentExpenses: EmploymentExpenses = EmploymentExpenses(),
                        stateBenefitsIncome: StateBenefitsIncome = StateBenefitsIncome(),
                        shareSchemesIncome: ShareSchemesIncome = ShareSchemesIncome(),
                        foreignIncome: ForeignIncome = ForeignIncome(),
                        chargeableEventGainsIncome: ChargeableEventGainsIncome = ChargeableEventGainsIncome(),
                        savingsAndGainsIncome: SavingsAndGainsIncome = SavingsAndGainsIncome(),
                        dividendsIncome: DividendsIncome = DividendsIncome(),
                        incomeSummaryTotals: IncomeSummaryTotals = IncomeSummaryTotals(),
                        taxCalculation: TaxCalculation = TaxCalculation()
                      )

object Calculation {
  implicit val format: OFormat[Calculation] = Json.format[Calculation]
}

case class ChargeableEventGainsIncome(totalOfAllGains: Option[Int] = None)
object ChargeableEventGainsIncome {
  implicit val format: OFormat[ChargeableEventGainsIncome] = Json.format[ChargeableEventGainsIncome]
}

case class DividendsIncome(chargeableForeignDividends: Option[Int] = None)
object DividendsIncome {
  implicit val format: OFormat[DividendsIncome] = Json.format[DividendsIncome]
}

case class EmploymentAndPensionsIncome(
                                        totalPayeEmploymentAndLumpSumIncome: Option[BigDecimal] = None,
                                        totalOccupationalPensionIncome: Option[BigDecimal] = None,
                                        totalBenefitsInKind: Option[BigDecimal] = None
                                      )
object EmploymentAndPensionsIncome {
  implicit val format: OFormat[EmploymentAndPensionsIncome] = Json.format[EmploymentAndPensionsIncome]
}

case class EmploymentExpenses(totalEmploymentExpenses: Option[BigDecimal] = None)
object EmploymentExpenses {
  implicit val format: OFormat[EmploymentExpenses] = Json.format[EmploymentExpenses]
}

case class ForeignIncome(
                          chargeableOverseasPensionsStateBenefitsRoyalties: Option[BigDecimal] = None,
                          chargeableAllOtherIncomeReceivedWhilstAbroad: Option[BigDecimal] = None,
                          overseasIncomeAndGains: OverseasIncomeAndGains = OverseasIncomeAndGains(),
                          totalForeignBenefitsAndGifts: Option[BigDecimal] = None
                        )
object ForeignIncome {
  implicit val format: OFormat[ForeignIncome] = Json.format[ForeignIncome]
}

case class OverseasIncomeAndGains(gainAmount: Option[BigDecimal] = None)
object OverseasIncomeAndGains {
  implicit val format: OFormat[OverseasIncomeAndGains] = Json.format[OverseasIncomeAndGains]
}

case class GiftAid(
                    grossGiftAidPayments: Option[Int] = None,
                    giftAidTax: Option[BigDecimal] = None
                  )
object GiftAid {
  implicit val format: OFormat[GiftAid] = Json.format[GiftAid]
}

case class IncomeSummaryTotals(
                                totalSelfEmploymentProfit: Option[Int] = None,
                                totalPropertyProfit: Option[Int] = None,
                                totalFHLPropertyProfit: Option[Int] = None,
                                totalForeignPropertyProfit: Option[Int] = None,
                                totalEeaFhlProfit: Option[Int] = None
                              )
object IncomeSummaryTotals {
  implicit val writes: OFormat[IncomeSummaryTotals] = Json.format[IncomeSummaryTotals]
}

case class MarriageAllowanceTransferredIn(
                                           amount: Option[BigDecimal] = None
                                         )
object MarriageAllowanceTransferredIn {
  implicit val format: OFormat[MarriageAllowanceTransferredIn] = Json.format[MarriageAllowanceTransferredIn]
}

case class SavingsAndGainsIncome(chargeableForeignSavingsAndGains: Option[Int] = None)
object SavingsAndGainsIncome {
  implicit val format: OFormat[SavingsAndGainsIncome] = Json.format[SavingsAndGainsIncome]
}

case class ShareSchemesIncome(totalIncome: Option[BigDecimal] = None)
object ShareSchemesIncome {
  implicit val format: OFormat[ShareSchemesIncome] = Json.format[ShareSchemesIncome]
}

case class StateBenefitsIncome(totalStateBenefitsIncome: Option[BigDecimal] = None)
object StateBenefitsIncome {
  implicit val format: OFormat[StateBenefitsIncome] = Json.format[StateBenefitsIncome]
}
