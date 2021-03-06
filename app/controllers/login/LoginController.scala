/*
 * Copyright 2019 CJWW Development
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
package controllers.login

import com.cjwwdev.auth.connectors.AuthConnector
import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.featuremanagement.services.FeatureService
import common.helpers.AuthController
import common.{FeatureManagement, RedirectUrls}
import connectors.SessionStoreConnector
import forms.UserLoginForm
import javax.inject.Inject
import models.UserLogin
import play.api.data.Form
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.LoginService
import views.html.login.UserLoginView

import scala.concurrent.{ExecutionContext, Future}

class DefaultLoginController @Inject()(val loginService : LoginService,
                                       val sessionStoreConnector: SessionStoreConnector,
                                       val config: ConfigurationLoader,
                                       val authConnector: AuthConnector,
                                       val featureService: FeatureService,
                                       val controllerComponents: ControllerComponents,
                                       implicit val ec: ExecutionContext) extends LoginController with RedirectUrls

trait LoginController extends AuthController with FeatureManagement {
  val loginService: LoginService
  val sessionStoreConnector: SessionStoreConnector

  private val loginForm: Form[UserLogin] = UserLoginForm.loginForm

  def show(redirect : Option[String]) : Action[AnyContent] = Action { implicit req =>
    Ok(UserLoginView(loginForm))
  }

  def submit : Action[AnyContent] = Action.async { implicit req =>
    loginForm.bindFromRequest.fold(
      errors => Future.successful(BadRequest(UserLoginView(errors))),
      valid  => loginService.processLoginAttempt(valid) map {
        case Some(session) => Redirect(routes.LoginController.activateAuthServiceSession(session("cookieId"))).withSession(session)
        case None          => Ok(UserLoginView(loginForm.fill(valid)
          .withError("userName", messagesApi("cjww.auth.login.error.invalid")).withError("password", "")))
      }
    )
  }

  def activateAuthServiceSession(cookieId: String): Action[AnyContent] = Action { implicit req =>
    if(deversityEnabled) {
      Redirect(s"$deversityFrontend/private/build-deversity-session/$cookieId")
    } else {
      Redirect(controllers.redirect.routes.RedirectController.chooseService())
    }
  }

  def redirectToServiceSelector: Action[AnyContent] = Action(implicit req => Redirect(serviceDirector))

  def signOut : Action[AnyContent] = isAuthorised { implicit user => implicit req =>
    sessionStoreConnector.destroySession map {
      _ => Redirect(routes.LoginController.show(None)).withNewSession
    }
  }
}
