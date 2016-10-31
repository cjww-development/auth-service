// Copyright (C) 2011-2012 the original author or authors.
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


package config

import com.typesafe.config.ConfigFactory

trait FrontendConfiguration {
  final val config = ConfigFactory.load

  final val env = config.getString("cjww.environment")

  final val apiCall = config.getString(s"$env.routes.rest-api")
  final val sessionStore = config.getString(s"$env.routes.session-store")

  final val diagnosticsFrontend = config.getString(s"$env.routes.diagnostics")
  final val deversityFrontend = s"deversity-frontend"
  final val hubFrontend = s"hub-frontend"


  final val APPLICATION_ID = config.getString(s"$env.application-ids.auth-service")
}