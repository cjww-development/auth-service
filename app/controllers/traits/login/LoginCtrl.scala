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
package controllers.traits.login

import connectors.SessionStoreConnector
import controllers.login.routes
import forms.UserLoginForm
import play.api.mvc.{Action, AnyContent}
import utils.application.FrontendController
import forms.UserLoginForm._
import models.UserLogin
import play.api.Logger
import services.LoginService
import views.html.login.UserLoginView

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait LoginCtrl extends FrontendController {

  val userLogin : LoginService
  val sessionStoreConnector : SessionStoreConnector

  def show(redirect : Option[String]) : Action[AnyContent] = Action.async {
    implicit request =>
      Future.successful(Ok(UserLoginView(loginForm.fill(UserLogin.empty))))
  }

  def submit : Action[AnyContent] = Action.async {
    implicit request =>
      UserLoginForm.loginForm.bindFromRequest.fold(
        errors => Future.successful(BadRequest(UserLoginView(errors))),
        valid =>
          userLogin.processLoginAttempt(valid) flatMap {
            case Some((session, Some(data))) =>
              sessionStoreConnector.cache(session("cookieID"), data).map {
                _ => Redirect(serviceDirector).withSession(session)
              }
            case None => Future.successful(Ok(UserLoginView(loginForm.fill(valid).withGlobalError("Your user name or password is incorrect"))))
          }
      )
  }

  def signOut : Action[AnyContent] = Action.async {
    implicit request =>
      sessionStoreConnector.destroySession(request.session("cookieID")) map {
        resp =>
          Logger.info(s"[LoginController] - [signOut] : Response from session store - ${resp.status} : ${resp.statusText}")
          Redirect(routes.LoginController.show(None)).withNewSession
      }
  }
}
