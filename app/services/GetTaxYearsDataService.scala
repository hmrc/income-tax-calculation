/*
 * Copyright 2022 HM Revenue & Customs
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

package services

import models.ErrorModel
import models.incomeSourceDetails.IncomeSourceDetailsModel
import models.mongo.{DatabaseError, TaxYearsData}
import org.joda.time.DateTimeZone
import uk.gov.hmrc.http.HeaderCarrier
import play.api.Logging
import repositories.TaxYearsDataRepository
import utils.Clock

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetTaxYearsDataService @Inject()(getBusinessDetailsService: GetBusinessDetailsService,
                                       taxYearsDataRepository: TaxYearsDataRepository,
                                       clock: Clock) (implicit ec: ExecutionContext) extends Logging {
  def getTaxYearsData(nino: String, mtditid: String)(implicit hc: HeaderCarrier): Future[Either[ErrorModel, TaxYearsData]] = {
    taxYearsDataRepository.find(nino).flatMap {
      case Right(Some(taxYearsData: TaxYearsData)) =>
        Future.successful(Right(taxYearsData))
      case _: Either[DatabaseError, Option[TaxYearsData]] =>
        getBusinessDetailsService.getBusinessDetails(nino, mtditid).flatMap {
          case Right(success: IncomeSourceDetailsModel) =>
            val taxYearsData = TaxYearsData(success.nino, success.taxYears, clock.now(DateTimeZone.UTC))
            taxYearsDataRepository.createOrUpdate(taxYearsData).map {
              case Left(_: DatabaseError) =>
                Right(taxYearsData)
              case Right(_) =>
                Right(taxYearsData)
            }
          case Left(error: ErrorModel) =>
            Future.successful(Left(error))
        }
    }
  }
}
