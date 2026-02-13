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

import models._
import play.api.Logging
import play.api.http.Status._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.PagerDutyHelper.PagerDutyKeys._
import utils.PagerDutyHelper._

object GetCalculationListHttpParserLegacy extends APIParser with Logging {
  
  type GetCalculationListResponseLegacy = Either[ErrorModel, Seq[GetCalculationListModelLegacy]]

  override val parserName: String = "GetCalculationListHttpParserLegacy"

  implicit object GetCalculationListHttpReadsLegacy extends HttpReads[GetCalculationListResponseLegacy] {
    
    override def read(method: String, url: String, response: HttpResponse): GetCalculationListResponseLegacy = {
      response.status match {
        case OK => response.json.validate[Seq[GetCalculationListModelLegacy]].fold[GetCalculationListResponseLegacy](
          validationErrors => badSuccessJsonFromAPI(validationErrors),
          parsedModel => Right(parsedModel)
        )
        case NOT_FOUND =>
          logger.info(s"[GetCalculationListHttpParserLegacy][read]: $NOT_FOUND")
          Left(ErrorModel(NOT_FOUND, ErrorBodyModel(NOT_FOUND.toString, "NOT FOUND")))
        case INTERNAL_SERVER_ERROR =>
          pagerDutyLog(INTERNAL_SERVER_ERROR_FROM_API, logMessage(response))
          handleIFError(response)
        case SERVICE_UNAVAILABLE =>
          pagerDutyLog(SERVICE_UNAVAILABLE_FROM_API, logMessage(response))
          handleIFError(response)
        case BAD_REQUEST | CONFLICT | UNPROCESSABLE_ENTITY | FORBIDDEN =>
          pagerDutyLog(FOURXX_RESPONSE_FROM_API, logMessage(response))
          handleIFError(response)
        case _ =>
          pagerDutyLog(UNEXPECTED_RESPONSE_FROM_API, logMessage(response))
          handleIFError(response, Some(INTERNAL_SERVER_ERROR))
      }
    }
  }
}
