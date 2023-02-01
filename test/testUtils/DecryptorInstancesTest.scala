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

package testUtils

import models.mongo.TextAndKey
import org.scalamock.scalatest.MockFactory
import utils.DecryptorInstances.intDecryptor
import utils.TypeCaster.Converter
import utils.TypeCaster.Converter.intLoader
import utils.{EncryptedValue, SecureGCMCipher}

class DecryptorInstancesTest extends TestSuite
  with MockFactory {

  private val encryptedValue = EncryptedValue("some-value", "some-nonce")

  private implicit val secureGCMCipher: SecureGCMCipher = mock[SecureGCMCipher]
  private implicit val textAndKey: TextAndKey = TextAndKey("some-associated-text", "some-aes-key")

  "intDecryptor" should {
    "decrypt to Int values" in {
      val intValue: Int = 50

      (secureGCMCipher.decrypt[Int](_: String, _: String)(_: TextAndKey, _: Converter[Int]))
        .expects(encryptedValue.value, encryptedValue.nonce, textAndKey, intLoader).returning(intValue)

      intDecryptor.decrypt(encryptedValue) mustBe intValue
    }
  }
}
