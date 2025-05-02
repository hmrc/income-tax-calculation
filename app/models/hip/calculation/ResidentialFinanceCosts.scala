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

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class ResidentialFinanceCosts(adjustedTotalIncome: BigDecimal,
                                   relievableAmount: BigDecimal,
                                   rate: BigDecimal,
                                   totalResidentialFinanceCostsRelief: BigDecimal)

object ResidentialFinanceCosts {
  implicit val writes: Writes[ResidentialFinanceCosts] = Json.writes[ResidentialFinanceCosts]

  implicit val reads: Reads[ResidentialFinanceCosts] = (
    (__ \ "adjustedTotalIncome").read[BigDecimal] and
      (__ \ "relievableAmount").read[BigDecimal] and
      (__ \ "rate").read[BigDecimal] and
      (__ \ "totalResidentialFinanceCostsRelief").read[BigDecimal]
  )(ResidentialFinanceCosts.apply _)

}