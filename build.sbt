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
    ScoverageKeys.coverageMinimumStmtTotal := 95,
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

lazy val it = project
  .dependsOn(microservice % "test->test")
  .settings(DefaultBuildSettings.itSettings(false))
  .enablePlugins(play.sbt.PlayScala)
  .settings(
    publish / skip := true
  )
  .settings(scalaVersion := currentScalaVersion)
  .settings(majorVersion := 1)
  //.settings(scalacOptions += "-Xfatal-warnings") -> TODO: fix it:test warnings && re-enable
  .settings(
    testForkedParallel := true
  )
  .settings(
    libraryDependencies ++= AppDependencies.test
  )