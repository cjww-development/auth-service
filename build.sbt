import scoverage.ScoverageKeys

name := """auth-service"""

version := "1.0-SNAPSHOT"

lazy val playSettings : Seq[Setting[_]] = Seq.empty

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(playSettings ++ scoverageSettings : _*)

scalaVersion := "2.11.7"

//PlayKeys.devSettings := Seq("play.server.http.port" -> "disabled")
PlayKeys.devSettings := Seq("play.server.http.port" -> "8602")

libraryDependencies ++= Seq(
  cache,
  "com.typesafe.play" %% "play-ws" % "2.5.4",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "org.mockito" % "mockito-core" % "1.8.5",
  "org.reactivemongo" %% "reactivemongo" % "0.11.14",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.14"
)

lazy val scoverageSettings = {

  // Semicolon-separated list of regexs matching classes to exclude
  Seq(
    ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;models/.data/..*;config.*;utils.*;views.*;.*(AuthService|BuildInfo|Routes).*",
    ScoverageKeys.coverageMinimum := 80,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true
  )
}

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

herokuAppName in Compile := "cjww-auth-service"
