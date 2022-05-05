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

package testUtils

import models.mongo.TextAndKey
import org.scalamock.scalatest.MockFactory
import utils.EncryptorInstances.intEncryptor
import utils.{EncryptedValue, SecureGCMCipher}

class EncryptorInstancesTest extends TestSuite
  with MockFactory {

  private val encryptedInt = mock[EncryptedValue]

  private implicit val secureGCMCipher: SecureGCMCipher = mock[SecureGCMCipher]
  private implicit val textAndKey: TextAndKey = TextAndKey("some-associated-text", "some-aes-key")

  "intEncryptor" should {
    "encrypt Int values" in {
      val intValue: Int = 500

      (secureGCMCipher.encrypt(_: Int)(_: TextAndKey)).expects(intValue, textAndKey).returning(encryptedInt)

      intEncryptor.encrypt(intValue) mustBe encryptedInt
    }
  }
}
