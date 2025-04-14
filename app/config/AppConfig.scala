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

  val authBaseUrl: String

  val desBaseUrl: String

  val auditingEnabled: Boolean
  val graphiteHost: String

  val desEnvironment: String
  val incomeTaxSubmissionStubUrl: String
  val ifEnvironment: String
  val authorisationToken: String
  val ifBaseUrl: String
  val hipBaseUrl: String

  def iFAuthorisationToken(api: String): String
  def hipSecret(apiNumber: String): String
  def hipClientId(apiNumber: String): String


  val mongoTTL: Int
  val encryptionKey: String
  val useEncryption: Boolean
  val useBusinessDetailsStub: Boolean
  def confidenceLevel: Int
  val useGetCalcListIFPlatform: Boolean
  val useGetCalcListHiPlatform: Boolean
}

class BackendAppConfig @Inject()(config: Configuration, servicesConfig: ServicesConfig) extends AppConfig {

  val authBaseUrl: String = servicesConfig.baseUrl("auth")

  val desBaseUrl: String = servicesConfig.baseUrl("des")
  val incomeTaxSubmissionStubUrl: String = config.get[String]("income-tax-submission-stub-url")
  val ifBaseUrl: String = servicesConfig.baseUrl("if")

  val hipBaseUrl: String = servicesConfig.baseUrl("hip")

  val auditingEnabled: Boolean = config.get[Boolean]("auditing.enabled")
  val graphiteHost: String = config.get[String]("microservice.metrics.graphite.host")

  val desEnvironment: String = config.get[String]("microservice.services.des.environment")
  val ifEnvironment: String = config.get[String]("microservice.services.if.environment")

  val authorisationToken: String = config.get[String]("microservice.services.des.authorisation-token")

  def iFAuthorisationToken(api: String): String = config.get[String](s"microservice.services.if.authorisation-token.$api")

  // mongo config
  lazy val encryptionKey: String = servicesConfig.getString("mongodb.encryption.key")
  lazy val mongoTTL: Int = Duration(servicesConfig.getString("mongodb.timeToLive")).toMinutes.toInt

  override val confidenceLevel: Int = config.get[Int]("microservice.services.auth.confidenceLevel")

  lazy val useGetCalcListIFPlatform: Boolean = servicesConfig.getBoolean("feature-switch.useGetCalcListIFPlatform")
  lazy val useGetCalcListHiPlatform: Boolean = servicesConfig.getBoolean("feature-switch.useGetCalcListHIPlatform")

  lazy val useEncryption: Boolean = servicesConfig.getBoolean("feature-switch.useEncryption")
  lazy val useBusinessDetailsStub: Boolean = servicesConfig.getBoolean("feature-switch.useBusinessDetailsStub")

  override def hipSecret(apiNumber: String): String = config.get[String](s"microservice.services.hip.$apiNumber.secret")
  override def hipClientId(apiNumber: String): String = config.get[String](s"microservice.services.hip.$apiNumber.clientId")

}
