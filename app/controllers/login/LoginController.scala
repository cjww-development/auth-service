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
package controllers.login

import javax.inject.Inject

import com.cjwwdev.auth.actions.Actions
import com.cjwwdev.logging.Logger
import com.cjwwdev.auth.connectors.AuthConnector
import com.google.inject.Singleton
import config.ApplicationConfiguration
import connectors.SessionStoreConnector
import forms.UserLoginForm
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import services.LoginService
import utils.application.FrontendController
import views.html.login.UserLoginView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class LoginController @Inject()(messagesApi: MessagesApi, configuration: ApplicationConfiguration,
                                userLogin : LoginService, sessionStoreConnector: SessionStoreConnector,
                                authConnect: AuthConnector) extends FrontendController with Actions {

  val authConnector = authConnect

  def show(redirect : Option[String]) : Action[AnyContent] = unauthenticatedAction.async {
    implicit user =>
      implicit request =>
        Future.successful(Ok(UserLoginView(UserLoginForm.loginForm)))
  }

  def submit : Action[AnyContent] = unauthenticatedAction.async {
    implicit user =>
      implicit request =>
        UserLoginForm.loginForm.bindFromRequest.fold(
          errors => Future.successful(BadRequest(UserLoginView(errors))),
          valid => userLogin.processLoginAttempt(valid) map {
            case Some(session) => Redirect(urlParser.serviceDirector).withSession(session)
            case None => Ok(
              UserLoginView(
                UserLoginForm.loginForm.fill(valid).withError("userName", "Your user name or password is incorrect").withError("password", "")
              )
            )
          }
        )
  }

  def signOut : Action[AnyContent] = authorisedFor(configuration.LOGIN_CALLBACK).async {
    implicit user =>
      implicit request =>
        sessionStoreConnector.destroySession map { resp =>
          Logger.info(s"[LoginController] - [signOut] : Response from session store - ${resp.status} : ${resp.statusText}")
          Redirect(routes.LoginController.show(None)).withNewSession
        }
  }
}
