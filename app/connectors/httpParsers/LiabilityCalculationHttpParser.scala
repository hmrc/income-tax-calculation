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

import models._
import play.api.http.Status._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.PagerDutyHelper._
import PagerDutyKeys._


object LiabilityCalculationHttpParser {
  type LiabilityCalculationResponse = Either[DesErrorModel, LiabilityCalculationIdModel]

  implicit object CreateIncomeSourcesHttpReads extends HttpReads[LiabilityCalculationResponse] {
    override def read(method: String, url: String, response: HttpResponse): LiabilityCalculationResponse = {
      response.status match {
        case OK => response.json.validate[LiabilityCalculationIdModel].fold[LiabilityCalculationResponse](
          _ => {
            pagerDutyLog(BAD_SUCCESS_JSON_FROM_DES, Some(s"[LiabilityCalculationHttpParser][read] Invalid Json from DES."))
            Left(DesErrorModel(INTERNAL_SERVER_ERROR, DesErrorBodyModel.parsingError))},
          parsedModel => Right(parsedModel)
        )
        case INTERNAL_SERVER_ERROR =>
          pagerDutyLog(INTERNAL_SERVER_ERROR_FROM_DES, logMessage(response))
          handleDESError(response)
        case SERVICE_UNAVAILABLE =>
          pagerDutyLog(SERVICE_UNAVAILABLE_FROM_DES, logMessage(response))
          handleDESError(response)
        case BAD_REQUEST | NOT_FOUND | CONFLICT =>
          pagerDutyLog(FOURXX_RESPONSE_FROM_DES, logMessage(response))
          handleDESError(response)
        case _ =>
          pagerDutyLog(UNEXPECTED_RESPONSE_FROM_DES, logMessage(response))
          handleDESError(response, Some(INTERNAL_SERVER_ERROR))
      }
    }
  }

  private def logMessage(response:HttpResponse): Option[String] = {

    val addedId = (response.header("CorrelationId"), response.header("sessionId")) match {
      case (Some(id), _) => s" CorrelationId: $id"
      case (_, Some(id)) => s" SessionId: $id"
      case _ => ""
    }

    Some(s"[LiabilityCalculationHttpParser][read] Received ${response.status} from DES. Body:${response.body}. " + addedId)
  }

  private def handleDESError(response: HttpResponse, statusOverride: Option[Int] = None): LiabilityCalculationResponse = {

    val status = statusOverride.getOrElse(response.status)
    try {
      response.json.validate[DesErrorBodyModel].fold[LiabilityCalculationResponse](
        _ => {
          pagerDutyLog(UNEXPECTED_RESPONSE_FROM_DES, Some(s"[LiabilityCalculationHttpParser][read] Unexpected Json from DES."))
          Left(DesErrorModel(status, DesErrorBodyModel.parsingError))
        },
        parsedError => Left(DesErrorModel(status, parsedError))
      )
    } catch {
      case _: Exception => Left(DesErrorModel(status, DesErrorBodyModel.parsingError))
    }
  }

}
