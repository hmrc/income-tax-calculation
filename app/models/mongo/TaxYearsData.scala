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

package models.mongo

import models.mongo.TaxYearsData.dateTimeFormat
import play.api.libs.json._
import utils.DecryptableSyntax.DecryptableOps
import utils.DecryptorInstances.intDecryptor
import utils.EncryptableSyntax.EncryptableOps
import utils.EncryptorInstances.intEncryptor
import utils.{EncryptedValue, SecureGCMCipher}

import java.time.LocalDate

case class TaxYearsData(nino: String,
                        taxYears: Seq[Int],
                        lastUpdated: LocalDate = LocalDate.now()){  //DateTimeZone.UTC)) {

  def encrypted()(implicit secureGCMCipher: SecureGCMCipher, textAndKey: TextAndKey): EncryptedTaxYearsData = EncryptedTaxYearsData(
    nino = nino,
    taxYears = taxYears.map(_.encrypted),
    lastUpdated = lastUpdated
  )
}

object TaxYearsData {
  final val dateTimeReads: Reads[LocalDate] =
    Reads.at[String](__ \ "$date" \ "$numberLong")
      .map(dateTime => LocalDate.ofEpochDay(dateTime.toLong)) //, DateTimeZone.UTC))

  final val dateTimeWrites: Writes[LocalDate] =
    Writes.at[String](__ \ "$date" \ "$numberLong")
      .contramap[LocalDate](x => x.toEpochDay.toString)

  val dateTimeFormat: Format[LocalDate] = Format(dateTimeReads, dateTimeWrites)

  implicit val mongoJodaDateTimeFormats: Format[LocalDate] = dateTimeFormat

  implicit val format: OFormat[TaxYearsData] = Json.format[TaxYearsData]
}

case class EncryptedTaxYearsData(nino: String,
                                 taxYears: Seq[EncryptedValue],
                                 lastUpdated: LocalDate = LocalDate.now()){ // DateTimeZone.UTC)) {

  def decrypted()(implicit secureGCMCipher: SecureGCMCipher, textAndKey: TextAndKey): TaxYearsData = TaxYearsData(
    nino = nino,
    taxYears = taxYears.map(_.decrypted),
    lastUpdated = lastUpdated
  )
}

object EncryptedTaxYearsData  {
  implicit val mongoJodaDateTimeFormats: Format[LocalDate] = dateTimeFormat

  implicit val formats: Format[EncryptedTaxYearsData] = Json.format[EncryptedTaxYearsData]
}
