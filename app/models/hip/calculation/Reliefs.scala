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

case class Reliefs(residentialFinanceCosts: Option[ResidentialFinanceCosts],
                   reliefsClaimed: Option[Seq[ReliefsClaimed]],
                   foreignTaxCreditRelief: Option[ForeignTaxCreditRelief],
                   topSlicingRelief: Option[TopSlicingRelief],
                   giftAidTaxReductionWhereBasicRateDiffers: Option[GiftAidTaxReductionWhereBasicRateDiffers])

object Reliefs {
  implicit val format: OFormat[Reliefs] = Json.format[Reliefs]
}

case class ResidentialFinanceCosts(totalResidentialFinanceCostsRelief: BigDecimal)

object ResidentialFinanceCosts {
  implicit val format: OFormat[ResidentialFinanceCosts] = Json.format[ResidentialFinanceCosts]
}

case class ReliefsClaimed(`type`: String, amountUsed: Option[BigDecimal] = None)

object ReliefsClaimed {
  implicit val format: OFormat[ReliefsClaimed] = Json.format[ReliefsClaimed]
}

case class ForeignTaxCreditRelief(totalForeignTaxCreditRelief: BigDecimal)

object ForeignTaxCreditRelief {
  implicit val format: OFormat[ForeignTaxCreditRelief] = Json.format[ForeignTaxCreditRelief]
}

case class TopSlicingRelief(amount: Option[BigDecimal] = None)

object TopSlicingRelief {
  implicit val format: OFormat[TopSlicingRelief] = Json.format[TopSlicingRelief]
}

case class GiftAidTaxReductionWhereBasicRateDiffers(amount: Option[BigDecimal] = None)

object GiftAidTaxReductionWhereBasicRateDiffers {
  implicit val format: OFormat[GiftAidTaxReductionWhereBasicRateDiffers] = Json.format[GiftAidTaxReductionWhereBasicRateDiffers]
}