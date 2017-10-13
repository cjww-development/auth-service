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

package config

import com.cjwwdev.config.ConfigurationLoader

trait ApplicationConfiguration {

  val config: ConfigurationLoader

  //FeedServiceConfig
  val EDIT_PROFILE              = "edit-profile"
  val TITLE                     = "Your profile has been updated"

  val LOGIN_CALLBACK            = controllers.login.routes.LoginController.show(None)

  //routes
  val accountsMicroservice      = config.buildServiceUrl("accounts-microservice")
  val authMicroservice          = config.buildServiceUrl("auth-microservice")
  val sessionStore              = config.buildServiceUrl("session-store")
  val diagnosticsFrontend       = config.buildServiceUrl("diagnostics-frontend")
  val deversityFrontend         = config.buildServiceUrl("deversity-frontend")
  val deversityMicroservice     = config.buildServiceUrl("deversity")
  val hubFrontend               = config.buildServiceUrl("hub-frontend")
}
