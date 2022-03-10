/*
 * Copyright 2020 HM Revenue & Customs
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

package models

import models.calculation.CalculationResponseModel
import play.api.libs.json.Json
import testUtils.TestSuite

class ExampleCalculationSpec extends TestSuite {

  val response: String = """{
            |	"metadata": {
            |		"calculationId": "44d13ecf-d653-4ad0-8cc2-ab4d40c55fba",
            |		"requestedBy": "customer",
            |		"periodFrom": "2020-04-06",
            |		"calculationTimestamp": "2022-03-10T11:21:06.326Z",
            |		"taxYear": 2021,
            |		"calculationType": "crystallisation",
            |		"intentToCrystallise": true,
            |		"periodTo": "2021-04-05",
            |		"calculationReason": "customerRequest",
            |		"requestedTimestamp": "2022-03-10T11:21:03.626Z"
            |	},
            |	"inputs": {
            |		"personalInformation": {
            |			"identifier": "BE036433A",
            |			"statePensionAgeDate": "2027-05-31",
            |			"dateOfBirth": "1960-10-31",
            |			"uniqueTaxpayerReference": "6669036433",
            |			"taxRegime": "UK"
            |		},
            |		"incomeSources": {
            |			"businessIncomeSources": [{
            |				"accountingPeriodStartDate": "2020-04-06",
            |				"source": "MTD-SA",
            |				"incomeSourceType": "01",
            |				"finalisationTimestamp": "2022-03-04T10:13:31.000Z",
            |				"incomeSourceName": "ABC Pvt Ltd",
            |				"finalised": true,
            |				"submissionPeriods": [{
            |					"periodId": "2020040620200705",
            |					"startDate": "2020-04-06",
            |					"endDate": "2020-07-05",
            |					"receivedDateTime": "2022-03-04T10:12:23.401Z"
            |				}, {
            |					"periodId": "2021010620210405",
            |					"startDate": "2021-01-06",
            |					"endDate": "2021-04-05",
            |					"receivedDateTime": "2022-03-04T10:13:05.618Z"
            |				}, {
            |					"periodId": "2020070620201005",
            |					"startDate": "2020-07-06",
            |					"endDate": "2020-10-05",
            |					"receivedDateTime": "2022-03-04T10:12:51.723Z"
            |				}, {
            |					"periodId": "2020100620210105",
            |					"startDate": "2020-10-06",
            |					"endDate": "2021-01-05",
            |					"receivedDateTime": "2022-03-04T10:13:04.339Z"
            |				}],
            |				"incomeSourceId": "XHIS00000007697",
            |				"latestPeriodEndDate": "2021-04-05",
            |				"latestReceivedDateTime": "2022-03-04T10:13:05.618Z",
            |				"accountingPeriodEndDate": "2021-04-05"
            |			}]
            |		}
            |	},
            |	"calculation": {
            |		"taxCalculation": {
            |			"incomeTax": {
            |				"incomeTaxCharged": 11500,
            |				"totalTaxableIncome": 47500,
            |				"totalIncomeReceivedFromAllSources": 60000,
            |				"totalAllowancesAndDeductions": 12500,
            |				"payPensionsProfit": {
            |					"taxableIncome": 47500,
            |					"incomeTaxAmount": 11500,
            |					"incomeReceived": 60000,
            |					"allowancesAllocated": 12500,
            |					"taxBands": [{
            |						"rate": 20,
            |						"name": "BRT",
            |						"bandLimit": 37500,
            |						"taxAmount": 7500,
            |						"income": 37500,
            |						"apportionedBandLimit": 37500
            |					}, {
            |						"rate": 40,
            |						"name": "HRT",
            |						"bandLimit": 112500,
            |						"taxAmount": 4000,
            |						"income": 10000,
            |						"apportionedBandLimit": 112500
            |					}]
            |				}
            |			},
            |			"nics": {
            |				"class4Nics": {
            |					"totalIncomeLiableToClass4Charge": 60000,
            |					"totalIncomeChargeableToClass4": 60000,
            |					"totalAmount": 3845,
            |					"nic4Bands": [{
            |						"rate": 0,
            |						"name": "ZRT",
            |						"amount": 0,
            |						"income": 9500,
            |						"apportionedThreshold": 9500,
            |						"threshold": 9500
            |					}, {
            |						"rate": 9,
            |						"name": "BRT",
            |						"amount": 3645,
            |						"income": 40500,
            |						"apportionedThreshold": 50000,
            |						"threshold": 50000
            |					}, {
            |						"rate": 2,
            |						"name": "HRT",
            |						"amount": 200,
            |						"income": 10000,
            |						"apportionedThreshold": 99999999,
            |						"threshold": 99999999
            |					}]
            |				},
            |				"nic4NetOfDeductions": 3845,
            |				"totalNic": 3845
            |			},
            |			"totalIncomeTaxAndNicsDue": 15345
            |		},
            |		"incomeSummaryTotals": {
            |			"totalSelfEmploymentProfit": 60000
            |		},
            |		"allowancesAndDeductions": {
            |			"personalAllowance": 12500
            |		},
            |		"previousCalculation": {
            |			"calculationTimestamp": "2022-03-10T11:10:21.684Z",
            |			"calculationId": "1e4a27d6-ac07-4e19-97c9-ea8c24928b3d",
            |			"totalIncomeTaxAndNicsDue": 15345,
            |			"incomeTaxNicDueThisPeriod": 0
            |		},
            |		"businessProfitAndLoss": [{
            |			"incomeSourceType": "01",
            |			"totalIncome": 60000,
            |			"netProfit": 60000,
            |			"incomeSourceName": "ABC Pvt Ltd",
            |			"totalExpenses": 0,
            |			"taxableProfit": 60000,
            |			"incomeSourceId": "XHIS00000007697"
            |		}]
            |	}
            |}""".stripMargin

  "response" should {
    "parse to model" in {
      Json.parse(response).asOpt[CalculationResponseModel].isDefined mustBe true
    }
  }
}
