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

package filters

import akka.stream.Materializer
import com.cjwwdev.frontendUI.builders.NavBarLinkBuilder
import com.cjwwdev.shuttering.filters.FrontendShutteringFilter
import controllers.login.{routes => loginRoutes}
import controllers.redirect.{routes => redirectRoutes}
import controllers.register.{routes => registerRoutes}
import controllers.user.{routes => userRoutes}
import controllers.{routes => assetRoutes}
import javax.inject.Inject
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc.{Call, RequestHeader}

class DefaultShutteringFilter @Inject()(implicit val mat: Materializer,
                                        implicit val messages: MessagesApi,
                                        val langs: Langs) extends FrontendShutteringFilter {
  private def deversityEnabled: Boolean = System.getProperty("features.deversity", "false").toBoolean

  override implicit def pageLinks(implicit rh: RequestHeader): Seq[NavBarLinkBuilder] = {
    val home = Seq(NavBarLinkBuilder("/", "glyphicon-home", "Home", "home"))
    val diag = Seq(NavBarLinkBuilder(redirectRoutes.RedirectController.redirectToDiagnostics().url, "glyphicon-wrench", "Diagnostics", "diagnostics"))
    val dev  = Seq(NavBarLinkBuilder(redirectRoutes.RedirectController.redirectToDeversity().url, "glyphicon-education", "Deversity", "deversity"))
    val hub  = Seq(NavBarLinkBuilder("/", "glyphicon-asterisk", "Hub", "the-hub"))

    home ++ diag ++ (if(deversityEnabled) dev else Seq.empty) ++ hub
  }

  override implicit def navBarRoutes(implicit rh: RequestHeader): Map[String, Call]   = Map(
    "navBarLogo"   -> assetRoutes.Assets.versioned("images/logo.png"),
    "globalAssets" -> assetRoutes.Assets.versioned("stylesheets/global-assets.css"),
    "favicon"      -> assetRoutes.Assets.versioned("images/favicon.ico"),
    "userRegister" -> registerRoutes.UserRegisterController.show(),
    "orgRegister"  -> registerRoutes.OrgRegisterController.show(),
    "login"        -> loginRoutes.LoginController.show(None),
    "dashboard"    -> userRoutes.DashboardController.show(),
    "signOut"      -> loginRoutes.LoginController.signOut()
  )
}
