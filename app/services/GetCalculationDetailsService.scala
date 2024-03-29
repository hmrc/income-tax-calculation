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

package services

import connectors.httpParsers.CalculationDetailsHttpParser.CalculationDetailResponse
import connectors.{CalculationDetailsConnector, CalculationDetailsConnectorLegacy, GetCalculationListConnector, GetCalculationListConnectorLegacy}
import models.{ErrorBodyModel, ErrorModel}
import play.api.http.Status.NO_CONTENT
import uk.gov.hmrc.http.HeaderCarrier
import utils.TaxYear

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetCalculationDetailsService @Inject()(calculationDetailsConnectorLegacy: CalculationDetailsConnectorLegacy,
                                             calculationDetailsConnector: CalculationDetailsConnector,
                                             listCalculationDetailsConnector: GetCalculationListConnector,
                                             listCalculationDetailsConnectorLegacy: GetCalculationListConnectorLegacy)(implicit ec: ExecutionContext) {

  private val specificTaxYear: Int = TaxYear.specificTaxYear

  def getCalculationDetails(nino: String, taxYearOption: Option[String])(implicit hc: HeaderCarrier): Future[CalculationDetailResponse] = {
    taxYearOption match {
      case Some(taxYear) if taxYear.toInt >= specificTaxYear =>
        listCalculationDetailsConnector.getCalculationList(nino, taxYear).flatMap {
          case Right(listOfCalculationDetails) if listOfCalculationDetails.isEmpty =>
            Future.successful(Left(ErrorModel(NO_CONTENT, ErrorBodyModel.parsingError())))
          case Right(listOfCalculationDetails) =>
            getCalculationDetailsByCalcId(nino, listOfCalculationDetails.head.calculationId, taxYearOption)
          case Left(desError) => Future.successful(Left(desError))
        }
      case _ => handleLegacy(nino, taxYearOption)
    }
  }

  private def handleLegacy(nino: String, taxYear: Option[String])(implicit hc: HeaderCarrier): Future[CalculationDetailResponse] = {
    listCalculationDetailsConnectorLegacy.calcList(nino, taxYear).flatMap {
      case Right(listOfCalculationDetails) if listOfCalculationDetails.isEmpty =>
        Future.successful(Left(ErrorModel(NO_CONTENT, ErrorBodyModel.parsingError())))
      case Right(listOfCalculationDetails) =>
        getCalculationDetailsByCalcId(nino, listOfCalculationDetails.head.calculationId, taxYear)
      case Left(desError) => Future.successful(Left(desError))
    }
  }

  def getCalculationDetailsByCalcId(nino: String, calcId: String, taxYear: Option[String])(implicit hc: HeaderCarrier): Future[CalculationDetailResponse] = {

    TaxYear.convert(taxYear) match {
      case _ if taxYear.isEmpty => calculationDetailsConnectorLegacy.getCalculationDetails(nino, calcId)
      case Right(year) if year >= specificTaxYear =>
        calculationDetailsConnector.getCalculationDetails(TaxYear.updatedFormat(year.toString), nino, calcId)
      case Right(_) => calculationDetailsConnectorLegacy.getCalculationDetails(nino, calcId)
      case Left(error) => throw new RuntimeException(error)
    }
  }
}