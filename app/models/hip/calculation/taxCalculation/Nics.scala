/*
 * Copyright 2025 HM Revenue & Customs
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

package models.hip.calculation.taxCalculation

import play.api.libs.json._

case class Nics(class2Nics: Option[Class2Nics] = None,
                class4Nics: Option[Class4Nics] = None)

object Nics {
  implicit val format: OFormat[Nics] = Json.format[Nics]
}

case class Class2Nics(amount: Option[BigDecimal] = None)

object Class2Nics {
  implicit val format: OFormat[Class2Nics] = Json.format[Class2Nics]
}

case class Nic4Bands(name: String,
                     rate: BigDecimal,
                     income: Int,
                     amount: BigDecimal
                    )
object Nic4Bands {
  implicit val format: OFormat[Nic4Bands] = Json.format[Nic4Bands]
}

case class Class4Nics(nic4Bands: Seq[Nic4Bands])

object Class4Nics {
  implicit val format: OFormat[Class4Nics] = Json.format[Class4Nics]
}


