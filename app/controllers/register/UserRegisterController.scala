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


package controllers.register

import javax.inject.Inject

import com.cjwwdev.auth.connectors.AuthConnector
import com.cjwwdev.views.html.templates.errors.StandardErrorView
import common.FrontendController
import enums.Registration
import forms.UserRegisterForm
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import services.RegisterService
import views.html.register.{RegisterSuccess, UserRegisterView}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserRegisterControllerImpl @Inject()(val registrationService : RegisterService,
                                           val authConnector: AuthConnector,
                                           implicit val messagesApi: MessagesApi) extends UserRegisterController

trait UserRegisterController extends FrontendController {
  val registrationService: RegisterService

  def show : Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(UserRegisterView(UserRegisterForm.RegisterUserForm)))
  }

  def submit : Action[AnyContent] = Action.async { implicit request =>
    UserRegisterForm.RegisterUserForm.bindFromRequest.fold(
      errors  => Future.successful(BadRequest(UserRegisterView(errors))),
      newUser => registrationService.registerIndividual(newUser) map {
        case Registration.success       => Ok(RegisterSuccess("individual"))
        case Registration.bothInUse     => BadRequest(UserRegisterView(
          UserRegisterForm.RegisterUserForm.fill(newUser)
            .withError("userName", messagesApi("cjww.auth.register.generic.userName.inUse"))
            .withError("orgEmail", messagesApi("cjww.auth.register.generic.email.inUse"))
        ))
        case Registration.emailInUse    => BadRequest(UserRegisterView(
          UserRegisterForm.RegisterUserForm.fill(newUser).withError("orgEmail", messagesApi("cjww.auth.register.generic.email.inUse"))
        ))
        case Registration.userNameInUse => BadRequest(UserRegisterView(
          UserRegisterForm.RegisterUserForm.withError("userName", messagesApi("cjww.auth.register.generic.userName.inUse"))
        ))
        case Registration.failed        => InternalServerError(StandardErrorView(messagesApi("cjww.auth.error.generic")))
      }
    )
  }
}
