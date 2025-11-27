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

import sbt.*

object AppDependencies {

  private val bootstrapPlay30Version = "10.2.0"
  private val mongoPlay30Version = "2.10.0"
  private val scalaTestVersion = "3.2.19"
  private val pegdownVersion = "1.6.0"
  private val jacksonModuleScalaVersion = "2.20.1"
  private val wiremockVersion = "3.0.1"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-backend-play-30"  % bootstrapPlay30Version,
    "uk.gov.hmrc.mongo"            %% "hmrc-mongo-play-30"         % mongoPlay30Version,
    "com.fasterxml.jackson.module" %% "jackson-module-scala"       % jacksonModuleScalaVersion
  )
  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"   % bootstrapPlay30Version % "test",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-30"  % mongoPlay30Version     % "test",
    "org.scalatest"           %% "scalatest"                % scalaTestVersion       % "test",
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"   % bootstrapPlay30Version % "test",
    "org.pegdown"             %  "pegdown"                  % pegdownVersion         % "test",
    "com.vladsch.flexmark"    %  "flexmark-all"             % "0.64.8"               % "test",
    "com.github.tomakehurst"  %  "wiremock"                 % wiremockVersion        % "test",
    "org.scalamock"           %% "scalamock"                % "7.5.2"                % "test"
  )
}