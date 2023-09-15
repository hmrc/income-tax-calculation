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

package models.incomeSourceDetails

import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, Reads, _}

import java.time.LocalDate

sealed trait IncomeSourceDetailsResponseModel

case class IncomeSourceDetailsModel(processingDate: String,
                                   taxPayerDisplayResponse: TaxPayerDisplayResponse) extends IncomeSourceDetailsResponseModel {

}

object IncomeSourceDetailsModel {
  implicit val formats: OFormat[IncomeSourceDetailsModel] = Json.format[IncomeSourceDetailsModel]

  implicit val desReads: Reads[IncomeSourceDetailsModel] = (
    (JsPath \ "processingDate").read[String] and
      (JsPath \ "taxPayerDisplayResponse").read(TaxPayerDisplayResponse.desReads)
    ) (IncomeSourceDetailsModel.apply _)
}
case class TaxPayerDisplayResponse(nino: String,
                                   mtdbsa: String,
                                   yearOfMigration: Option[String],
                                   businesses: List[BusinessDetailsModel],
                                   property: Option[PropertyDetailsModel]){

  def taxYears: Seq[Int] = Option(orderedTaxYearsByAccountingPeriods).filter(_.nonEmpty).getOrElse(Seq(getCurrentTaxEndYear))

  def startingTaxYear: Int = (businesses.flatMap(_.firstAccountingPeriodEndDate) ++ property.flatMap(_.firstAccountingPeriodEndDate))
    .map(_.getYear).sortWith(_ < _).headOption.getOrElse(getCurrentTaxEndYear)

  def orderedTaxYearsByAccountingPeriods: List[Int] = {
    (startingTaxYear to getCurrentTaxEndYear).toList.distinct
  }

  def getCurrentTaxEndYear: Int = {
    val currentDate = LocalDate.now
    if (currentDate.isBefore(LocalDate.of(currentDate.getYear, 4, 6))) {
      currentDate.getYear
    } else {
      currentDate.getYear + 1
    }
  }
}

case class IncomeSourceDetailsError(status: Int, reason: String) extends IncomeSourceDetailsResponseModel

object TaxPayerDisplayResponse {

  def applyWithFields(nino: String,
                      mtdbsa: String,
                      yearOfMigration: Option[String],
                      businessData: Option[List[BusinessDetailsModel]],
                      propertyData: Option[PropertyDetailsModel]): TaxPayerDisplayResponse = {
    val businessDetails = businessData match {
      case Some(data) => data
      case None => List()
    }
    TaxPayerDisplayResponse(
      nino,
      mtdbsa,
      yearOfMigration,
      businessDetails,
      propertyData
    )
  }

  val desReads: Reads[TaxPayerDisplayResponse] = (
    (__ \ "nino").read[String] and
      (__ \ "mtdId").read[String] and
      (__ \ "yearOfMigration").readNullable[String] and
      (__ \ "businessData").readNullable(Reads.list(BusinessDetailsModel.desReads)) and
      (__ \ "propertyData").readNullable[List[PropertyDetailsModel]].map(_.map(_.head))
    ) (TaxPayerDisplayResponse.applyWithFields _)

  implicit val format: Format[TaxPayerDisplayResponse] = Json.format[TaxPayerDisplayResponse]

}

object IncomeSourceDetailsError {
  implicit val format: Format[IncomeSourceDetailsError] = Json.format[IncomeSourceDetailsError]
}
