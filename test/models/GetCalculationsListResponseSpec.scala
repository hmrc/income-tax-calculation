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

package models

import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.{JsValue, Json}
import testUtils.TestSuite

class GetCalculationsListResponseSpec extends TestSuite {

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |  "calculations": [
      |    {
      |      "calculationId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |      "calculationTimestamp": "2019-03-17T09:22:59Z"
      |    }
      |  ]
      |}
    """.stripMargin
  )


  val desJson: JsValue = Json.parse(
    """
      |[
      |	{
      |		"calculationId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |		"calculationTimestamp": "2019-03-17T09:22:59Z"
      |	}
      |]
    """.stripMargin
  )

  val response: CalculationsListResponse = CalculationsListResponse(
    Seq(
      GetCalculationListModel(
        calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
        calculationTimestamp = "2019-03-17T09:22:59Z"
      )
    ))


  "Json writes" must {
    "have the same output as the frontend" in {
      Json.toJson(response) shouldBe mtdJson
    }
  }

  "Json reads" must {
    "align with the API 1404 des spec" in {
      desJson.as[CalculationsListResponse] shouldBe response

    }
  }

}
