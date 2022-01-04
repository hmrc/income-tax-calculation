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

package models.LiabilityCalculation.reliefs

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Reliefs(
                   reliefsClaimed: Seq[ReliefsClaimed] = Seq(ReliefsClaimed()),
                   residentialFinanceCosts: ResidentialFinanceCosts = ResidentialFinanceCosts(),
                   foreignTaxCreditRelief: ForeignTaxCreditRelief = ForeignTaxCreditRelief(),
                   topSlicingRelief: TopSlicingRelief = TopSlicingRelief()
                 )
object Reliefs {
  implicit val writes: OWrites[Reliefs] = Json.writes[Reliefs]
  implicit val reads: Reads[Reliefs] = (
    (JsPath \ "reliefsClaimed").read[Seq[ReliefsClaimed]] and
      (JsPath \ "residentialFinanceCosts").read[ResidentialFinanceCosts] and
      (JsPath \ "foreignTaxCreditRelief").read[ForeignTaxCreditRelief] and
      (JsPath \ "topSlicingRelief").read[TopSlicingRelief]
    ) (Reliefs.apply _)
}

case class ReliefsClaimed(`type`: Option[String] = None,
                          amountUsed: Option[BigDecimal] = None)

object ReliefsClaimed {
  implicit val writes: OWrites[ReliefsClaimed] = Json.writes[ReliefsClaimed]
  implicit val reads: Reads[ReliefsClaimed] = (
    (JsPath \ "type").readNullable[String].map {
      case Some("nonDeductableLoanInterest") => Some("nonDeductibleLoanInterest")
      case other => other
    } and
      (JsPath \ "amountUsed").readNullable[BigDecimal]
    )(ReliefsClaimed.apply _)
}

case class ResidentialFinanceCosts(totalResidentialFinanceCostsRelief: Option[BigDecimal] = None)
object ResidentialFinanceCosts {
  implicit val format: OFormat[ResidentialFinanceCosts] = Json.format[ResidentialFinanceCosts]
}

case class ForeignTaxCreditRelief(totalForeignTaxCreditRelief: Option[BigDecimal] = None)
object ForeignTaxCreditRelief {
  implicit val format: OFormat[ForeignTaxCreditRelief] = Json.format[ForeignTaxCreditRelief]
}

case class TopSlicingRelief(amount: Option[BigDecimal] = None)
object TopSlicingRelief {
  implicit val format: OFormat[TopSlicingRelief] = Json.format[TopSlicingRelief]
}

case class SavingsAndGains(taxableIncome: Option[Int] = None)
object SavingsAndGains {
  implicit val format: OFormat[SavingsAndGains] = Json.format[SavingsAndGains]
}

