package models.hip.calculation.taxCalculation

case class IncomeTax(totalIncomeReceivedFromAllSources: Int,
                     totalAllowancesAndDeductions: Int,
                     totalTaxableIncome: Int,
                     payPensionsProfit: Option[PayPensionsProfit] = None,
                     savingsAndGains: Option[SavingsAndGains] = None,
                     dividends: Option[Dividends] = None,
                     lumpSums: Option[LumpSums] = None,
                     gainsOnLifePolicies: Option[GainsOnLifePolicies] = None,
                     totalReliefs: Option[BigDecimal] = None,
                     totalNotionalTax: Option[BigDecimal] = None,
                     incomeTaxDueAfterTaxReductions: Option[BigDecimal] = None,
                     totalPensionSavingsTaxCharges: Option[BigDecimal] = None,
                     statePensionLumpSumCharges: Option[BigDecimal] = None,
                     payeUnderpaymentsCodedOut: Option[BigDecimal] = None,
                     giftAidTaxChargeWhereBasicRateDiffers: Option[BigDecimal] = None,
                     incomeTaxChargedOnTransitionProfits: Option[BigDecimal] = None,
                     incomeTaxCharged: BigDecimal)


case class PayPensionsProfit(incomeReceived: Int,
                             allowancesAllocated: Int,
                            )

case class SavingsAndGains()

case class Dividends()

case class LumpSums()

case class GainsOnLifePolicies()