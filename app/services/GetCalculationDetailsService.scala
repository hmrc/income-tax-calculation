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
import connectors.hip.{HipCalculationLegacyListConnector, HipGetCalculationListConnector, HipGetCalculationsDataConnector}
import connectors.httpParsers.GetCalculationListHttpParserLegacy.GetCalculationListResponseLegacy
import connectors.{CalculationDetailsConnectorLegacy, GetCalculationListConnector}
import models.calculation.CalcType.postFinalisationAllowedTypes
import models.{ErrorBodyModel, ErrorModel, GetCalculationListModel}
import play.api.Logging
import play.api.http.Status.{BAD_REQUEST, NO_CONTENT}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier
import utils.TaxYear

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetCalculationDetailsService @Inject()(calculationDetailsConnectorLegacy: CalculationDetailsConnectorLegacy,
                                             listCalculationDetailsConnector: GetCalculationListConnector,
                                             calcListHipLegacyConnector: HipCalculationLegacyListConnector,
                                             hipGetCalculationsDataConnector: HipGetCalculationsDataConnector,
                                             hipGetCalculationListConnector: HipGetCalculationListConnector,
                                             val appConfig: AppConfig)(implicit ec: ExecutionContext) extends Logging {

  private val specificTaxYear: Int = TaxYear.taxYear2024
  private type CalculationDetailAsJsonResponse = Either[ErrorModel, JsValue]

  def getCalculationDetails(nino: String, taxYearOption: Option[String], calculationRecord: Option[String])(implicit hc: HeaderCarrier): Future[CalculationDetailAsJsonResponse] = {
    taxYearOption match {
      case Some(taxYear) if taxYear.toInt >= specificTaxYear =>
        val list = taxYear.toInt match {
          case year if year >= TaxYear.taxYear2026 =>
            listCalculationDetailsConnector.getCalculationList2083(nino, taxYear)
          case _ if calculationRecord.isDefined =>
            listCalculationDetailsConnector.getCalculationList2150(nino, taxYear)
          case _ if appConfig.useGetCalcListHip5624 => //Used to switch between using HIP 5294 and IF 2150 - MISUV-10190
            hipGetCalculationListConnector.getCalculationList5624(nino, taxYear)
          case _ =>
            listCalculationDetailsConnector.getCalculationList2150(nino, taxYear)
        }

        list.flatMap {
          case Right(listOfCalculationDetails) if listOfCalculationDetails.isEmpty =>
            Future.successful(Left(ErrorModel(NO_CONTENT, ErrorBodyModel.parsingError())))
          case Right(listOfCalculationDetails) => filterCalcList(nino, taxYearOption, listOfCalculationDetails, calculationRecord)
          case Left(desError) => Future.successful(Left(desError))
        }
      case _ => handleLegacy(nino, taxYearOption)
    }
  }

  private def getLegacyCalcListResult(nino: String, taxYear: Option[String])
                                     (implicit hc: HeaderCarrier): Future[GetCalculationListResponseLegacy] = {
      logger.info(s"[GetCalculationDetailsService][calcListHipLegacyConnector]")
      calcListHipLegacyConnector.calcList(nino, taxYear)
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

  protected[services] def filterCalcList(nino: String, taxYear: Option[String], list: Seq[GetCalculationListModel], calculationRecord: Option[String])(implicit hc: HeaderCarrier): Future[CalculationDetailAsJsonResponse] = {

    val processedList = if (list.forall(_.calculationOutcome.isEmpty)) {
      list
    } else {
      list.filter(_.calculationOutcome.exists(_.equalsIgnoreCase("PROCESSED")))
    }

    if (processedList.isEmpty) {
      logger.error(s"[CalculationDetailController][filterCalcList] - NO_CONTENT: No calculations found after filtering by outcome")
      Future.successful(Left(ErrorModel(NO_CONTENT, ErrorBodyModel.notFoundError())))
    } else {
      calculationRecord match {
        case Some("LATEST") =>
          val sortedList = processedList.sortBy(_.calculationTimestamp)(Ordering[String].reverse)
          getCalculationDetailsByCalcId(nino, sortedList.head.calculationId, taxYear)
        case Some("PREVIOUS") =>
          val filteredList = processedList.filter(calc => postFinalisationAllowedTypes.contains(calc.calculationType))
          val sortedList = filteredList.sortBy(_.calculationTimestamp)(Ordering[String].reverse)
          sortedList.lift(1) match {
            case Some(value) => getCalculationDetailsByCalcId(nino, value.calculationId, taxYear)
            case None => Future.successful(Left(ErrorModel(NO_CONTENT, ErrorBodyModel.notFoundError())))
          }
        case Some(_) =>
          logger.error(s"[CalculationDetailController][filterCalcList] - INVALID_CALCULATION_RECORD: The provided calculation record is invalid")
          Future.successful(Left(ErrorModel(BAD_REQUEST, ErrorBodyModel("INVALID_CALCULATION_RECORD", "The provided calculation record is invalid"))))
        case None => getCalculationDetailsByCalcId(nino, processedList.head.calculationId, taxYear)
      }
    }
  }

  def getCalculationDetailsByCalcId(nino: String, calcId: String, taxYear: Option[String])(implicit hc: HeaderCarrier): Future[CalculationDetailAsJsonResponse] = {
    TaxYear.convert(taxYear) match {
      case _ if taxYear.isEmpty => calculationDetailsConnectorLegacy.getCalculationDetails(nino, calcId).collect {
        case Right(value) => Right(Json.toJson(value))
        case Left(error) => Left(error)
      }
      case Right(year) if year >= specificTaxYear =>
          hipGetCalculationsDataConnector.getCalculationsData(TaxYear.updatedFormat(taxYear.head), nino, calcId).collect {
            case Right(value) => Right(Json.toJson(value))
            case Left(error) => Left(error)
          }
      case Right(_) =>
        if (appConfig.useGetCalcDetailsHipPlatform5294) {
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
