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

package models.mongo

import models.mongo.EncryptedTaxYearsData.dateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.json.{Format, Json, OFormat}
import uk.gov.hmrc.mongo.play.json.formats.MongoJodaFormats
import utils.DecryptableSyntax.DecryptableOps
import utils.DecryptorInstances.intDecryptor
import utils.EncryptableSyntax.EncryptableOps
import utils.EncryptorInstances.intEncryptor
import utils.{EncryptedValue, SecureGCMCipher}

case class TaxYearsData(nino: String,
                        taxYears: Seq[Int],
                        lastUpdated: DateTime = DateTime.now(DateTimeZone.UTC)) {

  def encrypted()(implicit secureGCMCipher: SecureGCMCipher, textAndKey: TextAndKey): EncryptedTaxYearsData = EncryptedTaxYearsData(
    nino = nino,
    taxYears = taxYears.map(_.encrypted),
    lastUpdated = lastUpdated
  )
}

object TaxYearsData {
  implicit val mongoJodaDateTimeFormats: Format[DateTime] = dateTimeFormat

  implicit val format: OFormat[TaxYearsData] = Json.format[TaxYearsData]
}

case class EncryptedTaxYearsData(nino: String,
                                 taxYears: Seq[EncryptedValue],
                                 lastUpdated: DateTime = DateTime.now(DateTimeZone.UTC)) {

  def decrypted()(implicit secureGCMCipher: SecureGCMCipher, textAndKey: TextAndKey): TaxYearsData = TaxYearsData(
    nino = nino,
    taxYears = taxYears.map(_.decrypted),
    lastUpdated = lastUpdated
  )
}

object EncryptedTaxYearsData extends MongoJodaFormats {
  implicit val mongoJodaDateTimeFormats: Format[DateTime] = dateTimeFormat

  implicit val formats: Format[EncryptedTaxYearsData] = Json.format[EncryptedTaxYearsData]
}
