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

package controllers.register

import javax.inject.Inject

import com.cjwwdev.auth.actions.{Actions, AuthActions}
import com.cjwwdev.auth.connectors.AuthConnector
import com.google.inject.Singleton
import connectors._
import forms.UserRegisterForm
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import utils.application.FrontendController
import views.html.error_template
import views.html.register.{RegisterSuccess, UserRegisterView}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class UserRegisterController @Inject()(messagesApi: MessagesApi,
                                       userRegister : UserRegistrationConnector,
                                       authConnect: AuthConnector) extends FrontendController with Actions {

  val authConnector = authConnect

  def show : Action[AnyContent] = unauthenticatedAction.async {
    implicit potentialUser =>
      implicit request =>
        Future.successful(Ok(UserRegisterView(UserRegisterForm.RegisterUserForm)))
  }

  def submit : Action[AnyContent] = unauthenticatedAction.async {
    implicit potentialUser =>
      implicit request =>
        UserRegisterForm.RegisterUserForm.bindFromRequest.fold(
          errors => {
            Future.successful(BadRequest(UserRegisterView(errors)))
          },
          newUser => {
            userRegister.createNewIndividualUser(newUser.encryptPasswords) map {
              case UserRegisterSuccessResponse(_) => Ok(RegisterSuccess("individual"))
              case UserRegisterClientErrorResponse(_) => BadRequest(error_template(messagesApi("cjww.auth.error.generic")))
              case UserRegisterServerErrorResponse(_) => InternalServerError(error_template(messagesApi("cjww.auth.error.generic")))
              case UserRegisterErrorResponse(_) => InternalServerError(error_template(messagesApi("cjww.auth.error.generic")))
            }
          }
        )
  }
}
