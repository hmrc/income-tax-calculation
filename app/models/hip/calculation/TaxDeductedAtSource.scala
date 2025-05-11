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

case class TaxDeductedAtSource(
                                bbsi: Option[BigDecimal] = None,
                                ukLandAndProperty: Option[BigDecimal] = None,
                                cis: Option[BigDecimal] = None,
                                voidedIsa: Option[BigDecimal] = None,
                                payeEmployments: Option[BigDecimal] = None,
                                occupationalPensions: Option[BigDecimal] = None,
                                stateBenefits: Option[BigDecimal] = None,
                                specialWithholdingTaxOrUkTaxPaid: Option[BigDecimal] = None,
                                inYearAdjustmentCodedInLaterTaxYear: Option[BigDecimal] = None,
                                taxTakenOffTradingIncome: Option[BigDecimal] = None
                              )

object TaxDeductedAtSource {
  implicit val writes: Writes[TaxDeductedAtSource] = Json.writes[TaxDeductedAtSource]

  implicit val reads: Reads[TaxDeductedAtSource] = (
    (__ \ "bbsi").readNullable[BigDecimal] and
      (__ \ "ukLandAndProperty").readNullable[BigDecimal] and
      (__ \ "cis").readNullable[BigDecimal] and
      (__ \ "voidedIsa").readNullable[BigDecimal] and
      (__ \ "payeEmployments").readNullable[BigDecimal] and
      (__ \ "occupationalPensions").readNullable[BigDecimal] and
      (__ \ "stateBenefits").readNullable[BigDecimal] and
      (__ \ "specialWithholdingTaxOrUkTaxPaid").readNullable[BigDecimal] and
      (__ \ "inYearAdjustmentCodedInLaterTaxYear").readNullable[BigDecimal] and
      (__ \ "taxTakenOffTradingIncome").readNullable[BigDecimal])(TaxDeductedAtSource.apply _)
}