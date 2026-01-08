/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.libs.json.{JsValue, Json}
import testUtils.TestSuite

class GetCalculationsListModelSpec extends TestSuite {

  val responseJson: JsValue =
    Json.parse(
      """
        |{
        |  "calculationId": "1d35cfe4-cd23-22b2-b074-fae6052024p1",
        |  "calculationTimestamp": "2023-09-30T09:15:34.0Z",
        |  "calculationType": "IY",
        |  "requestedBy": "Customer",
        |  "fromDate": "2013-05-d1",
        |  "toDate": "2016-05-d1",
        |  "calculationOutcome": "Processed"
        |}
    """.stripMargin
    )

  val largerJsonExample =
    Json.parse(
      """
        |{
        |  "calculationId": "abc123",
        |  "calculationTimestamp": "2024-01-01T10:00:00Z",
        |  "calculationType": "IY",
        |  "requestedBy": "Customer",
        |  "fromDate": "2023-04-06",
        |  "toDate": "2024-04-05",
        |  "calculationOutcome": "Success",
        |
        |  "someHugeNestedObject": {
        |    "foo": "bar",
        |    "baz": [1, 2, 3]
        |  },
        |  "irrelevantField": 123,
        |  "anotherOne": true
        |}
        |""".stripMargin
    )

  val response: GetCalculationListModel =
    GetCalculationListModel(
      calculationId = "1d35cfe4-cd23-22b2-b074-fae6052024p1",
      calculationTimestamp = "2023-09-30T09:15:34.0Z",
      calculationType = "IY",
      requestedBy = Some("Customer"),
      fromDate = Some("2013-05-d1"),
      toDate = Some("2016-05-d1"),
      calculationOutcome = Some("Processed")
    )

  val largerJsonResponse: GetCalculationListModel =
    GetCalculationListModel(
      calculationId = "abc123",
      calculationTimestamp = "2024-01-01T10:00:00Z",
      calculationType = "IY",
      requestedBy = Some("Customer"),
      fromDate = Some("2023-04-06"),
      toDate = Some("2024-04-05"),
      calculationOutcome = Some("Success")
    )


  "Json writes" must {
    "write to json correctly from scala" in {
      Json.toJson(response) mustBe responseJson
    }
  }

  "Json reads" must {
    "read correctly from json" in {
      responseJson.as[GetCalculationListModel] mustBe response
    }

    "read only the fields from larger json" in {
      largerJsonExample.as[GetCalculationListModel] mustBe largerJsonResponse
    }
  }

}
