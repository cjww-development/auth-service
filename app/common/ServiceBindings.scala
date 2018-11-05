/*
 * Copyright 2018 CJWW Development
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

package common

import com.cjwwdev.config.{ConfigurationLoader, DefaultConfigurationLoader}
import com.cjwwdev.featuremanagement.models.Features
import com.cjwwdev.filters.IpWhitelistFilter
import com.cjwwdev.health.{DefaultHealthController, HealthController}
import com.cjwwdev.http.headers.filters.{DefaultHeadersFilter, HeadersFilter}
import com.cjwwdev.shuttering.filters.FrontendShutteringFilter
import connectors._
import connectors.test._
import controllers.login.{DefaultLoginController, LoginController}
import controllers.redirect.{DefaultRedirectController, RedirectController}
import controllers.register.{DefaultOrgRegisterController, DefaultUserRegisterController, OrgRegisterController, UserRegisterController}
import controllers.test._
import controllers.user._
import controllers.user.deversity.{ClassroomController, DefaultClassroomController}
import filters.{DefaultIpWhitelistFilter, DefaultShutteringFilter}
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import services._

class ServiceBindings extends Module {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
    bindOther() ++ bindConnectors() ++ bindServices() ++ bindControllers()

  private def bindConnectors(): Seq[Binding[_]] = Seq(
    bind(classOf[TeardownConnector]).to(classOf[DefaultTeardownConnector]).eagerly(),
    bind(classOf[AccountsMicroserviceConnector]).to(classOf[DefaultAccountsMicroserviceConnector]).eagerly(),
    bind(classOf[AuthMicroserviceConnector]).to(classOf[DefaultAuthMicroserviceConnector]).eagerly(),
    bind(classOf[DeversityMicroserviceConnector]).to(classOf[DefaultDeversityMicroserviceConnector]).eagerly(),
    bind(classOf[SessionStoreConnector]).to(classOf[DefaultSessionStoreConnector]).eagerly()
  )

  private def bindServices(): Seq[Binding[_]] = Seq(
    bind(classOf[DashboardService]).to(classOf[DefaultDashboardService]).eagerly(),
    bind(classOf[FeedService]).to(classOf[DefaultFeedService]).eagerly(),
    bind(classOf[LoginService]).to(classOf[DefaultLoginService]).eagerly(),
    bind(classOf[RegisterService]).to(classOf[DefaultRegisterService]).eagerly(),
    bind(classOf[RegistrationCodeService]).to(classOf[DefaultRegistrationCodeService]).eagerly(),
    bind(classOf[ClassroomService]).to(classOf[DefaultClassroomService]).eagerly()
  )

  private def bindControllers(): Seq[Binding[_]] = Seq(
    bind(classOf[LoginController]).to(classOf[DefaultLoginController]).eagerly(),
    bind(classOf[RedirectController]).to(classOf[DefaultRedirectController]).eagerly(),
    bind(classOf[OrgRegisterController]).to(classOf[DefaultOrgRegisterController]).eagerly(),
    bind(classOf[UserRegisterController]).to(classOf[DefaultUserRegisterController]).eagerly(),
    bind(classOf[TeardownController]).to(classOf[DefaultTeardownController]).eagerly(),
    bind(classOf[DashboardController]).to(classOf[DefaultDashboardController]).eagerly(),
    bind(classOf[EditProfileController]).to(classOf[DefaultEditProfileController]).eagerly(),
    bind(classOf[GenerateCodeController]).to(classOf[DefaultGenerateCodeController]).eagerly(),
    bind(classOf[ClassroomController]).to(classOf[DefaultClassroomController]).eagerly(),
    bind(classOf[HealthController]).to(classOf[DefaultHealthController]).eagerly(),
    bind(classOf[FeatureController]).to(classOf[DefaultFeatureController]).eagerly()
  )

  private def bindOther(): Seq[Binding[_]] = Seq(
    bind(classOf[ConfigurationLoader]).to(classOf[DefaultConfigurationLoader]).eagerly(),
    bind(classOf[HeadersFilter]).to(classOf[DefaultHeadersFilter]).eagerly(),
    bind(classOf[IpWhitelistFilter]).to(classOf[DefaultIpWhitelistFilter]).eagerly(),
    bind(classOf[FrontendShutteringFilter]).to(classOf[DefaultShutteringFilter]).eagerly(),
    bind(classOf[Features]).to(classOf[FeatureDef]).eagerly()
  )
}
