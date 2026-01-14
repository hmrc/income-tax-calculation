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
import connectors.httpParsers.GetCalculationListHttpParser.GetCalculationListResponse
import connectors.{CalculationDetailsConnectorLegacy, GetCalculationListConnector}
import models.calculation.CalcType.postFinalisationAllowedTypes
import models.calculation.CalculationResponseModel
import models.hip.CalculationHipResponseModel
import models.{ErrorBodyModel, ErrorModel, GetCalculationListModel}
import play.api.Logging
import play.api.http.Status.{BAD_REQUEST, NO_CONTENT}
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.HeaderCarrier
import utils.TaxYear

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

//noinspection ScalaStyle
class GetCalculationDetailsService @Inject()(
                                              calculationDetailsConnectorLegacy: CalculationDetailsConnectorLegacy,
                                              listCalculationDetailsConnector: GetCalculationListConnector,
                                              calcListHipLegacyConnector: HipCalculationLegacyListConnector,
                                              hipGetCalculationsDataConnector: HipGetCalculationsDataConnector,
                                              hipGetCalculationListConnector: HipGetCalculationListConnector,
                                              val appConfig: AppConfig
                                            )(implicit ec: ExecutionContext) extends Logging {

  private val specificTaxYear: Int = TaxYear.taxYear2024
  private type CalculationDetailAsJsonResponse = Either[ErrorModel, JsValue]

  private def getCalcListDetails[A](nino: String, taxYearOpt: Option[String])
                                   (result: (String, String, Option[String]) => Future[Either[ErrorModel, A]])
                                   (implicit hc: HeaderCarrier): Future[Either[ErrorModel, A]] = {

    logger.info(s"[GetCalculationDetailsService][calcListHipLegacyConnector]")
    calcListHipLegacyConnector
      .calcList(nino, taxYearOpt)
      .flatMap {
        case Right(listOfCalculationDetails) if listOfCalculationDetails.isEmpty =>
          Future(Left(ErrorModel(NO_CONTENT, ErrorBodyModel.parsingError())))
        case Right(listOfCalculationDetails) =>
          result(nino, listOfCalculationDetails.head.calculationId, taxYearOpt)
        case Left(desError) =>
          Future(Left(desError))
      }
  }

  def getCalculationDetailsByCalcIdLegacy(
                                           nino: String,
                                           calcId: String,
                                           taxYear: Option[String]
                                         )(implicit hc: HeaderCarrier): Future[Either[ErrorModel, CalculationResponseModel]] = {

    TaxYear.convert(taxYear) match {
      case _ if taxYear.isEmpty =>
        calculationDetailsConnectorLegacy.getCalculationDetails(nino, calcId)
      case Right(_) =>
        calculationDetailsConnectorLegacy.getCalculationDetails(nino, calcId)
      case Left(error) =>
        throw new RuntimeException(error)
    }
  }


  def getCalculationDetailsByCalcIdHip(
                                        nino: String,
                                        calcId: String,
                                        taxYear: Option[String]
                                      )(implicit hc: HeaderCarrier): Future[Either[ErrorModel, CalculationHipResponseModel]] = {

    TaxYear.convert(taxYear) match {
      case Right(year) if year >= specificTaxYear =>
        hipGetCalculationsDataConnector.getCalculationsData(TaxYear.updatedFormat(taxYear.head), nino, calcId)
      case Right(_) =>
        hipGetCalculationsDataConnector.getCalculationsData(TaxYear.updatedFormat(taxYear.head), nino, calcId)
      case Left(error) =>
        throw new RuntimeException(error)

    }
  }

  def filterCalcListLegacy(
                            nino: String,
                            taxYear: Option[String],
                            list: Seq[GetCalculationListModel],
                            calculationRecord: Option[String]
                          )(implicit hc: HeaderCarrier): Future[Either[ErrorModel, CalculationResponseModel]] = {

    val processedList =
      if (list.forall(_.calculationOutcome.isEmpty)) {
        list
      } else {
        list.filter(_.calculationOutcome.exists(_.equalsIgnoreCase("PROCESSED")))
      }

    lazy val sortedLatestList = processedList.sortBy(_.calculationTimestamp)(Ordering[String].reverse)

    lazy val filteredList = processedList.filter(calc => postFinalisationAllowedTypes.contains(calc.calculationType))
    lazy val sortedPreviousList = filteredList.sortBy(_.calculationTimestamp)(Ordering[String].reverse)

    if (processedList.isEmpty) {
      logger.error(s"[CalculationDetailController][filterCalcListLegacy] - NO_CONTENT: No calculations found after filtering by outcome")
      Future(Left(ErrorModel(NO_CONTENT, ErrorBodyModel.notFoundError())))
    } else {
      calculationRecord match {
        case Some("LATEST") =>
          getCalculationDetailsByCalcIdLegacy(nino, sortedLatestList.head.calculationId, taxYear)
        case Some("PREVIOUS") =>
          sortedPreviousList.lift(1) match {
            case Some(value) =>
              getCalculationDetailsByCalcIdLegacy(nino, value.calculationId, taxYear)
            case None =>
              Future(Left(ErrorModel(NO_CONTENT, ErrorBodyModel.notFoundError())))
          }
        case Some(_) =>
          logger.error(s"[CalculationDetailController][filterCalcListLegacy] - INVALID_CALCULATION_RECORD: The provided calculation record is invalid")
          Future(Left(ErrorModel(BAD_REQUEST, ErrorBodyModel("INVALID_CALCULATION_RECORD", "The provided calculation record is invalid"))))
        case None =>
          getCalculationDetailsByCalcIdLegacy(nino, processedList.head.calculationId, taxYear)
      }
    }
  }


  def filterCalcListHip(
                         nino: String,
                         taxYear: Option[String],
                         list: Seq[GetCalculationListModel],
                         calculationRecord: Option[String]
                       )(implicit hc: HeaderCarrier): Future[Either[ErrorModel, CalculationHipResponseModel]] = {

    val processedList =
      if (list.forall(_.calculationOutcome.isEmpty)) {
        list
      } else {
        list.filter(_.calculationOutcome.exists(_.equalsIgnoreCase("PROCESSED")))
      }

    if (processedList.isEmpty) {
      logger.error(s"[CalculationDetailController][filterCalcListHip] - NO_CONTENT: No calculations found after filtering by outcome")
      Future(Left(ErrorModel(NO_CONTENT, ErrorBodyModel.notFoundError())))
    } else {
      calculationRecord match {
        case Some("LATEST") =>
          val sortedList = processedList.sortBy(_.calculationTimestamp)(Ordering[String].reverse)
          getCalculationDetailsByCalcIdHip(nino, sortedList.head.calculationId, taxYear)
        case Some("PREVIOUS") =>
          val filteredList = processedList.filter(calc => postFinalisationAllowedTypes.contains(calc.calculationType))
          val sortedList = filteredList.sortBy(_.calculationTimestamp)(Ordering[String].reverse)
          sortedList.lift(1) match {
            case Some(value) =>
              getCalculationDetailsByCalcIdHip(nino, value.calculationId, taxYear)
            case None =>
              Future(Left(ErrorModel(NO_CONTENT, ErrorBodyModel.notFoundError())))
          }
        case Some(_) =>
          logger.error(s"[CalculationDetailController][filterCalcListHip] - INVALID_CALCULATION_RECORD: The provided calculation record is invalid")
          Future(Left(ErrorModel(BAD_REQUEST, ErrorBodyModel("INVALID_CALCULATION_RECORD", "The provided calculation record is invalid"))))
        case None =>
          getCalculationDetailsByCalcIdHip(nino, processedList.head.calculationId, taxYear)
      }
    }
  }


  private[services] def getLegacyCalculationSummaryList(
                                                         taxYearString: String,
                                                         nino: String,
                                                         calculationRecord: Option[String]
                                                       )(implicit hc: HeaderCarrier) = {
    taxYearString.toInt match {
      case taxYear if taxYear >= TaxYear.taxYear2026 =>
        listCalculationDetailsConnector.getCalculationList2083(nino, taxYear.toString)
      case taxYear if calculationRecord.isDefined =>
        listCalculationDetailsConnector.getCalculationList2150(nino, taxYear.toString)
      case taxYear if appConfig.useGetCalcListHip5624 => // Used to switch between using HIP 5294 and IF 2150 - MISUV-10190
        hipGetCalculationListConnector.getCalculationList5624(nino, taxYear.toString)
      case taxYear =>
        listCalculationDetailsConnector.getCalculationList2150(nino, taxYear.toString)
    }
  }


  def getCalculationDetailsLegacy(
                                   nino: String,
                                   taxYearOption: Option[String],
                                   calculationRecord: Option[String]
                                 )(implicit hc: HeaderCarrier): Future[Either[ErrorModel, CalculationResponseModel]] = {

    taxYearOption match {
      case Some(taxYear) if taxYear.toInt >= specificTaxYear =>

        //        val legacyCalculationSummaryList =
        //          taxYear.toInt match {
        //            case year if year >= TaxYear.taxYear2026 =>
        //              listCalculationDetailsConnector.getCalculationList2083(nino, taxYear)
        //            case _ if calculationRecord.isDefined =>
        //              listCalculationDetailsConnector.getCalculationList2150(nino, taxYear)
        //            case _ if appConfig.useGetCalcListHip5624 => // Used to switch between using HIP 5294 and IF 2150 - MISUV-10190
        //              hipGetCalculationListConnector.getCalculationList5624(nino, taxYear)
        //            case _ =>
        //              listCalculationDetailsConnector.getCalculationList2150(nino, taxYear)
        //          }

        val legacyCalculationSummaryList =
          getLegacyCalculationSummaryList(taxYear, nino, calculationRecord)

        legacyCalculationSummaryList.flatMap {
          case Right(listOfCalculationDetails) if listOfCalculationDetails.isEmpty =>
            Future(Left(ErrorModel(NO_CONTENT, ErrorBodyModel.parsingError())))
          case Right(listOfCalculationDetails) =>
            filterCalcListLegacy(nino, taxYearOption, listOfCalculationDetails, calculationRecord)
          case Left(desError) =>
            Future(Left(desError))
        }
      case _ =>
        getCalcListDetails[CalculationResponseModel](nino, taxYearOption)(getCalculationDetailsByCalcIdLegacy)
    }
  }

  def getCalculationDetailsHip(
                                nino: String,
                                taxYearOption: Option[String],
                                calculationRecord: Option[String]
                              )(implicit hc: HeaderCarrier): Future[Either[ErrorModel, CalculationHipResponseModel]] = {

    taxYearOption match {
      case Some(taxYear) if taxYear.toInt >= specificTaxYear && appConfig.useGetCalcListHip5624 =>

        val hipCalculationList: Future[GetCalculationListResponse] = hipGetCalculationListConnector.getCalculationList5624(nino, taxYear)

        hipCalculationList.flatMap {
          case Right(listOfCalculationDetails) if listOfCalculationDetails.isEmpty =>
            Future(Left(ErrorModel(NO_CONTENT, ErrorBodyModel.parsingError())))
          case Right(listOfCalculationDetails) =>
            filterCalcListHip(nino, taxYearOption, listOfCalculationDetails, calculationRecord)
          case Left(desError) =>
            Future(Left(desError))
        }
      case _ =>
        getCalcListDetails[CalculationHipResponseModel](nino, taxYearOption)(getCalculationDetailsByCalcIdHip)
    }
  }
}
