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

package repositories

import config.BackendAppConfig
import helpers.WiremockSpec
import models.mongo._
import org.joda.time.{DateTime, DateTimeZone}
import org.mongodb.scala.MongoWriteException
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model.{IndexModel, IndexOptions}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Configuration
import uk.gov.hmrc.mongo.MongoUtils
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import utils.SecureGCMCipher

import scala.concurrent.Future

class TaxYearsDataRepositoryISpec extends AnyWordSpec with WiremockSpec with Matchers {

  val taxYearsData: TaxYearsData = TaxYearsData(
    nino = "AA123456A",
    taxYears = Seq(2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023),
    lastUpdated = DateTime.now(DateTimeZone.UTC)
  )

  private val repoWithInvalidEncryption = appWithInvalidEncryptionKey.injector.instanceOf[TaxYearsDataRepositoryImpl]
  private implicit val secureGCMCipher: SecureGCMCipher = app.injector.instanceOf[SecureGCMCipher]

  private def count: Long = await(underTest.collection.countDocuments().toFuture())

  private def countFromOtherDatabase: Long = await(underTest.collection.countDocuments().toFuture())

  private val underTest: TaxYearsDataRepositoryImpl = app.injector.instanceOf[TaxYearsDataRepositoryImpl]

  val appConfig = new BackendAppConfig(app.injector.instanceOf[Configuration], app.injector.instanceOf[ServicesConfig])

  class EmptyDatabase {
    await(underTest.collection.drop().toFuture())
    await(underTest.ensureIndexes)
  }

  "update with invalid encryption" should {
    "fail to add data" in new EmptyDatabase {
      countFromOtherDatabase mustBe 0
      val res: Either[DatabaseError, Unit] = await(repoWithInvalidEncryption.createOrUpdate(taxYearsData))
      res mustBe Left(EncryptionDecryptionError(
        "Key being used is not valid. It could be due to invalid encoding, wrong length or uninitialized for encrypt Invalid AES key length: 2 bytes"))
    }
  }

  "find with invalid encryption" should {
    "fail to find data" in new EmptyDatabase {
      implicit val textAndKey: TextAndKey = TextAndKey(taxYearsData.nino, appConfig.encryptionKey)
      countFromOtherDatabase mustBe 0
      await(repoWithInvalidEncryption.collection.insertOne(taxYearsData.encrypted()).toFuture())
      countFromOtherDatabase mustBe 1
      private val res = await(repoWithInvalidEncryption.find(taxYearsData.nino))
      res mustBe Left(EncryptionDecryptionError(
        "Key being used is not valid. It could be due to invalid encoding, wrong length or uninitialized for decrypt Invalid AES key length: 2 bytes"))
    }
  }

  "handleEncryptionDecryptionException" should {
    "handle an exception" in {
      val res = repoWithInvalidEncryption.handleEncryptionDecryptionException(new Exception("fail"), "")
      res mustBe Left(EncryptionDecryptionError("fail"))
    }
  }

  "createOrUpdate" should {
    "fail to add a document to the collection when a mongo error occurs" in new EmptyDatabase {
      def ensureIndexes: Future[Seq[String]] = {
        val indexes = Seq(IndexModel(ascending("taxYear"), IndexOptions().unique(true).name("fakeIndex")))
        MongoUtils.ensureIndexes(underTest.collection, indexes, replaceIndexes = true)
      }

      await(ensureIndexes)
      count mustBe 0

      private val res = await(underTest.createOrUpdate(taxYearsData))
      res mustBe Right(())
      count mustBe 1

      private val res2 = await(underTest.createOrUpdate(taxYearsData.copy(nino = "AA234567A")))
      res2.left.toOption.get.message must include("Command failed with error 11000 (DuplicateKey)")
      count mustBe 1
    }

    "create a document in collection when one does not exist" in new EmptyDatabase {
      await(underTest.createOrUpdate(taxYearsData)) mustBe Right(())
      count mustBe 1
    }

    "create a document in collection with all fields present" in new EmptyDatabase {
      await(underTest.createOrUpdate(taxYearsData)) mustBe Right(())
      count mustBe 1
    }

    "update a document in collection when one already exists" in new EmptyDatabase {
      await(underTest.createOrUpdate(taxYearsData)) mustBe Right(())
      count mustBe 1

      private val updatedTaxYearsData = taxYearsData.copy(taxYears = Seq(2016, 2017, 2018))

      await(underTest.createOrUpdate(updatedTaxYearsData)) mustBe Right(())
      count mustBe 1
    }

    "create a new document when the same documents exists but the nino is different" in new EmptyDatabase {
      await(underTest.createOrUpdate(taxYearsData)) mustBe Right(())
      count mustBe 1

      private val newTaxYearsData = taxYearsData.copy(nino = "123")

      await(underTest.createOrUpdate(newTaxYearsData)) mustBe Right(())
      count mustBe 2
    }
  }

  "find" should {
    "get a document and update the TTL" in new EmptyDatabase {
      private val now = DateTime.now(DateTimeZone.UTC)
      private val data = taxYearsData.copy(lastUpdated = now)

      await(underTest.createOrUpdate(data)) mustBe Right(())
      count mustBe 1

      private val findResult = await(underTest.find(data.nino))

      findResult.toOption.get.map(_.copy(lastUpdated = data.lastUpdated)) mustBe Some(data)
      findResult.toOption.get.map(_.lastUpdated.isAfter(data.lastUpdated)) mustBe Some(true)
    }

    "find a document in collection with all fields present" in new EmptyDatabase {
      await(underTest.createOrUpdate(taxYearsData)) mustBe Right(())
      count mustBe 1

      val findResult: Either[DatabaseError, Option[TaxYearsData]] = {
        await(underTest.find(taxYearsData.nino))
      }

      findResult mustBe Right(Some(taxYearsData.copy(lastUpdated = findResult.toOption.get.get.lastUpdated)))
    }

    "return None when find operation succeeds but no data is found for the given inputs" in new EmptyDatabase {
      await(underTest.find("345")) mustBe Right(None)
    }
  }

  "the set indexes" should {
    "enforce uniqueness" in new EmptyDatabase {
      implicit val textAndKey: TextAndKey = TextAndKey(taxYearsData.nino, appConfig.encryptionKey)
      await(underTest.createOrUpdate(taxYearsData)) mustBe Right(())
      count mustBe 1

      private val encryptedTaxYearsData: EncryptedTaxYearsData = taxYearsData.encrypted()

      private val caught = intercept[MongoWriteException](await(underTest.collection.insertOne(encryptedTaxYearsData).toFuture()))

      caught.getMessage must
        include("E11000 duplicate key error collection: income-tax-calculation.taxYearsData index: TaxYearDataLookupIndex dup key:")
    }
  }
}
