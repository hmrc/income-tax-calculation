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

package connectors.httpParsers

import models.{ErrorBodyModel, ErrorModel, ErrorsBodyModel}
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.libs.json.{JsValue, Json, JsonValidationError, __}
import testUtils.TestSuite
import uk.gov.hmrc.http.HttpResponse

class APIParserSpec extends TestSuite {

  object FakeParser extends APIParser {
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
        """[TestParser][read] Received 500 response. Body:{
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
      val result = FakeParser.badSuccessJsonFromAPI(Seq(((__ \ "some" \ "path"), Seq(JsonValidationError(
        messages = Seq("err msg1", "err msg2"))))))
      result mustBe Left(ErrorModel(INTERNAL_SERVER_ERROR,
        ErrorBodyModel("PARSING_ERROR","Error parsing response from API - List((/some/path,List(JsonValidationError(List(err msg1, err msg2),ArraySeq()))))")))
    }
    "handle multiple errors" in {
      val result = FakeParser.handleIFError(httpResponse())
      result mustBe Left(ErrorModel(INTERNAL_SERVER_ERROR,ErrorsBodyModel(Seq(
        ErrorBodyModel("SERVICE_UNAVAILABLE","The service is currently unavailable"),
        ErrorBodyModel("INTERNAL_SERVER_ERROR","The service is currently facing issues.")
      ))))
    }
    "handle single errors" in {
      val result = FakeParser.handleIFError(httpResponse(Json.parse(
        """{"code":"INTERNAL_SERVER_ERROR","reason":"The service is currently facing issues."}""".stripMargin)))
      result mustBe Left(ErrorModel(INTERNAL_SERVER_ERROR,ErrorBodyModel("INTERNAL_SERVER_ERROR","The service is currently facing issues.")))
    }
  }

}