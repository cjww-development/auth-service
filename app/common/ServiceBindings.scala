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

package common

import com.cjwwdev.auth.connectors.AuthConnector
import com.cjwwdev.config.{ConfigurationLoader, ConfigurationLoaderImpl}
import com.google.inject.AbstractModule
import connectors._
import connectors.test._
import services._
import controllers.login._
import controllers.redirect._
import controllers.register._
import controllers.test._
import controllers.user._

class ServiceBindings extends AbstractModule {
  override def configure(): Unit = {
    bindOther()
    bindConnectors()
    bindServices()
    bindControllers()
  }

  private def bindConnectors(): Unit = {
    bind(classOf[TeardownConnector]).to(classOf[TeardownConnectorImpl]).asEagerSingleton()
    bind(classOf[AccountsMicroserviceConnector]).to(classOf[AccountsMicroserviceConnectorImpl]).asEagerSingleton()
    bind(classOf[AuthMicroserviceConnector]).to(classOf[AuthMicroserviceConnectorImpl]).asEagerSingleton()
    bind(classOf[DeversityMicroserviceConnector]).to(classOf[DeversityMicroserviceConnectorImpl]).asEagerSingleton()
    bind(classOf[SessionStoreConnector]).to(classOf[SessionStoreConnectorImpl]).asEagerSingleton()
  }

  private def bindServices(): Unit = {
    bind(classOf[DashboardService]).to(classOf[DashboardServiceImpl]).asEagerSingleton()
    bind(classOf[DeversityService]).to(classOf[DeversityServiceImpl]).asEagerSingleton()
    bind(classOf[FeedService]).to(classOf[FeedServiceImpl]).asEagerSingleton()
    bind(classOf[LoginService]).to(classOf[LoginServiceImpl]).asEagerSingleton()
    bind(classOf[RegisterService]).to(classOf[RegisterServiceImpl]).asEagerSingleton()
    bind(classOf[RegistrationCodeService]).to(classOf[RegistrationCodeServiceImpl]).asEagerSingleton()
  }

  private def bindControllers(): Unit = {
    bind(classOf[LoginController]).to(classOf[LoginControllerImpl]).asEagerSingleton()
    bind(classOf[RedirectController]).to(classOf[RedirectControllerImpl]).asEagerSingleton()
    bind(classOf[OrgRegisterController]).to(classOf[OrgRegisterControllerImpl]).asEagerSingleton()
    bind(classOf[UserRegisterController]).to(classOf[UserRegisterControllerImpl]).asEagerSingleton()
    bind(classOf[TeardownController]).to(classOf[TeardownControllerImpl]).asEagerSingleton()
    bind(classOf[DashboardController]).to(classOf[DashboardControllerImpl]).asEagerSingleton()
    bind(classOf[EditProfileController]).to(classOf[EditProfileControllerImpl]).asEagerSingleton()
    bind(classOf[GenerateCodeController]).to(classOf[GenerateCodeControllerImpl]).asEagerSingleton()
  }

  private def bindOther(): Unit = {
    bind(classOf[ConfigurationLoader]).to(classOf[ConfigurationLoaderImpl]).asEagerSingleton()
  }
}
