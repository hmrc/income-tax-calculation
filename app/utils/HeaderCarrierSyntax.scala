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

package utils

import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames}

object HeaderCarrierSyntax {

  implicit class HeaderCarrierOps(hc: HeaderCarrier) {

    def toSeq: Seq[(String, String)] = {
      Seq(
        HeaderNames.xRequestId            -> hc.requestId.map(_.value),
        HeaderNames.xSessionId            -> hc.sessionId.map(_.value),
        HeaderNames.xForwardedFor         -> hc.forwarded.map(_.value),
        HeaderNames.xRequestChain         -> Some(hc.requestChain.value),
        HeaderNames.authorisation         -> hc.authorization.map(_.value),
        HeaderNames.trueClientIp          -> hc.trueClientIp,
        HeaderNames.trueClientPort        -> hc.trueClientPort,
        HeaderNames.googleAnalyticTokenId -> hc.gaToken,
        HeaderNames.googleAnalyticUserId  -> hc.gaUserId,
        HeaderNames.deviceID              -> hc.deviceID,
        HeaderNames.akamaiReputation      -> hc.akamaiReputation.map(_.value)
      ).collect { case (k, Some(v)) => (k, v) } ++ hc.extraHeaders
    }
  }

}
