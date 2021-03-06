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


package controllers.register

import com.cjwwdev.auth.connectors.AuthConnector
import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.featuremanagement.services.FeatureService
import com.cjwwdev.views.html.templates.errors.StandardErrorView
import common.helpers.FrontendController
import common.{FeatureManagement, RedirectUrls}
import enums.Registration
import forms.UserRegisterForm
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.RegisterService
import views.html.register.{RegisterSuccess, UserRegisterView}

import scala.concurrent.{ExecutionContext, Future}

class DefaultUserRegisterController @Inject()(val registrationService : RegisterService,
                                              val controllerComponents: ControllerComponents,
                                              val authConnector: AuthConnector,
                                              val config: ConfigurationLoader,
                                              val featureService: FeatureService,
                                              implicit val ec: ExecutionContext) extends UserRegisterController with RedirectUrls

trait UserRegisterController extends FrontendController with FeatureManagement {
  val registrationService: RegisterService

  def show : Action[AnyContent] = Action.async { implicit req =>
    Future.successful(Ok(UserRegisterView(UserRegisterForm.RegisterUserForm)))
  }

  def submit : Action[AnyContent] = Action.async { implicit req =>
    UserRegisterForm.RegisterUserForm.bindFromRequest.fold(
      errors  => Future.successful(BadRequest(UserRegisterView(errors))),
      newUser => registrationService.registerIndividual(newUser) map {
        case Registration.success       => Ok(RegisterSuccess("individual"))
        case Registration.bothInUse     => BadRequest(UserRegisterView(
          UserRegisterForm.RegisterUserForm.fill(newUser)
            .withError("userName", messagesApi("cjww.auth.register.generic.userName.inUse"))
            .withError("email", messagesApi("cjww.auth.register.generic.email.inUse"))
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
