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

import com.google.inject.ImplementedBy
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.Inject
import scala.concurrent.duration.Duration

@ImplementedBy(classOf[BackendAppConfig])
trait AppConfig {

  def authBaseUrl: String

  def desBaseUrl: String

  def auditingEnabled: Boolean
  def graphiteHost: String

  def desEnvironment: String
  def incomeTaxSubmissionStubUrl: String
  def ifEnvironment: String
  def authorisationToken: String
  def ifBaseUrl: String
  def hipBaseUrl: String

  def iFAuthorisationToken(api: String): String
  def hipSecret(apiNumber: String): String
  def hipClientId(apiNumber: String): String


  def mongoTTL: Int
  def encryptionKey: String
  def useEncryption: Boolean
  def useBusinessDetailsStub: Boolean
  def confidenceLevel: Int
  def useGetCalcDetailsHipPlatform5294: Boolean
  def useGetCalcListHip5624: Boolean
}

class BackendAppConfig @Inject()(config: Configuration, servicesConfig: ServicesConfig) extends AppConfig {

  def authBaseUrl: String = servicesConfig.baseUrl("auth")

  def desBaseUrl: String = servicesConfig.baseUrl("des")
  def incomeTaxSubmissionStubUrl: String = config.get[String]("income-tax-submission-stub-url")
  def ifBaseUrl: String = servicesConfig.baseUrl("if")

  def hipBaseUrl: String = servicesConfig.baseUrl("hip")

  def auditingEnabled: Boolean = config.get[Boolean]("auditing.enabled")
  def graphiteHost: String = config.get[String]("microservice.metrics.graphite.host")

  def desEnvironment: String = config.get[String]("microservice.services.des.environment")
  def ifEnvironment: String = config.get[String]("microservice.services.if.environment")

  def authorisationToken: String = config.get[String]("microservice.services.des.authorisation-token")

  def iFAuthorisationToken(api: String): String = config.get[String](s"microservice.services.if.authorisation-token.$api")

  // mongo config
   def encryptionKey: String = servicesConfig.getString("mongodb.encryption.key")
   def mongoTTL: Int = Duration(servicesConfig.getString("mongodb.timeToLive")).toMinutes.toInt

  override def confidenceLevel: Int = config.get[Int]("microservice.services.auth.confidenceLevel")

   def useGetCalcDetailsHipPlatform5294: Boolean = servicesConfig.getBoolean("feature-switch.useGetCalcDetailsHipPlatform5294")
   def useGetCalcListHip5624: Boolean = servicesConfig.getBoolean("feature-switch.useGetCalcListHipPlatform5624")

  def useEncryption: Boolean = servicesConfig.getBoolean("feature-switch.useEncryption")
  def useBusinessDetailsStub: Boolean = servicesConfig.getBoolean("feature-switch.useBusinessDetailsStub")

  override def hipSecret(apiNumber: String): String = config.get[String](s"microservice.services.hip.$apiNumber.secret")
  override def hipClientId(apiNumber: String): String = config.get[String](s"microservice.services.hip.$apiNumber.clientId")
}
