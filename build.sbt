// Copyright (C) 2016-2017 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import com.typesafe.config.ConfigFactory
import scoverage.ScoverageKeys
import scala.util.{Failure, Success, Try}

val appName = "auth-service"

val btVersion: String = Try(ConfigFactory.load.getString("version")) match {
  case Success(ver) => ver
  case Failure(_)   => "0.1.0"
}

lazy val scoverageSettings = Seq(
  ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;models/.data/..*;config.*;utils.*;views.*;.*(AuthService|BuildInfo|Routes).*",
  ScoverageKeys.coverageMinimum          := 80,
  ScoverageKeys.coverageFailOnMinimum    := false,
  ScoverageKeys.coverageHighlighting     := true
)

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala)
  .settings(scoverageSettings)
  .configs(IntegrationTest)
  .settings(PlayKeys.playDefaultPort := 8602)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    name                                          :=  """auth-service""",
    version                                       :=  btVersion,
    scalaVersion                                  :=  "2.11.12",
    organization                                  :=  "com.cjww-dev.frontends",
    resolvers                                     ++= Seq(
      "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
      "cjww-dev"       at "http://dl.bintray.com/cjww-development/releases"
    ),
    libraryDependencies                           ++= AppDependencies(),
    libraryDependencies                           +=  filters,
    herokuAppName in Compile                      :=  "cjww-auth-service",
    bintrayOrganization                           :=  Some("cjww-development"),
    bintrayReleaseOnPublish in ThisBuild          :=  false,
    bintrayRepository                             :=  "releases",
    bintrayOmitLicense                            :=  true,
    Keys.fork in IntegrationTest                  :=  false,
    unmanagedSourceDirectories in IntegrationTest :=  (baseDirectory in IntegrationTest)(base => Seq(base / "it")).value,
    parallelExecution in IntegrationTest          :=  false
  )
