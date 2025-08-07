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

import config.AppConfig
import connectors.hip.{HipCalculationLegacyListConnector, HipGetCalculationsDataConnector}
import connectors.httpParsers.GetCalculationListHttpParserLegacy.GetCalculationListResponseLegacy
import connectors.{CalculationDetailsConnector, CalculationDetailsConnectorLegacy, GetCalculationListConnector, GetCalculationListConnectorLegacy}
import models.{ErrorBodyModel, ErrorModel}
import play.api.Logging
import play.api.http.Status.NO_CONTENT
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier
import utils.TaxYear

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetCalculationDetailsService @Inject()(calculationDetailsConnectorLegacy: CalculationDetailsConnectorLegacy,
                                             calculationDetailsConnector: CalculationDetailsConnector,
                                             listCalculationDetailsConnector: GetCalculationListConnector,
                                             listCalculationDetailsConnectorLegacy: GetCalculationListConnectorLegacy,
                                             calcListHipLegacyConnector: HipCalculationLegacyListConnector,
                                             hipGetCalculationsDataConnector: HipGetCalculationsDataConnector,
                                             val appConfig: AppConfig)(implicit ec: ExecutionContext) extends Logging {

  private val specificTaxYear: Int = TaxYear.taxYear2024
  private type CalculationDetailAsJsonResponse = Either[ErrorModel, JsValue]

  def getCalculationDetails(nino: String, taxYearOption: Option[String])(implicit hc: HeaderCarrier): Future[CalculationDetailAsJsonResponse] = {
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

  private def getLegacyCalcListResult(nino: String, taxYear: Option[String])
                                     (implicit hc: HeaderCarrier): Future[GetCalculationListResponseLegacy] = {
    if (appConfig.useGetCalcListHiPlatform) {
      logger.info(s"[GetCalculationDetailsService][calcListHipLegacyConnector]")
      calcListHipLegacyConnector.calcList(nino, taxYear)
    } else {
      // DES or IF connection will be used instead
      logger.info(s"[GetCalculationDetailsService][listCalculationDetailsConnectorLegacy]")
      listCalculationDetailsConnectorLegacy.calcList(nino, taxYear)
    }
  }

  private def handleLegacy(nino: String, taxYear: Option[String])
                          (implicit hc: HeaderCarrier): Future[CalculationDetailAsJsonResponse] = {
    getLegacyCalcListResult(nino, taxYear).flatMap {
      case Right(listOfCalculationDetails) if listOfCalculationDetails.isEmpty =>
        Future.successful(Left(ErrorModel(NO_CONTENT, ErrorBodyModel.parsingError())))
      case Right(listOfCalculationDetails) =>
        getCalculationDetailsByCalcId(nino, listOfCalculationDetails.head.calculationId, taxYear)
      case Left(desError) => Future.successful(Left(desError))
    }
  }

  def getCalculationDetailsByCalcId(nino: String, calcId: String, taxYear: Option[String])(implicit hc: HeaderCarrier): Future[CalculationDetailAsJsonResponse] = {
    TaxYear.convert(taxYear) match {
      case _ if taxYear.isEmpty => calculationDetailsConnectorLegacy.getCalculationDetails(nino, calcId).collect {
        case Right(value) => Right(Json.toJson(value))
        case Left(error) => Left(error)
      }
      case Right(year) if year >= specificTaxYear =>
        if (appConfig.useGetCalcDetailsHipPlatform) {
          hipGetCalculationsDataConnector.getCalculationsData(TaxYear.updatedFormat(taxYear.head), nino, calcId).collect {
            case Right(value) => Right(Json.toJson(value))
            case Left(error) => Left(error)
          }
        } else {
          calculationDetailsConnector.getCalculationDetails(TaxYear.updatedFormat(year.toString), nino, calcId).collect {
            case Right(value) => Right(Json.toJson(value))
            case Left(error) => Left(error)
          }
        }
      case Right(_) =>
        if (appConfig.useGetCalcDetailsHipPlatformR17) {
          hipGetCalculationsDataConnector.getCalculationsData(TaxYear.updatedFormat(taxYear.head), nino, calcId).collect {
            case Right(value) => Right(Json.toJson(value))
            case Left(error) => Left(error)
          }
        }
        else {
          calculationDetailsConnectorLegacy.getCalculationDetails(nino, calcId).collect {
            case Right(value) => Right(Json.toJson(value))
            case Left(error) => Left(error)
          }
        }
      case Left(error) => throw new RuntimeException(error)
    }
  }
}
