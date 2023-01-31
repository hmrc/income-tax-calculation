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

package utils

object TaxYear {

  def convert(taxYear: Option[String]): Either[String, Int] = {

    def isValidFormat(year: String): Boolean = year.length == 4 && year.forall(_.isDigit)

    taxYear match {
      case Some(year) if isValidFormat(year) => Right(year.toInt)
      case _ => Left("Failed to parse Tax year")
    }
  }

  def updatedFormat(year: String): String = {
    val endYear: String = year.takeRight(2)
    val startYear: String = (year.toInt - 1).toString.takeRight(2)
    s"$startYear-$endYear"
  }

  def convertSpecificTaxYear(taxYear: String): String =
    s"${(taxYear.toInt - 1).toString takeRight 2}-${taxYear takeRight 2}"
}