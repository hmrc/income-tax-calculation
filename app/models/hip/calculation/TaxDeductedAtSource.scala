package models.hip.calculation

import play.api.libs.json.{Json, OFormat}

case class TaxDeductedAtSource(
                                bbsi: Option[BigDecimal] = None,
                                ukLandAndProperty: Option[BigDecimal] = None,
                                cis: Option[BigDecimal] = None,
                                securities: Option[BigDecimal] = None,
                                voidedIsa: Option[BigDecimal] = None,
                                payeEmployments: Option[BigDecimal] = None,
                                occupationalPensions: Option[BigDecimal] = None,
                                stateBenefits: Option[BigDecimal] = None,
                                specialWithholdingTaxOrUkTaxPaid: Option[BigDecimal] = None,
                                inYearAdjustmentCodedInLaterTaxYear: Option[BigDecimal] = None,
                                taxTakenOffTradingIncome: Option[BigDecimal] = None
                              )

object TaxDeductedAtSource {
  implicit val format: OFormat[TaxDeductedAtSource] = Json.format[TaxDeductedAtSource]
}