import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-27"  % "5.3.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-27"   % "5.3.0" % "test, it",
    "org.scalatest"           %% "scalatest"                % "3.2.9"  % "test, it",
    "com.typesafe.play"       %% "play-test"                % current  % "test, it",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"  % "test, it",
    "com.vladsch.flexmark"    %  "flexmark-all"             % "0.36.8" % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "4.0.3"  % "test, it",
    "com.github.tomakehurst"  %  "wiremock-jre8"            % "2.28.0" % "test, it",
    "org.scalamock"           %% "scalamock"                % "5.1.0"  % "test, it"
  )
}
