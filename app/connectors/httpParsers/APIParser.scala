/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.libs.json.{JsPath, JsonValidationError}
import uk.gov.hmrc.http.HttpResponse
import utils.PagerDutyHelper.PagerDutyKeys.{BAD_SUCCESS_JSON_FROM_API, UNEXPECTED_RESPONSE_FROM_API}
import utils.PagerDutyHelper.{getCorrelationId, pagerDutyLog}

trait APIParser {

  val parserName : String

  def logMessage(response:HttpResponse): Option[String] ={
    Some(s"[$parserName][read] Received ${response.status} response. Body:${response.body}" + getCorrelationId(response))
  }

  def badSuccessJsonFromAPI[Response](validationErrors: collection.Seq[(JsPath, collection.Seq[JsonValidationError])],
                                      responseAsString: String = "" ): Either[ErrorModel, Response] = {
    pagerDutyLog(BAD_SUCCESS_JSON_FROM_API, Some(s"[$parserName][read] Invalid Json response. " + validationErrors))
    Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("PARSING_ERROR", s"Error parsing response from API - ${responseAsString} - " + validationErrors)))
  }

  def handleIFError[Response](response: HttpResponse, statusOverride: Option[Int] = None, apiNumber: String = ""): Either[ErrorModel, Response] = {

    val status = statusOverride.getOrElse(response.status)

    try {
      val json = response.json

      lazy val apiError = json.asOpt[ErrorBodyModel]
      lazy val apiErrors = json.asOpt[ErrorsBodyModel]

      (apiError, apiErrors) match {
        case (Some(apiError), _) => Left(ErrorModel(status, apiError))
        case (_, Some(apiErrors)) => Left(ErrorModel(status, apiErrors))
        case _ =>
          pagerDutyLog(UNEXPECTED_RESPONSE_FROM_API, Some(s"[$parserName][read] Unexpected Json response."))
          Left(ErrorModel(status, ErrorBodyModel.parsingError(apiNumber)))
      }
    } catch {
      case _:
        Exception =>
        pagerDutyLog(UNEXPECTED_RESPONSE_FROM_API, Some(s"[$parserName][read] Unexpected Json response."))
        Left(ErrorModel(status, ErrorBodyModel.parsingError(apiNumber)))
    }
  }
}
