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
package controllers.login

import javax.inject.Inject

import com.cjwwdev.auth.actions.Actions
import com.cjwwdev.auth.connectors.AuthConnector
import com.cjwwdev.config.ConfigurationLoader
import common.FrontendController
import connectors.SessionStoreConnector
import forms.UserLoginForm
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import services.LoginService
import views.html.login.UserLoginView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginControllerImpl @Inject()(val loginService : LoginService,
                                    val sessionStoreConnector: SessionStoreConnector,
                                    val authConnector: AuthConnector,
                                    implicit val messagesApi: MessagesApi) extends LoginController

trait LoginController extends FrontendController with Actions {
  val loginService: LoginService
  val sessionStoreConnector: SessionStoreConnector

  def show(redirect : Option[String]) : Action[AnyContent] = Action { implicit request =>
    Ok(UserLoginView(UserLoginForm.loginForm))
  }

  def submit : Action[AnyContent] = Action.async {
    implicit request =>
      UserLoginForm.loginForm.bindFromRequest.fold(
        errors => Future.successful(BadRequest(UserLoginView(errors))),
        valid  => loginService.processLoginAttempt(valid) map {
          case Some(session) => Redirect(routes.LoginController.activateAuthServiceSession()).withSession(session)
          case None          => Ok(UserLoginView(
            UserLoginForm.loginForm.fill(valid).withError("userName", messagesApi("cjww.auth.login.error.invalid")).withError("password", "")
          ))
        }
      )
  }

  def activateAuthServiceSession: Action[AnyContent] = Action { implicit request =>
    Redirect(s"$deversityFrontend/private/build-deversity-session/${request.session("cookieId")}")
  }

  def redirectToServiceSelector: Action[AnyContent] = Action(implicit request => Redirect(serviceDirector))

  def signOut : Action[AnyContent] = authorisedFor(LOGIN_CALLBACK).async {
    implicit user =>
      implicit request =>
        sessionStoreConnector.destroySession map {
          _ => Redirect(routes.LoginController.show(None)).withNewSession
        }
  }
}
