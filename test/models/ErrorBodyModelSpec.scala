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
import com.codahale.metrics.SharedMetricRegistries
import play.api.libs.json.{JsObject, Json}
import play.api.http.Status.SERVICE_UNAVAILABLE
import testUtils.TestSuite

class ErrorBodyModelSpec extends TestSuite {
  SharedMetricRegistries.clear()
  val model: ErrorBodyModel = ErrorBodyModel("SERVER_ERROR", "Service is unavailable")
  val jsonModel: JsObject = Json.obj(
    "code" -> "SERVER_ERROR",
    "reason" -> "Service is unavailable"
  )

  val errorsJsModel: JsObject = Json.obj(
    "failures" -> Json.arr(
      Json.obj("code" -> "SERVICE_UNAVAILABLE",
        "reason" -> "The service is currently unavailable"),
      Json.obj("code" -> "INTERNAL_SERVER_ERROR",
        "reason" -> "The service is currently facing issues.")
    )
  )

  "The ErrorModel" should {

    val model = ErrorModel(SERVICE_UNAVAILABLE, ErrorBodyModel("SERVER_ERROR","Service is unavailable"))
    val errorsModel = ErrorModel(SERVICE_UNAVAILABLE, ErrorsBodyModel(Seq(
      ErrorBodyModel("SERVICE_UNAVAILABLE","The service is currently unavailable"),
      ErrorBodyModel("INTERNAL_SERVER_ERROR","The service is currently facing issues.")
    )))

    "parse to Json" in {
      model.toJson mustBe jsonModel
    }
    "parse to Json for multiple errors" in {
      errorsModel.toJson mustBe errorsJsModel
    }
  }

}