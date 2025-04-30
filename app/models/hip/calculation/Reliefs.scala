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

import models.hip.calculation.reliefs.residentialFinanceCosts.ResidentialFinanceCosts
import play.api.libs.json.{Json, OFormat}

case class Reliefs(residentialFinanceCosts: Option[ResidentialFinanceCosts],
                   reliefsClaimed: Option[List[ReliefsClaimed]],
                   foreignTaxCreditRelief: ForeignTaxCreditRelief,
                   topSlicingRelief: Option[TopSlicingRelief],
                   basicRateExtension: Option[BasicRateExtension],
                   giftAidTaxReductionWhereBasicRateDiffers: Option[GiftAidTaxReductionWhereBasicRateDiffers])

object Reliefs {
  implicit val format: OFormat[Reliefs] = Json.format[Reliefs]
}

case class ReliefsClaimed(`type`: String,
                          amountClaimed: Option[BigDecimal],
                          allowableAmount: Option[BigDecimal],
                          amountUsed: Option[BigDecimal],
                          rate: Option[BigDecimal],
                          reliefsClaimedDetail: Option[List[ReliefsClaimedDetail]])

object ReliefsClaimed {
  implicit val format: OFormat[ReliefsClaimed] = Json.format[ReliefsClaimed]
}


case class ReliefsClaimedDetail(amountClaimed: Option[BigDecimal],
                                uniqueInvestmentRef: Option[String],
                                name: Option[String],
                                socialEnterpriseName: Option[String],
                                companyName: Option[String],
                                deficiencyReliefType: Option[String],
                                customerReference: Option[String])

object ReliefsClaimedDetail {
  implicit val format: OFormat[ReliefsClaimedDetail] = Json.format[ReliefsClaimedDetail]
}

case class ForeignTaxCreditRelief(customerCalculatedRelief: Option[Boolean],
                                  totalForeignTaxCreditRelief: BigDecimal,
                                  foreignTaxCreditReliefOnProperty: BigDecimal,
                                  foreignTaxCreditReliefOnDividends: BigDecimal,
                                  foreignTaxCreditReliefOnSavings: BigDecimal,
                                  foreignTaxCreditReliefOnForeignIncome: BigDecimal,
                                  foreignTaxCreditReliefDetail: List[ForeignTaxCreditReliefDetail])

object ForeignTaxCreditRelief {
  implicit val format: OFormat[ForeignTaxCreditRelief] = Json.format[ForeignTaxCreditRelief]
}

case class ForeignTaxCreditReliefDetail(incomeSourceType: Option[String],
                                        incomeSourceId: Option[String],
                                        countryCode: String,
                                        foreignIncome: BigDecimal,
                                        foreignTax: Option[BigDecimal],
                                        dtaRate: Option[BigDecimal],
                                        dtaAmount: Option[BigDecimal],
                                        ukLiabilityOnIncome: Option[BigDecimal],
                                        foreignTaxCredit: BigDecimal,
                                        employmentLumpSum: Option[Boolean])

object ForeignTaxCreditReliefDetail {
  implicit val format: OFormat[ForeignTaxCreditReliefDetail] = Json.format[ForeignTaxCreditReliefDetail]
}

case class TopSlicingRelief(amount: Option[BigDecimal] = None)

object TopSlicingRelief {
  implicit val format: OFormat[TopSlicingRelief] = Json.format[TopSlicingRelief]
}

case class BasicRateExtension(totalBasicRateExtension: Option[BigDecimal],
                              giftAidRelief: Option[Int],
                              pensionContributionReliefs: Option[BigDecimal])

object BasicRateExtension {
  implicit val format: OFormat[BasicRateExtension] = Json.format[BasicRateExtension]
}

case class GiftAidTaxReductionWhereBasicRateDiffers(amount: BigDecimal)

object GiftAidTaxReductionWhereBasicRateDiffers {
  implicit val format: OFormat[GiftAidTaxReductionWhereBasicRateDiffers] = Json.format[GiftAidTaxReductionWhereBasicRateDiffers]
}