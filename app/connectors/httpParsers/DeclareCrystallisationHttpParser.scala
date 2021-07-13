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

import models.DesErrorModel
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import play.api.http.Status._
import utils.PagerDutyHelper.PagerDutyKeys.{FOURXX_RESPONSE_FROM_DES, INTERNAL_SERVER_ERROR_FROM_DES, SERVICE_UNAVAILABLE_FROM_DES, UNEXPECTED_RESPONSE_FROM_DES}
import utils.PagerDutyHelper._

object DeclareCrystallisationHttpParser extends DESParser {
  type DeclareCrystallisationResponse = Either[DesErrorModel, Unit]

  override val parserName: String = "DeclareCrystallisationHttpParser"

  implicit object DeclareCrystallisationHttpReads extends HttpReads[DeclareCrystallisationResponse] {
    override def read(method: String, url: String, response: HttpResponse): DeclareCrystallisationResponse = {
      response.status match {
        case NO_CONTENT => Right(())
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
          handleDESError(response)
      }
    }
  }
}
