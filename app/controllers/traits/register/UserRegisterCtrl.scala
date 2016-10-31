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

package controllers.traits.register

import connectors._
import models.UserRegister
import forms.UserRegisterForm._
import play.api.i18n.Messages.Implicits._
import play.api.i18n.Messages
import play.api.mvc.{Action, AnyContent}
import utils.application.FrontendController
import views.html.register.{RegisterSuccess, UserRegisterView}
import views.html.error_template

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait UserRegisterCtrl extends FrontendController {

  val userRegister : UserRegistrationConnector

  val errorMessage : String

  def show : Action[AnyContent] = Action.async {
    implicit request =>
      Future.successful(Ok(UserRegisterView(RegisterUserForm.fill(UserRegister.empty))))
  }

  def submit : Action[AnyContent] = Action.async {
    implicit request =>
      RegisterUserForm.bindFromRequest.fold(
        errors => Future.successful(BadRequest(UserRegisterView(errors))),
        newUser =>
          userRegister.createNewIndividualUser(newUser.encryptPasswords) map {
            case UserRegisterSuccessResponse(code) => Ok(RegisterSuccess("individual"))
            case UserRegisterClientErrorResponse(code) => BadRequest(error_template(errorMessage))
            case UserRegisterServerErrorResponse(code) => InternalServerError(error_template(errorMessage))
            case UserRegisterErrorResponse(code) => InternalServerError(error_template(errorMessage))
          }
      )
  }
}