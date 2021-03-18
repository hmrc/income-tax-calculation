/*
 * Copyright 2021 HM Revenue & Customs
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

package connectors.httpParsers

import controllers.Assets.INTERNAL_SERVER_ERROR
import models.{DesErrorBodyModel, DesErrorModel, DesErrorsBodyModel}
import play.api.libs.json.{JsValue, Json}
import testUtils.TestSuite
import uk.gov.hmrc.http.HttpResponse

class DESParserSpec extends TestSuite{

  object FakeParser extends DESParser {
    override val parserName: String = "TestParser"
  }

  def httpResponse(json: JsValue =
                   Json.parse(
                     """{"failures":[
                       |{"code":"SERVICE_UNAVAILABLE","reason":"The service is currently unavailable"},
                       |{"code":"INTERNAL_SERVER_ERROR","reason":"The service is currently facing issues."}]}""".stripMargin)): HttpResponse = HttpResponse(
    INTERNAL_SERVER_ERROR,
    json,
    Map("CorrelationId" -> Seq("1234645654645"))
  )

  "FakeParser" should {
    "log the correct message" in {
      val result = FakeParser.logMessage(httpResponse())
      result mustBe Some(
        """[TestParser][read] Received 500 from DES. Body:{
          |  "failures" : [ {
          |    "code" : "SERVICE_UNAVAILABLE",
          |    "reason" : "The service is currently unavailable"
          |  }, {
          |    "code" : "INTERNAL_SERVER_ERROR",
          |    "reason" : "The service is currently facing issues."
          |  } ]
          |} CorrelationId: 1234645654645""".stripMargin)
    }
    "return the the correct error" in {
      val result = FakeParser.badSuccessJsonFromDES
      result mustBe Left(DesErrorModel(INTERNAL_SERVER_ERROR,DesErrorBodyModel("PARSING_ERROR","Error parsing response from DES")))
    }
    "handle multiple errors" in {
      val result = FakeParser.handleDESError(httpResponse())
      result mustBe Left(DesErrorModel(INTERNAL_SERVER_ERROR,DesErrorsBodyModel(Seq(
        DesErrorBodyModel("SERVICE_UNAVAILABLE","The service is currently unavailable"),
        DesErrorBodyModel("INTERNAL_SERVER_ERROR","The service is currently facing issues.")
      ))))
    }
    "handle single errors" in {
      val result = FakeParser.handleDESError(httpResponse(Json.parse(
        """{"code":"INTERNAL_SERVER_ERROR","reason":"The service is currently facing issues."}""".stripMargin)))
      result mustBe Left(DesErrorModel(INTERNAL_SERVER_ERROR,DesErrorBodyModel("INTERNAL_SERVER_ERROR","The service is currently facing issues.")))
    }
  }

}