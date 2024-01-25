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

import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  private val bootstrapPlay30Version = "8.1.0"
  private val mongoPlay30Version = "1.6.0"
  private val scalaTestVersion = "3.2.9"
  private val scalaTestPlusVersion = "7.0.0"
  private val pegdownVersion = "1.6.0"
  private val jacksonModuleScalaVersion = "2.16.1"
  private val wiremockVersion = "2.7.1"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-backend-play-30"  % bootstrapPlay30Version,
    "uk.gov.hmrc.mongo"            %% "hmrc-mongo-play-30"         % mongoPlay30Version,
    "com.fasterxml.jackson.module" %% "jackson-module-scala"       % jacksonModuleScalaVersion,
    "com.typesafe.play" %% "play-json-joda" % "2.9.3"
  )
  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"   % bootstrapPlay30Version % "test, it",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-30"  % mongoPlay30Version     % "test, it",
    "org.scalatest"           %% "scalatest"                % scalaTestVersion       % "test, it",
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"   % bootstrapPlay30Version % "test, it",
    "org.pegdown"             %  "pegdown"                  % pegdownVersion         % "test, it",
    "com.vladsch.flexmark"    %  "flexmark-all"             % "0.36.8"               % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % scalaTestPlusVersion   % "test, it",
    "com.github.tomakehurst"  %  "wiremock" % wiremockVersion % "test, it",
    "org.scalamock"           %% "scalamock"                % "5.2.0"                % "test, it"
  )
}