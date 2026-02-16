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

package helpers

import com.github.tomakehurst.wiremock.http.HttpHeader
import models.{GetCalculationListModel, GetCalculationListModelLegacy}
import play.api.http.HeaderNames
import play.api.libs.json.Json

trait CalculationDetailsITestHelper extends WiremockStubHelpers {
  trait Setup {


    val successNino: String = "AA123123A"
    val taxYear = "2026"
    val taxYearUpdated = "25-26"
    val calculationId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"

    val desUrlForListCalcWithoutTaxYear = s"/income-tax/list-of-calculation-results/$successNino"
    val hipCalcListLegacyWithoutTaxYear = s"/itsd/calculations/liability/$successNino"

    val desUrlForListCalcWithTaxYear = s"/income-tax/list-of-calculation-results/$successNino\\?taxYear=$taxYear"
    val hipUrlForListCalcWithTaxYear = s"/itsd/calculations/liability/$successNino\\?taxYear=$taxYear"

    val desUrlForCalculationDetails = s"/income-tax/view/calculations/liability/$successNino/$calculationId"
    val hipUrlForCalculationDetails = s"/itsa/income-tax/v1/$taxYearUpdated/view/calculations/liability/$successNino/$calculationId"
    val ifUrlForTYS24 = s"/income-tax/view/calculations/liability/23-24/$successNino/$calculationId"
    val ifUrlForTYS25 = s"/income-tax/view/calculations/liability/24-25/$successNino/$calculationId"
    val ifUrl2023 = s"/income-tax/view/calculations/liability/$successNino/$calculationId"

    def ifUrlListCalcLegacy(taxYear: String): String = s"/itsd/calculations/liability/$successNino\\?taxYear=$taxYear"

    val ifUrlForCalculationList = s"/income-tax/view/calculations/liability/23-24/$successNino"
    val listCalcResponseLegacy: String = Json.toJson(Seq(GetCalculationListModelLegacy(calculationId, "2019-03-17T09:22:59Z"))).toString()

    val ifGetCalcListUrl26 = "/income-tax/25-26/view/AA123123A/calculations-summary"

    val listCalcResponse: String = Json.toJson(Seq(GetCalculationListModel(
      calculationId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2",
      calculationTimestamp = "2019-03-17T09:22:59Z",
      calculationType = "IY",
      requestedBy = Some("Customer"),
      fromDate = None,
      toDate = None,
      calculationTrigger = None
    ))).toString
    val agentClientCookie: Map[String, String] = Map("MTDITID" -> "555555555")
    val authorization: (String, String) = HeaderNames.AUTHORIZATION -> "mock-bearer-token"
    val mtditidHeader = ("mtditid", "555555555")
    val requestHeaders: Seq[HttpHeader] = Seq(new HttpHeader("mtditid", "555555555"))
    val requestHeaderCalc = Seq(new HttpHeader("correlationId", "239825dd-250d-4d1d-a022-afe5dde7410f"))
    val correlationId = ("correlationId", "239825dd-250d-4d1d-a022-afe5dde7410f")
    auditStubs()
    mergedAuditStubs()
  }
}
