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

package config

import com.typesafe.config.ConfigFactory

class MockAppConfig extends AppConfig {

  override val incomeTaxSubmissionStubUrl: String = "/stub"

  override val authBaseUrl: String = "/auth"

  override val desBaseUrl: String = "/des"

  override val ifBaseUrl: String = "/if"

  override val auditingEnabled: Boolean = true

  override val graphiteHost: String = "/graphite"

  override val desEnvironment: String = "dev"

  override val ifEnvironment: String = "dev"

  override val authorisationToken: String = "someToken"

  override def iFAuthorisationToken(api: String): String = "someToken"

  override lazy val encryptionKey: String = "key"

  override lazy val mongoTTL: Int = 60

  override lazy val useEncryption: Boolean = true

  override val useBusinessDetailsStub: Boolean = false

  override val confidenceLevel = ConfigFactory.load().getInt("microservice.services.auth.confidenceLevel")

  override val useGetCalcListIFPlatform: Boolean = true

  def config(encrypt: Boolean = true): AppConfig = new AppConfig() {

    override val incomeTaxSubmissionStubUrl: String = "/stub"

    override val authBaseUrl: String = "/auth"

    override val desBaseUrl: String = "/des"

    override val ifBaseUrl: String = "/if"

    override val auditingEnabled: Boolean = true

    override val graphiteHost: String = "/graphite"

    override val desEnvironment: String = "dev"

    override val ifEnvironment: String = "dev"

    override val authorisationToken: String = "someToken"

    override def iFAuthorisationToken(api: String): String = "someToken"

    override lazy val encryptionKey: String = "encryptionKey12345"

    override lazy val mongoTTL: Int = 60

    override lazy val useEncryption: Boolean = encrypt

    override val useBusinessDetailsStub: Boolean = false

    override val confidenceLevel = ConfigFactory.load().getInt("microservice.services.auth.confidenceLevel")

    override val hipBaseUrl: String = "/hip"
    override val useGetCalcListHiPlatform: Boolean = false
    override val useGetCalcListIFPlatform: Boolean = true

    override def hipSecret(apiNumber: String): String = "secret"
    override def hipClientId(apiNumber: String): String = "clientId"

    override val useGetCalcDetailsHipPlatform: Boolean = false
  }

  override val hipBaseUrl: String = "/hip"
  override val useGetCalcListHiPlatform: Boolean = false
  override val useGetCalcDetailsHipPlatform: Boolean = false
  override def hipSecret(apiNumber: String): String = "secret"
  override def hipClientId(apiNumber: String): String = "clientId"

}
