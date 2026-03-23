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

import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

val appName = "income-tax-calculation"

val currentScalaVersion = "3.3.6"

lazy val coverageSettings: Seq[Setting[_]] = {
  import scoverage.ScoverageKeys

  val excludedPackages = Seq(
    "<empty>",
    ".*Reverse.*",
    ".*standardError*.*",
    ".*govuk_wrapper*.*",
    ".*main_template*.*",
    "uk.gov.hmrc.BuildInfo",
    "app.*",
    "prod.*",
    "config.*",
    "testOnly.*",
    "testOnlyDoNotUseInAppConf.*",
    "controllers.testOnly.*",
  )

  Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 80,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    majorVersion                     := 0,
    scalaVersion                     := currentScalaVersion,
    libraryDependencies              ++= AppDependencies.compile ++ AppDependencies.test
  )
  .settings(PlayKeys.playDefaultPort := 9314)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(coverageSettings: _*)
  .settings(ThisBuild / scalacOptions ++= Seq(
    "-Wconf:msg=Flag.*repeatedly:s",
    "-Wconf:src=routes/.*:s"
  ))



lazy val it = project
  .dependsOn(microservice % "test->test")
  .settings(DefaultBuildSettings.itSettings(false))
  .enablePlugins(play.sbt.PlayScala)
  .settings(
    publish / skip := true
  )
  .settings(scalaVersion := currentScalaVersion)
  .settings(majorVersion := 1)
  .settings(scalacOptions += "-Xfatal-warnings")
  .settings(
    testForkedParallel := true
  )
  .settings(
    libraryDependencies ++= AppDependencies.test
  )