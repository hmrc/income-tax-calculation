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

package utils

import models.mongo.TextAndKey

trait Encryptable[A] {
  def encrypt(value: A)(implicit secureGCMCipher: SecureGCMCipher, textAndKey: TextAndKey): EncryptedValue
}

object EncryptorInstances {

  implicit val intEncryptor: Encryptable[Int] = new Encryptable[Int] {
    def encrypt(value: Int)(implicit secureGCMCipher: SecureGCMCipher, textAndKey: TextAndKey): EncryptedValue = secureGCMCipher.encrypt(value)
  }
}

object EncryptableSyntax {
  implicit class EncryptableOps[A](value: A)(implicit e: Encryptable[A]) {
    def encrypted(implicit secureGCMCipher: SecureGCMCipher, textAndKey: TextAndKey): EncryptedValue = e.encrypt(value)
  }
}
