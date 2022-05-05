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

package utils

import java.security.{InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, SecureRandom}
import java.util.Base64

import config.AppConfig
import javax.crypto._
import javax.crypto.spec.{GCMParameterSpec, SecretKeySpec}
import javax.inject.{Inject, Singleton}
import models.mongo.TextAndKey
import play.api.Logging
import play.api.libs.json.{Json, OFormat}
import utils.TypeCaster.Converter

import scala.util.{Failure, Success, Try}

case class EncryptedValue(value: String, nonce: String)

object EncryptedValue {
  implicit val cryptoFormat: OFormat[EncryptedValue] = Json.format[EncryptedValue]
}

class EncryptionDecryptionException(method: String, reason: String, message: String) extends RuntimeException {
  val failureReason = s"$reason for $method"
  val failureMessage: String = message
}

@Singleton
class SecureGCMCipher @Inject()(implicit private val appConfig: AppConfig) extends Logging {

  val IV_SIZE = 96
  val TAG_BIT_LENGTH = 128
  val ALGORITHM_TO_TRANSFORM_STRING = "AES/GCM/PKCS5Padding"
  lazy val secureRandom = new SecureRandom()
  val ALGORITHM_KEY = "AES"
  val METHOD_ENCRYPT = "encrypt"
  val METHOD_DECRYPT = "decrypt"

  private[utils] def getCipherInstance: Cipher = Cipher.getInstance(ALGORITHM_TO_TRANSFORM_STRING)

  def encrypt[T](valueToEncrypt: T)(implicit textAndKey: TextAndKey): EncryptedValue = {
    if(appConfig.useEncryption){
      val initialisationVector = generateInitialisationVector
      val nonce = new String(Base64.getEncoder.encode(initialisationVector))
      val gcmParameterSpec = new GCMParameterSpec(TAG_BIT_LENGTH, initialisationVector)
      val secretKey = validateSecretKey(textAndKey.aesKey, METHOD_ENCRYPT)
      val cipherText = generateCipherText(valueToEncrypt.toString,
        validateAssociatedText(textAndKey.associatedText, METHOD_ENCRYPT), gcmParameterSpec, secretKey)
      EncryptedValue(cipherText, nonce)
    } else {
      logger.info("[SecureGCMCipher][encrypt] Encryption is turned off")
      EncryptedValue(valueToEncrypt.toString, s"${valueToEncrypt.toString}-Nonce")
    }
  }

  def decrypt[T](valueToDecrypt: String, nonce: String)(implicit textAndKey: TextAndKey, converter: Converter[T]): T = {
    if(appConfig.useEncryption) {
      val initialisationVector = Base64.getDecoder.decode(nonce)
      val gcmParameterSpec = new GCMParameterSpec(TAG_BIT_LENGTH, initialisationVector)
      val secretKey = validateSecretKey(textAndKey.aesKey, METHOD_DECRYPT)

      Try {
        decryptCipherText(valueToDecrypt, validateAssociatedText(textAndKey.associatedText, METHOD_DECRYPT), gcmParameterSpec, secretKey)
      }.toEither match {
        case Left(exception) => throw exception
        case Right(value) => converter.convert(value)
      }
    } else {
      logger.info("[SecureGCMCipher][decrypt] Encryption is turned off")
      converter.convert(valueToDecrypt)
    }
  }

  private def generateInitialisationVector: Array[Byte] = {
    val iv = new Array[Byte](IV_SIZE)
    secureRandom.nextBytes(iv)
    iv
  }

  private def validateSecretKey(key: String, method: String): SecretKey = Try {
    val decodedKey = Base64.getDecoder.decode(key)
    new SecretKeySpec(decodedKey, 0 , decodedKey.length, ALGORITHM_KEY)
  } match {
    case Success(secretKey) => secretKey
    case Failure(ex) => throw new EncryptionDecryptionException(method, "The key provided is invalid", ex.getMessage)
  }

  def generateCipherText(valueToEncrypt: String, associatedText: Array[Byte], gcmParameterSpec: GCMParameterSpec, secretKey: SecretKey): String = {
    Try {
      val cipher = getCipherInstance
      cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec, new SecureRandom())
      cipher.updateAAD(associatedText)
      cipher.doFinal(valueToEncrypt.getBytes)
    } match {
      case Success(cipherTextBytes) => new String(Base64.getEncoder.encode(cipherTextBytes))
      case Failure(ex) => throw processCipherTextFailure(ex, METHOD_ENCRYPT)
    }
  }

  def decryptCipherText(valueToDecrypt: String, associatedText: Array[Byte], gcmParameterSpec: GCMParameterSpec, secretKey: SecretKey): String = {
    Try {
      val cipher = getCipherInstance
      cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec, new SecureRandom())
      cipher.updateAAD(associatedText)
      cipher.doFinal(Base64.getDecoder.decode(valueToDecrypt))
    } match {
      case Success(value) => new String(value)
      case Failure(ex) => throw processCipherTextFailure(ex, METHOD_DECRYPT)
    }
  }

  private def validateAssociatedText(associatedText: String, method: String): Array[Byte] = {
    associatedText match {
      case text if text.length > 0 => text.getBytes
      case _ => throw new EncryptionDecryptionException(method, "associated text must not be null", "associated text was not defined")
    }
  }

  private def processCipherTextFailure(ex: Throwable, method: String): Throwable = ex match {
    case e: NoSuchAlgorithmException => throw new EncryptionDecryptionException(method, "Algorithm being requested is not available in this environment",
      e.getMessage)
    case e: NoSuchPaddingException => throw new EncryptionDecryptionException(method, "Padding Scheme being requested is not available this environment",
      e.getMessage)
    case e: InvalidKeyException => throw new EncryptionDecryptionException(method, "Key being used is not valid." +
      " It could be due to invalid encoding, wrong length or uninitialized", e.getMessage)
    case e: InvalidAlgorithmParameterException => throw new EncryptionDecryptionException(method, "Algorithm parameters being specified are not valid",
      e.getMessage)
    case e: IllegalStateException => throw new EncryptionDecryptionException(method, "Cipher is in an illegal state", e.getMessage)
    case e: UnsupportedOperationException => throw new EncryptionDecryptionException(method, "Provider might not be supporting this method", e.getMessage)
    case e: IllegalBlockSizeException => throw new EncryptionDecryptionException(method, "Error occured due to block size", e.getMessage)
    case e: BadPaddingException => throw new EncryptionDecryptionException(method, "Error occured due to padding scheme", e.getMessage)
    case _ => throw new EncryptionDecryptionException(method, "Unexpected exception", ex.getMessage)
  }

}
