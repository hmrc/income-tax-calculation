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

package repositories

import models.mongo.{DatabaseError, EncryptionDecryptionError}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.{and, equal}
import uk.gov.hmrc.mongo.play.json.Codecs.toBson
import utils.EncryptionDecryptionException
import utils.PagerDutyHelper.PagerDutyKeys.ENCRYPTION_DECRYPTION_ERROR
import utils.PagerDutyHelper.pagerDutyLog

trait Repository {
  def filter(nino: String): Bson = and(equal("nino", toBson(nino)))

  def handleEncryptionDecryptionException[T](exception: Exception, startOfMessage: String): Left[DatabaseError, T] = {
    val message: String = exception match {
      case exception: EncryptionDecryptionException => s"${exception.failureReason} ${exception.failureMessage}"
      case _ => exception.getMessage
    }

    pagerDutyLog(ENCRYPTION_DECRYPTION_ERROR, Some(s"$startOfMessage $message"))

    Left(EncryptionDecryptionError(message))
  }
}

object Repository extends Repository
