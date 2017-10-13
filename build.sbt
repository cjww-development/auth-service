import com.typesafe.config.ConfigFactory
import scoverage.ScoverageKeys
import scala.util.{Failure, Success, Try}

val btVersion: String = Try(ConfigFactory.load.getString("version")) match {
  case Success(ver) => ver
  case Failure(_)   => "0.1.0"
}

name         := """auth-service"""
version      := btVersion
scalaVersion := "2.11.11"
organization := "com.cjww-dev.frontends"

lazy val playSettings : Seq[Setting[_]] = Seq.empty

lazy val scoverageSettings = Seq(
  ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;models/.data/..*;config.*;utils.*;views.*;.*(AuthService|BuildInfo|Routes).*",
  ScoverageKeys.coverageMinimum          := 80,
  ScoverageKeys.coverageFailOnMinimum    := false,
  ScoverageKeys.coverageHighlighting     := true
)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(playSettings ++ scoverageSettings : _*)
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    Keys.fork in IntegrationTest := false,
    unmanagedSourceDirectories in IntegrationTest <<= (baseDirectory in IntegrationTest)(base => Seq(base / "it")),
    parallelExecution in IntegrationTest := false)


PlayKeys.devSettings := Seq("play.server.http.port" -> "8602")

val cjwwDep : Seq[ModuleID] = Seq(
  "com.cjww-dev.libs" % "data-security_2.11"         % "2.8.0",
  "com.cjww-dev.libs" % "http-verbs_2.11"            % "2.5.0",
  "com.cjww-dev.libs" % "authorisation_2.11"         % "1.14.0",
  "com.cjww-dev.libs" % "application-utilities_2.11" % "2.3.0"
)

val testDep : Seq[ModuleID] = Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.1"  % Test,
  "org.mockito"             % "mockito-core"       % "2.10.0" % Test
)

libraryDependencies ++= cjwwDep
libraryDependencies ++= testDep

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += "cjww-dev" at "http://dl.bintray.com/cjww-development/releases"

herokuAppName in Compile := "cjww-auth-service"

bintrayOrganization                  := Some("cjww-development")
bintrayReleaseOnPublish in ThisBuild := false
bintrayRepository                    := "releases"
bintrayOmitLicense                   := true
