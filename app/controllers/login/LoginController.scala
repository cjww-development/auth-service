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
package controllers.login

import com.cjwwdev.auth.connectors.AuthConnector
import common.FrontendController
import connectors.SessionStoreConnector
import forms.UserLoginForm
import javax.inject.Inject
import models.UserLogin
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import services.LoginService
import views.html.login.UserLoginView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginControllerImpl @Inject()(val loginService : LoginService,
                                    val sessionStoreConnector: SessionStoreConnector,
                                    val authConnector: AuthConnector,
                                    implicit val messagesApi: MessagesApi) extends LoginController {
  override val loginFailed: String = messagesApi("cjww.auth.login.error.invalid")
}

trait LoginController extends FrontendController {
  val loginService: LoginService
  val sessionStoreConnector: SessionStoreConnector

  val loginFailed: String

  private val loginForm: Form[UserLogin] = UserLoginForm.loginForm

  def show(redirect : Option[String]) : Action[AnyContent] = Action { implicit request =>
    Ok(UserLoginView(loginForm))
  }

  def submit : Action[AnyContent] = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      errors => Future.successful(BadRequest(UserLoginView(errors))),
      valid  => loginService.processLoginAttempt(valid) map {
        case Some(session) => Redirect(routes.LoginController.activateAuthServiceSession(session("cookieId"))).withSession(session)
        case None          => Ok(UserLoginView(loginForm.fill(valid).withError("userName", loginFailed).withError("password", "")))
      }
    )
  }

  def activateAuthServiceSession(cookieId: String): Action[AnyContent] = Action { implicit request =>
    Redirect(s"$deversityFrontend/private/build-deversity-session/$cookieId")
  }

  def redirectToServiceSelector: Action[AnyContent] = Action(implicit request => Redirect(serviceDirector))

  def signOut : Action[AnyContent] = isAuthorised { implicit user => implicit request =>
    sessionStoreConnector.destroySession map {
      _ => Redirect(routes.LoginController.show(None)).withNewSession
    }
  }
}
