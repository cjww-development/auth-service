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

import com.cjwwdev.frontendUI.builders.NavBarLinkBuilder
import com.typesafe.config.ConfigFactory
import controllers.login.{routes => loginRoutes}
import controllers.redirect.{routes => redirectRoutes}
import controllers.register.{routes => registerRoutes}
import controllers.user.{routes => userRoutes}
import controllers.{routes => assetRoutes}
import play.api.mvc.{Call, RequestHeader}

trait ApplicationConfiguration {
  //FeedServiceConfig
  val EDIT_PROFILE              = "edit-profile"
  val TITLE                     = "Your profile has been updated"

  val LOGIN_CALLBACK            = controllers.login.routes.LoginController.show(None)

  val LOGIN_REDIRECT            = "/account-services/login"

  //Account types
  val ORGANISATION              = "organisation"
  val INDIVIDUAL                = "individual"

  def buildServiceUrl(service: String): String = ConfigFactory.load.getString(s"microservice.external-services.$service.domain")

  //routes
  val accountsMicroservice      = buildServiceUrl("accounts-microservice")
  val authMicroservice          = buildServiceUrl("auth-microservice")
  val sessionStore              = buildServiceUrl("session-store")
  val authService               = buildServiceUrl("auth-service")
  val diagnosticsFrontend       = buildServiceUrl("diagnostics-frontend")
  val deversityFrontend         = buildServiceUrl("deversity-frontend")
  val deversityMicroservice     = buildServiceUrl("deversity")
  val hubFrontend               = buildServiceUrl("hub-frontend")

  implicit def serviceLinks(implicit requestHeader: RequestHeader): Seq[NavBarLinkBuilder] = Seq(
    NavBarLinkBuilder("/", "glyphicon-home", "Home", "home"),
    NavBarLinkBuilder(redirectRoutes.RedirectController.redirectToDiagnostics().url, "glyphicon-wrench", "Diagnostics", "diagnostics"),
    NavBarLinkBuilder(redirectRoutes.RedirectController.redirectToDeversity().url, "glyphicon-education", "Deversity", "deversity"),
    NavBarLinkBuilder("/", "glyphicon-asterisk", "Hub", "the-hub")
  )

  implicit def standardNavBarRoutes(implicit requestHeader: RequestHeader): Map[String, Call] = Map(
    "navBarLogo"    -> assetRoutes.Assets.versioned("images/logo.png"),
    "globalAssets"  -> assetRoutes.Assets.versioned("stylesheets/global-assets.css"),
    "favicon"       -> assetRoutes.Assets.versioned("images/favicon.ico"),
    "userRegister"  -> registerRoutes.UserRegisterController.show(),
    "orgRegister"   -> registerRoutes.OrgRegisterController.show(),
    "login"         -> loginRoutes.LoginController.show(None),
    "dashboard"     -> userRoutes.DashboardController.show(),
    "signOut"       -> loginRoutes.LoginController.signOut()
  )
}
