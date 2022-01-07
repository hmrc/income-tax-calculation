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
                      totalIncomeReceivedFromAllSources: Option[Int] = None,
                      totalAllowancesAndDeductions: Option[Int] = None,
                      totalTaxableIncome: Option[Int] = None,
                      payPensionsProfit: PayPensionsProfit = PayPensionsProfit(),
                      savingsAndGains: SavingsAndGains = SavingsAndGains(),
                      dividends: Dividends = Dividends(),
                      lumpSums: LumpSums = LumpSums(),
                      gainsOnLifePolicies: GainsOnLifePolicies = GainsOnLifePolicies(),
                      totalReliefs: Option[BigDecimal] = None,
                      totalNotionalTax: Option[BigDecimal] = None,
                      incomeTaxDueAfterTaxReductions: Option[BigDecimal] = None,
                      totalPensionSavingsTaxCharges: Option[BigDecimal] = None,
                      statePensionLumpSumCharges: Option[BigDecimal] = None,
                      payeUnderpaymentsCodedOut: Option[BigDecimal] = None
                    )
object IncomeTax {
  implicit val format: OFormat[IncomeTax] = Json.format[IncomeTax]
}

case class TaxBands(
                     name: Option[String] = None,
                     rate: Option[BigDecimal] = None,
                     bandLimit: Option[Int] = None,
                     apportionedBandLimit: Option[Int] = None,
                     income: Option[Int] = None,
                     taxAmount: Option[BigDecimal] = None
                   )
object TaxBands {
  implicit val format: OFormat[TaxBands] = Json.format[TaxBands]
}

case class PayPensionsProfit(taxBands: Seq[TaxBands] = Seq())
object PayPensionsProfit {
  implicit val format: OFormat[PayPensionsProfit] = Json.format[PayPensionsProfit]
}

case class SavingsAndGains(taxableIncome: Option[Int] = None, taxBands: Seq[TaxBands] = Seq())
object SavingsAndGains {
  implicit val format: OFormat[SavingsAndGains] = Json.format[SavingsAndGains]
}

case class LumpSums(taxBands: Seq[TaxBands] = Seq())
object LumpSums {
  implicit val format: OFormat[LumpSums] = Json.format[LumpSums]
}

case class Dividends(taxableIncome: Option[Int] = None, taxBands: Seq[TaxBands] = Seq())
object Dividends {
  implicit val format: OFormat[Dividends] = Json.format[Dividends]
}

case class GainsOnLifePolicies(taxBands: Seq[TaxBands] = Seq())
object GainsOnLifePolicies {
  implicit val format: OFormat[GainsOnLifePolicies] = Json.format[GainsOnLifePolicies]
}
