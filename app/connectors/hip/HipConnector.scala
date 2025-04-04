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

package connectors.hip

import config.AppConfig
import uk.gov.hmrc.http.{Authorization, HeaderCarrier}

import java.util.Base64

trait HipConnector {

  val appConfig: AppConfig

  private[connectors] def hipHeaderCarrier(apiNumber: String)(implicit hc: HeaderCarrier): HeaderCarrier = {
    val clientId = appConfig.hipClientId(s"1404")
    val secret = appConfig.hipSecret(s"1404")
    val encoded = Base64.getEncoder.encodeToString(s"$clientId:$secret".getBytes("UTF-8"))
    hc.copy(authorization = Some(Authorization(s"Basic $encoded")))
  }

}
