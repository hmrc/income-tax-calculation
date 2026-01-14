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
import connectors.httpParsers.CalculationDetailsHttpParser.CalculationDetailResponse
import connectors.httpParsers.GetCalculationListHttpParserLegacy.GetCalculationListResponseLegacy
import connectors.httpParsers.hip.HipGetCalculationDetailsHttpParser.HipGetCalculationDetailsResponse
import connectors.{CalculationDetailsConnectorLegacy, GetCalculationListConnector}
import enums.*
import models.calculation.CalcType.postFinalisationAllowedTypes
import models.{ErrorBodyModel, ErrorModel, GetCalculationListModel}
import play.api.Logging
import play.api.http.Status.*
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier
import utils.TaxYear

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

//noinspection ScalaStyle
class GetCalculationDetailsService @Inject()(calculationDetailsConnectorLegacy: CalculationDetailsConnectorLegacy,
                                             listCalculationDetailsConnector: GetCalculationListConnector,
                                             calcListHipLegacyConnector: HipCalculationLegacyListConnector,
                                             hipGetCalculationsDataConnector: HipGetCalculationsDataConnector,
                                             hipGetCalculationListConnector: HipGetCalculationListConnector,
                                             val appConfig: AppConfig)(implicit ec: ExecutionContext) extends Logging {

  private val taxYear2024: Int = TaxYear.taxYear2024

  private type CalculationDetailAsJsonResponse = Either[ErrorModel, JsValue]

  private def getLegacyCalcListResult(nino: String, taxYear: Option[String])
                                     (implicit hc: HeaderCarrier): Future[GetCalculationListResponseLegacy] = {
    logger.info(s"[GetCalculationDetailsService][calcListHipLegacyConnector]")
    calcListHipLegacyConnector.calcList(nino, taxYear)
  }

  def determineSubmissionChannel(calculationTrigger: Option[CalculationTrigger]): Option[SubmissionChannel] = {
    calculationTrigger.map {
      case CesaSAReturn2083 | CesaSAReturn2150 => IsLegacyWithCesa
      case _ => IsMTD
    }
  }
  
  def getCalculationDetailsByCalcId(
                                     nino: String,
                                     calcIdOpt: Option[String],
                                     taxYear: Option[String],
                                     submissionChannel: Option[SubmissionChannel]
                                   )(implicit hc: HeaderCarrier): Future[CalculationDetailAsJsonResponse] = {

    def handleResponsePartialF(log: String): PartialFunction[CalculationDetailResponse, Either[ErrorModel, JsValue]] = {
      case Right(value) =>
        val response = value.copy(submissionChannel = submissionChannel)
        logger.info(s"[getCalculationDetailsByCalcId][handleResponsePartialF] $log \n" + Json.toJson(response).toString + "\n")
        Right(Json.toJson(response))
      case Left(error) =>
        Left(error)
    }

    def handleResponsePartialFHip(log: String): PartialFunction[HipGetCalculationDetailsResponse, Either[ErrorModel, JsValue]] = {
      case Right(value) =>
        val response = value.copy(submissionChannel = submissionChannel)
        logger.info(s"[getCalculationDetailsByCalcId][handleResponsePartialFHip] $log \n" + Json.toJson(response).toString + "\n")
        Right(Json.toJson(response))
      case Left(error) =>
        Left(error)
    }

    (TaxYear.convert(taxYear), calcIdOpt) match {
      case (_, Some(calcId)) if taxYear.isEmpty =>
        calculationDetailsConnectorLegacy
          .getCalculationDetails(nino, calcId)
          .collect {
            handleResponsePartialF("taxYear.isEmpty")
          }
      case (Right(year), Some(calcId)) if year >= taxYear2024 =>
        hipGetCalculationsDataConnector
          .getCalculationsData(TaxYear.updatedFormat(taxYear.head), nino, calcId)
          .collect {
            handleResponsePartialFHip("year >= taxYear2024")
          }
      case (Right(_), Some(calcId)) if appConfig.useGetCalcDetailsHipPlatform5294 =>
        hipGetCalculationsDataConnector
          .getCalculationsData(TaxYear.updatedFormat(taxYear.head), nino, calcId)
          .collect {
            handleResponsePartialFHip("useGetCalcDetailsHipPlatform5294")
          }
      case (Right(taxYear), Some(calcId)) =>
        calculationDetailsConnectorLegacy.getCalculationDetails(nino, calcId)
          .collect {
            handleResponsePartialF(taxYear.toString)
          }
      case (Left(error), _) =>
        Future(Left(ErrorModel(500, ErrorBodyModel("500", error))))
      case _ =>
        Future(Left(ErrorModel(500, ErrorBodyModel("500", "[GetCalculationDetailsService][getCalculationDetailsByCalcId] - Unknown error"))))
    }
  }


  private[services] def filterCalcList(
                                        nino: String,
                                        taxYear: Option[String],
                                        calcSummaryList: Seq[GetCalculationListModel],
                                        calculationRecord: Option[String]
                                      )(implicit hc: HeaderCarrier): Future[CalculationDetailAsJsonResponse] = {

    val processedList: Seq[GetCalculationListModel] =
      if (calcSummaryList.forall(_.calculationOutcome.isEmpty)) {
        calcSummaryList
      } else {
        calcSummaryList.filter(_.calculationOutcome.exists(_.equalsIgnoreCase("PROCESSED")))
      }

    lazy val notFoundError = ErrorModel(NOT_FOUND, ErrorBodyModel.notFoundError())
    lazy val badRequestError = ErrorModel(BAD_REQUEST, ErrorBodyModel("INVALID_CALCULATION_RECORD", "The provided calculation record is invalid"))
    lazy val latestSortedList: Seq[GetCalculationListModel] = processedList.sortBy(_.calculationTimestamp)(Ordering[String].reverse)
    lazy val previousFilteredList = processedList.filter(calc => postFinalisationAllowedTypes.contains(calc.calculationType))
    lazy val previousSortedList = previousFilteredList.sortBy(_.calculationTimestamp)(Ordering[String].reverse)

    if (processedList.isEmpty) {
      logger.info(s"[CalculationDetailController][filterCalcList] - NOT_FOUND: No calculations found after filtering by outcome")
      getCalculationDetailsByCalcId(
        nino = nino,
        calcIdOpt = latestSortedList.headOption.map(_.calculationId),
        taxYear = taxYear,
        submissionChannel = determineSubmissionChannel(latestSortedList.headOption.flatMap(_.calculationTrigger))
      )
    } else {
      calculationRecord match {
        case Some("LATEST") =>
          logger.info(s"[CalculationDetailController][filterCalcList] - Success retrieving Latest tax calc, calculationTrigger: ${latestSortedList.head.calculationTrigger}")
          getCalculationDetailsByCalcId(nino, latestSortedList.headOption.map(_.calculationId), taxYear, determineSubmissionChannel(latestSortedList.head.calculationTrigger))
        case Some("PREVIOUS") =>
          previousSortedList.lift(1) match {
            case Some(value) =>
              logger.info(s"[CalculationDetailController][filterCalcList] - Success retrieving Previous tax calc, calculationTrigger: ${value.calculationTrigger}")
              getCalculationDetailsByCalcId(nino, Some(value.calculationId), taxYear, determineSubmissionChannel(value.calculationTrigger))
            case None =>
              Future(Left(notFoundError))
          }
        case Some(_) =>
          logger.error(s"[CalculationDetailController][filterCalcList] - INVALID_CALCULATION_RECORD: The provided calculation record is invalid")
          Future(Left(badRequestError))
        case None =>
          logger.info(s"[CalculationDetailController][filterCalcList] - No calculation record, Success retrieving tax calc for processed list, calculationTrigger: ${processedList.head.calculationTrigger}")
          getCalculationDetailsByCalcId(nino, processedList.headOption.map(_.calculationId), taxYear, determineSubmissionChannel(processedList.head.calculationTrigger))
      }
    }
  }

  private def handleLegacy(nino: String, taxYear: Option[String])
                          (implicit hc: HeaderCarrier): Future[CalculationDetailAsJsonResponse] = {
    getLegacyCalcListResult(nino, taxYear).flatMap {
      case Right(listOfCalculationDetails) if listOfCalculationDetails.isEmpty =>
        logger.info("[GetCalculationDetailsService][handleLegacy] listOfCalculationDetails.isEmpty")
        getCalculationDetailsByCalcId(nino = nino, calcIdOpt = listOfCalculationDetails.headOption.map(_.calculationId), taxYear = taxYear, submissionChannel = None)
      case Right(listOfCalculationDetails) =>
        logger.info("[GetCalculationDetailsService][handleLegacy] listOfCalculationDetails not empty")
        getCalculationDetailsByCalcId(nino = nino, calcIdOpt = listOfCalculationDetails.headOption.map(_.calculationId), taxYear = taxYear, submissionChannel = None)
      case Left(desError) =>
        logger.error(s"[GetCalculationDetailsService][handleLegacy] throwing error: $desError")
        Future(Left(desError))
    }
  }

  def getCalculationDetails(
                             nino: String,
                             taxYearOption: Option[String],
                             calculationRecord: Option[String]
                           )(implicit hc: HeaderCarrier): Future[CalculationDetailAsJsonResponse] = {
    
    taxYearOption match {
      case Some(taxYear) if taxYear.toInt >= taxYear2024 =>

        val retrievedCalculationList: Future[Either[ErrorModel, Seq[GetCalculationListModel]]] =
          taxYear.toInt match {
            case year if year >= TaxYear.taxYear2026 =>
              logger.info(s"[CalculationDetailController][getCalculationDetails] - Tax year: $taxYear, calling getCalculationList2083")
              listCalculationDetailsConnector.getCalculationList2083(nino, taxYear)
            case _ if calculationRecord.isDefined =>
              logger.info(s"[CalculationDetailController][getCalculationDetails] - Tax year: $taxYear, calling getCalculationList2150")
              listCalculationDetailsConnector.getCalculationList2150(nino, taxYear)
            case _ if appConfig.useGetCalcListHip5624 =>
              logger.info(s"[CalculationDetailController][getCalculationDetails] - Tax year: $taxYear, calling getCalculationList5624")
              hipGetCalculationListConnector.getCalculationList5624(nino, taxYear)
            case _ =>
              logger.info(s"[CalculationDetailController][getCalculationDetails] - Tax year: $taxYear, calling getCalculationList2150, possibly No calculationRecord defined")
              listCalculationDetailsConnector.getCalculationList2150(nino, taxYear)
          }

        retrievedCalculationList.flatMap {
          case Right(calcList) if calcList.isEmpty =>
            logger.info(s"[CalculationDetailController][getCalculationDetails] - Not legacy returning EMPTY calc list")
            filterCalcList(nino, taxYearOption, calcList, calculationRecord)
          case Right(calcList) =>
            logger.info(s"[CalculationDetailController][getCalculationDetails] - Not legacy returning filtered calculation list")
            filterCalcList(nino, taxYearOption, calcList, calculationRecord)
          case Left(desError) =>
            Future(Left(desError))
        }
      case _ =>
        logger.debug(s"[CalculationDetailController][getCalculationDetails] - No Tax Year handling legacy")
        handleLegacy(nino, taxYearOption)
    }
  }
}
