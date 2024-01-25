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

package repositories

import com.mongodb.client.model.ReturnDocument
import com.mongodb.client.model.Updates.set
import config.AppConfig
import models.mongo.LocalDateExtensions.dateTimeWrites
import models.mongo._
import org.mongodb.scala.model.{FindOneAndReplaceOptions, FindOneAndUpdateOptions}
import play.api.Logging
import play.api.libs.json.Writes
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.Codecs.toBson
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.play.http.logging.Mdc
import utils.PagerDutyHelper.PagerDutyKeys.{FAILED_TO_CREATE_UPDATE_TAX_YEARS_DATA, FAILED_TO_FIND_TAX_YEARS_DATA}
import utils.PagerDutyHelper.pagerDutyLog
import utils.SecureGCMCipher

import java.time.{LocalDate, LocalDateTime, ZoneOffset}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class TaxYearsDataRepositoryImpl @Inject()(mongo: MongoComponent, appConfig: AppConfig)
                                          (implicit secureGCMCipher: SecureGCMCipher, ec: ExecutionContext)
  extends PlayMongoRepository[EncryptedTaxYearsData](
  mongoComponent = mongo,
  collectionName = "taxYearsData",
  domainFormat = EncryptedTaxYearsData.formats,
  indexes = TaxYearsDataIndexes.indexes(appConfig)
) with Repository with TaxYearsDataRepository with Logging {

  def logOutIndexes(implicit ec: ExecutionContext): Future[Unit] = {
    val StartOfLog: String = "INDEX_IN_TAX_YEARS_DATA"
    Mdc.preservingMdc(collection.listIndexes().toFuture())
      .map { listOfIndexes =>
        listOfIndexes.foreach { eachIndex =>
          logger.info(s"$StartOfLog $eachIndex")
        }
      }
  }



  def find(nino: String): Future[Either[DatabaseError, Option[TaxYearsData]]] = {
    lazy val start = "[TaxYearsDataRepositoryImpl][find]"

    val queryFilter = filter(nino)
    val now = LocalDateTime.now(ZoneOffset.UTC).toLocalDate
    val update = set("lastUpdated", toBson(now) (dateTimeWrites))
    val options = FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)

    val findResult = collection.findOneAndUpdate(queryFilter, update, options).toFutureOption().map(Right(_)).recover {
      case exception: Exception =>
        pagerDutyLog(FAILED_TO_FIND_TAX_YEARS_DATA, Some(s"$start Failed to find tax years data. Exception: ${exception.getMessage}"))
        Left(MongoError(exception.getMessage))
    }

    findResult.map {
      case Left(error) => Left(error)
      case Right(encryptedData) =>
        Try {
          encryptedData.map { encryptedTaxYearsData: EncryptedTaxYearsData =>
            implicit val textAndKey: TextAndKey = TextAndKey(encryptedTaxYearsData.nino, appConfig.encryptionKey)
            encryptedTaxYearsData.decrypted()
          }
        }.toEither match {
          case Left(exception: Exception) => handleEncryptionDecryptionException(exception, start)
          case Right(decryptedData) => Right(decryptedData)
        }
    }
  }

  def createOrUpdate(taxYearsData: TaxYearsData): Future[Either[DatabaseError, Unit]] = {
    lazy val start = "[TaxYearsDataRepositoryImpl][createOrUpdate]"

    Try {
      implicit val textAndKey: TextAndKey = TextAndKey(taxYearsData.nino, appConfig.encryptionKey)
      taxYearsData.encrypted()
    }.toEither match {
      case Left(exception: Exception) => Future.successful(handleEncryptionDecryptionException(exception, start))
      case Right(encryptedData) =>

        val queryFilter = filter(encryptedData.nino)
        val replacement = encryptedData
        val options = FindOneAndReplaceOptions().upsert(true).returnDocument(ReturnDocument.AFTER)

        collection.findOneAndReplace(queryFilter, replacement, options).toFutureOption().map {
          case Some(_) => Right(())
          case None =>
            pagerDutyLog(FAILED_TO_CREATE_UPDATE_TAX_YEARS_DATA, Some(s"$start Failed to update tax years data."))
            Left(DataNotUpdatedError)
        }.recover {
          case exception: Exception =>
            pagerDutyLog(FAILED_TO_CREATE_UPDATE_TAX_YEARS_DATA, Some(s"$start Failed to update tax years data. Exception: ${exception.getMessage}"))
            Left(MongoError(exception.getMessage))
        }
    }
  }
}

trait TaxYearsDataRepository {
  def createOrUpdate(taxYearsData: TaxYearsData): Future[Either[DatabaseError, Unit]]

  def find(nino: String): Future[Either[DatabaseError, Option[TaxYearsData]]]
}
