/*
 * Copyright 2026 HM Revenue & Customs
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

package connectors.httpParsers.hip

import connectors.httpParsers.APIParser
import models.hip.calculation.taxLiabilitySummaryDataSA302.TaxLiabilitySummaryDataSA302ResponseModel
import models.{ErrorBodyModel, ErrorModel}
import play.api.http.Status.*
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.PagerDutyHelper.PagerDutyKeys.*
import utils.PagerDutyHelper.pagerDutyLog

object HipGetTaxLiabilitySummaryDataSA302DetailsHttpParser extends APIParser {
  type HipGetTaxLiabilitySummaryDataSA302DetailsResponse = Either[ErrorModel, TaxLiabilitySummaryDataSA302ResponseModel]

  override val parserName: String = "TaxLiabilitySummaryDataSA302DetailsHttpParser"

  implicit object GetCalculationDetailsHttpReads extends HttpReads[HipGetTaxLiabilitySummaryDataSA302DetailsResponse] {
    
    override def read(method: String, url: String, response: HttpResponse): HipGetTaxLiabilitySummaryDataSA302DetailsResponse = {
      response.status match {
        case OK => response.json.validate[TaxLiabilitySummaryDataSA302ResponseModel].fold[HipGetTaxLiabilitySummaryDataSA302DetailsResponse](
          validationErrors => badSuccessJsonFromAPI(validationErrors),
          parsedModel => Right(parsedModel)
        )
        case NOT_FOUND =>
          logger.info(s"[HipGetTaxLiabilitySummaryDataSA302DetailsHttpParser][read]: $NOT_FOUND")
          Left(ErrorModel(NOT_FOUND, ErrorBodyModel(NOT_FOUND.toString, "NOT FOUND")))
        case INTERNAL_SERVER_ERROR =>
          pagerDutyLog(INTERNAL_SERVER_ERROR_FROM_API, logMessage(response))
          handleIFError(response)
        case SERVICE_UNAVAILABLE =>
          pagerDutyLog(SERVICE_UNAVAILABLE_FROM_API, logMessage(response))
          handleIFError(response)
        case BAD_REQUEST | CONFLICT | UNPROCESSABLE_ENTITY =>
          pagerDutyLog(FOURXX_RESPONSE_FROM_API, logMessage(response))
          handleIFError(response)
        case _ =>
          pagerDutyLog(UNEXPECTED_RESPONSE_FROM_API, logMessage(response))
          handleIFError(response)
      }
    }
  }
}