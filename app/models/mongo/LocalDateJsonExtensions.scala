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

import play.api.libs.json.{Reads, Writes, __}

import java.time.{Instant, LocalDate, LocalDateTime, ZoneOffset}

object LocalDateJsonExtensions {

  final val dateTimeReads: Reads[LocalDateTime] =
    Reads.at[String](__ \ "$date" \ "$numberLong")
      .map(instant => LocalDateTime.ofInstant( Instant.ofEpochMilli(instant.toLong), ZoneOffset.UTC ) )

  final val dateTimeWrites: Writes[LocalDateTime] =
    Writes.at[String](__ \ "$date" \ "$numberLong")
      .contramap[LocalDateTime](x => x.toInstant(ZoneOffset.UTC).toEpochMilli.toString)

}
