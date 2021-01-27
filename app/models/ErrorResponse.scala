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

package models

import play.api.http.Status._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

sealed class ErrorResponse(val httpStatusCode: Int, val errorCode: String, val errorMsg: String)

object ErrorResponse {

  implicit val writes: Writes[ErrorResponse] =  (
    (JsPath \ "code").write[String] and
    (JsPath \ "message").write[String]
  ) (error => (error.errorCode, error.errorMsg))
}

case object InternalServerError extends ErrorResponse(
  INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "A downstream service didn't respond as expected"
)

case object ServiceUnavailableError extends ErrorResponse(
  SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", "The service is temporarily unavailable"
)

case object NotFoundError extends ErrorResponse(
  NOT_FOUND, "NOT_FOUND", "The data requested could not be found"
)

case object DesInvalidNinoError extends ErrorResponse(
  BAD_REQUEST, "INVALID_NINO", "The data requested could not be found"
)

case object DesInvalidTaxYearError extends ErrorResponse(
  BAD_REQUEST, "INVALID_TAX_YEAR", "The data requested could not be found"
)

case object DesInvalidTaxCrystalliseError extends ErrorResponse(
  BAD_REQUEST, "INVALID_TAX_CRYSTALLISE", "The data requested could not be found"
)

case object DesInvalidRequestError extends ErrorResponse(
  BAD_REQUEST, "INVALID_REQUEST", "The data requested could not be found"
)

case object DesNoSubmissionExistError extends ErrorResponse(
  FORBIDDEN, "NO_SUBMISSION_EXIST", "The data requested could not be found"
)

case object DesConflictError extends ErrorResponse(
  CONFLICT, "CONFLICT", "The data requested could not be found"
)

case object DesServerError extends ErrorResponse(
  INTERNAL_SERVER_ERROR, "SERVER_ERROR", "The data requested could not be found"
)

case object DesServiceUnavailableError extends ErrorResponse(
  SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", "The data requested could not be found"
)
