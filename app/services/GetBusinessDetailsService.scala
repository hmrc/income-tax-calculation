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

package services

import connectors.GetBusinessDetailsConnector
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import models.ErrorModel
import models.incomeSourceDetails.{IncomeSourceDetailsError, IncomeSourceDetailsModel, TaxPayerDisplayResponse}

import scala.concurrent.{ExecutionContext, Future}

class GetBusinessDetailsService @Inject()(getBusinessDetailsConnector: GetBusinessDetailsConnector) (implicit ec: ExecutionContext) {
  def getBusinessDetails(nino: String, mtditid: String)(implicit hc: HeaderCarrier): Future[Either[ErrorModel, IncomeSourceDetailsModel]] = {
    getBusinessDetailsConnector.getBusinessDetails(nino).map {
      case Left(error) => Left(error)
      case Right(model: IncomeSourceDetailsModel) => Right(model)
      case Right(_: IncomeSourceDetailsError) => Right(IncomeSourceDetailsModel("", TaxPayerDisplayResponse(nino,mtditid,None,List.empty,None)))
    }
  }
}
