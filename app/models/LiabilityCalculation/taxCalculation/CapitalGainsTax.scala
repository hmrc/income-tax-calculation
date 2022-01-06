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

case class CapitalGainsTax(
                            totalCapitalGainsIncome: Option[BigDecimal] = None,
                            adjustments: Option[BigDecimal] = None,
                            foreignTaxCreditRelief: Option[BigDecimal] = None,
                            taxOnGainsAlreadyPaid: Option[BigDecimal] = None,
                            capitalGainsTaxDue: Option[BigDecimal] = None,
                            capitalGainsOverpaid: Option[BigDecimal] = None,
                            residentialPropertyAndCarriedInterest: ResidentialPropertyAndCarriedInterest = ResidentialPropertyAndCarriedInterest(),
                            businessAssetsDisposalsAndInvestorsRel: BusinessAssetsDisposalsAndInvestorsRel = BusinessAssetsDisposalsAndInvestorsRel()
                          )
object CapitalGainsTax {
  implicit val writes: OWrites[CapitalGainsTax] = Json.writes[CapitalGainsTax]
  implicit val reads: Reads[CapitalGainsTax] = (
    (JsPath \ "totalCapitalGainsIncome").readNullable[BigDecimal] and
      (JsPath \ "adjustments").readNullable[BigDecimal] and
      (JsPath \ "foreignTaxCreditRelief").readNullable[BigDecimal] and
      (JsPath \ "taxOnGainsAlreadyPaid").readNullable[BigDecimal] and
      (JsPath \ "capitalGainsTaxDue").readNullable[BigDecimal] and
      (JsPath \ "capitalGainsOverpaid").readNullable[BigDecimal] and
      (JsPath \ "residentialPropertyAndCarriedInterest").read[ResidentialPropertyAndCarriedInterest] and
      (JsPath \ "businessAssetsDisposalsAndInvestorsRel").read[BusinessAssetsDisposalsAndInvestorsRel]
    ) (CapitalGainsTax.apply _)
}

case class ResidentialPropertyAndCarriedInterest(
                                                  cgtTaxBands: Seq[CgtTaxBands] = Seq()
                                                )
object ResidentialPropertyAndCarriedInterest {
  implicit val format: OFormat[ResidentialPropertyAndCarriedInterest] = Json.format[ResidentialPropertyAndCarriedInterest]
}

case class CgtTaxBands(
                        name: Option[String] = None,
                        rate: Option[BigDecimal] = None,
                        income: Option[BigDecimal] = None,
                        taxAmount: Option[BigDecimal] = None
                      )
object CgtTaxBands {
  implicit val format: OFormat[CgtTaxBands] = Json.format[CgtTaxBands]
}

case class BusinessAssetsDisposalsAndInvestorsRel(
                                                   taxableGains: Option[BigDecimal] = None,
                                                   rate: Option[BigDecimal] = None,
                                                   taxAmount  : Option[BigDecimal] = None
                                                 )
object BusinessAssetsDisposalsAndInvestorsRel {
  implicit val format: OFormat[BusinessAssetsDisposalsAndInvestorsRel] = Json.format[BusinessAssetsDisposalsAndInvestorsRel]
}