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

import play.api.libs.json.{JsValue, Json, OFormat}

sealed trait ErrorBody

case class ErrorModel(status: Int, body: ErrorBody){
  def toJson: JsValue ={
    body match {
      case error: ErrorBodyModel => Json.toJson(error)
      case errors: ErrorsBodyModel => Json.toJson(errors)
    }
  }
}

/** Single Error **/
case class ErrorBodyModel(code: String, reason: String) extends ErrorBody

object ErrorBodyModel {
  implicit val formats: OFormat[ErrorBodyModel] = Json.format[ErrorBodyModel]
  val parsingError: ErrorBodyModel = ErrorBodyModel("PARSING_ERROR", "Error parsing response from API")
}

/** Multiple Errors **/
case class ErrorsBodyModel(failures: Seq[ErrorBodyModel]) extends ErrorBody

object ErrorsBodyModel {
  implicit val formats: OFormat[ErrorsBodyModel] = Json.format[ErrorsBodyModel]
}
