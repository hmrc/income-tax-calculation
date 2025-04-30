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

package models.hip.calculation.reliefs.residentialFinanceCosts

import play.api.libs.json.{Json, OFormat}

case class ResidentialFinanceCosts(adjustedTotalIncome: BigDecimal,
                                   totalAllowableAmount: Option[BigDecimal],
                                   relievableAmount: BigDecimal,
                                   rate: BigDecimal,
                                   totalResidentialFinanceCostsRelief: BigDecimal,
                                   ukProperty: Option[UkProperty],
                                   foreignProperty: Option[ForeignProperty],
                                   allOtherIncomeReceivedWhilstAbroad: Option[AllOtherIncomeReceivedWhilstAbroad])

object ResidentialFinanceCosts {
  implicit val format: OFormat[ResidentialFinanceCosts] = Json.format[ResidentialFinanceCosts]
}

case class UkProperty(amountClaimed: Int,
                      allowableAmount: BigDecimal,
                      carryForwardAmount: Option[BigDecimal])

object UkProperty {
  implicit val format: OFormat[UkProperty] = Json.format[UkProperty]
}

case class ForeignProperty(totalForeignPropertyAllowableAmount: BigDecimal,
                           foreignPropertyRfcDetail: List[ForeignPropertyRfcDetail]
                          )

object ForeignProperty {
  implicit val format: OFormat[ForeignProperty] = Json.format[ForeignProperty]
}


case class ForeignPropertyRfcDetail(countryCode: String,
                                    amountClaimed: Int,
                                    allowableAmount: BigDecimal,
                                    carryForwardAmount: Option[BigDecimal])

object ForeignPropertyRfcDetail {
  implicit val format: OFormat[ForeignPropertyRfcDetail] = Json.format[ForeignPropertyRfcDetail]
}

case class AllOtherIncomeReceivedWhilstAbroad(totalOtherIncomeAllowableAmount: BigDecimal,
                                              otherIncomeRfcDetail: List[OtherIncomeRfcDetail])

object AllOtherIncomeReceivedWhilstAbroad {
  implicit val format: OFormat[AllOtherIncomeReceivedWhilstAbroad] = Json.format[AllOtherIncomeReceivedWhilstAbroad]
}

case class OtherIncomeRfcDetail(countryCode: String,
                                residentialFinancialCostAmount: Option[BigDecimal],
                                broughtFwdResidentialFinancialCostAmount: Option[BigDecimal])

object OtherIncomeRfcDetail {
  implicit val format: OFormat[OtherIncomeRfcDetail] = Json.format[OtherIncomeRfcDetail]
}
