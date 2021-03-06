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
import forms.OrgRegisterForm
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.RegisterService
import views.html.register.{OrgRegisterView, RegisterSuccess}

import scala.concurrent.{ExecutionContext, Future}

class DefaultOrgRegisterController @Inject()(val registrationService : RegisterService,
                                             val controllerComponents: ControllerComponents,
                                             val authConnector: AuthConnector,
                                             val config: ConfigurationLoader,
                                             val featureService: FeatureService,
                                             implicit val ec: ExecutionContext) extends OrgRegisterController with RedirectUrls

trait OrgRegisterController extends FrontendController with FeatureManagement {
  val registrationService: RegisterService

  def show: Action[AnyContent] = Action.async { implicit req =>
    Future.successful(Ok(OrgRegisterView(OrgRegisterForm.orgRegisterForm)))
  }

  def submit: Action[AnyContent] = Action.async { implicit req =>
    OrgRegisterForm.orgRegisterForm.bindFromRequest.fold(
      errors => Future.successful(BadRequest(OrgRegisterView(errors))),
      newOrg => registrationService.registerOrg(newOrg) map {
        case Registration.success       => Ok(RegisterSuccess("organisation"))
        case Registration.bothInUse     => BadRequest(OrgRegisterView(
          OrgRegisterForm.orgRegisterForm.fill(newOrg)
            .withError("orgUserName", messagesApi("cjww.auth.register.generic.userName.inUse"))
            .withError("orgEmail", messagesApi("cjww.auth.register.generic.email.inUse"))
        ))
        case Registration.userNameInUse => BadRequest(OrgRegisterView(
          OrgRegisterForm.orgRegisterForm.fill(newOrg).withError("orgUserName", messagesApi("cjww.auth.register.generic.userName.inUse"))
        ))
        case Registration.emailInUse    => BadRequest(OrgRegisterView(
          OrgRegisterForm.orgRegisterForm.fill(newOrg).withError("orgEmail", messagesApi("cjww.auth.register.generic.email.inUse"))
        ))
        case Registration.failed        => InternalServerError(StandardErrorView(messagesApi("cjww.auth.error.generic")))
      }
    )
  }
}
