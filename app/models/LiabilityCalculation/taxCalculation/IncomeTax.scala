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

package models.LiabilityCalculation.taxCalculation

import play.api.libs.json.{JsPath, Json, OFormat, OWrites, Reads}
import play.api.libs.functional.syntax._

case class IncomeTax(
                      dividends: Dividends = Dividends(),
                      savingsAndGains: SavingsAndGains = SavingsAndGains(),
                      totalReliefs: Option[BigDecimal] = None,
                      totalIncomeReceivedFromAllSources: Option[Int] = None,
                      totalAllowancesAndDeductions: Option[Int] = None,
                      totalTaxableIncome: Option[Int] = None
                    )
object IncomeTax {
  implicit val writes: OWrites[IncomeTax] = Json.writes[IncomeTax]
  implicit val reads: Reads[IncomeTax] = (
    (JsPath \ "dividends").read[Dividends] and
      (JsPath \ "savingsAndGains").read[SavingsAndGains] and
      (JsPath \ "totalReliefs").readNullable[BigDecimal] and
      (JsPath \ "totalIncomeReceivedFromAllSources").readNullable[Int] and
      (JsPath \ "totalAllowancesAndDeductions").readNullable[Int] and
      (JsPath \ "totalTaxableIncome").readNullable[Int]
    ) (IncomeTax.apply _)
}

case class Dividends(taxableIncome: Option[Int] = None)
object Dividends {
  implicit val format: OFormat[Dividends] = Json.format[Dividends]
}

case class SavingsAndGains(taxableIncome: Option[Int] = None)
object SavingsAndGains {
  implicit val format: OFormat[SavingsAndGains] = Json.format[SavingsAndGains]
}
