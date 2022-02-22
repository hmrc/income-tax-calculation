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

import connectors.httpParsers.CalculationDetailsHttpParser.CalculationDetailResponse
import connectors.{CalculationDetailsConnector, GetCalculationListConnector}
import models.{DesErrorBodyModel, DesErrorModel}
import play.api.http.Status.NO_CONTENT
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetCalculationDetailsService @Inject()(calculationDetailsConnector: CalculationDetailsConnector,
                                             listCalculationDetailsConnector: GetCalculationListConnector) (implicit ec: ExecutionContext) {

  def getCalculationDetails(nino: String, taxYear: Option[String])(implicit hc: HeaderCarrier):Future[CalculationDetailResponse] = {
    listCalculationDetailsConnector.calcList(nino, taxYear).flatMap {
      case Right(listOfCalculationDetails) if(listOfCalculationDetails.isEmpty) =>
        Future.successful(Left(DesErrorModel(NO_CONTENT, DesErrorBodyModel.parsingError)))
      case Right(listOfCalculationDetails) =>
        getCalculationDetailsByCalcId(nino, listOfCalculationDetails.head.calculationId)
      case Left(desError) => Future.successful(Left(desError))
    }
  }

  def getCalculationDetailsByCalcId(nino: String, calcId: String)(implicit hc: HeaderCarrier): Future[CalculationDetailResponse] = {
    calculationDetailsConnector.getCalculationDetails(nino, calcId)
  }
}
