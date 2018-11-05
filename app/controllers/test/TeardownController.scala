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
package controllers.test

import com.cjwwdev.auth.connectors.AuthConnector
import common.helpers.AuthController
import connectors.test.TeardownConnector
import forms.test.TearDownUserForm
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import views.html.test.{ActionCompleteView, TearDownUserView}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DefaultTeardownController @Inject()(val authConnector: AuthConnector,
                                          val controllerComponents: ControllerComponents,
                                          val tearDownConnector: TeardownConnector) extends TeardownController

trait TeardownController extends AuthController {
  val tearDownConnector: TeardownConnector

  def tearDownTestUserShow: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(TearDownUserView(TearDownUserForm.form)))
  }

  def tearDownTestUserSubmit: Action[AnyContent] = Action.async { implicit request =>
    TearDownUserForm.form.bindFromRequest.fold(
      errors => Future.successful(BadRequest(TearDownUserView(errors))),
      valid  => tearDownConnector.deleteTestAccountInstance(valid.testUserName, valid.credentialType) map {
        _ => Ok(ActionCompleteView())
      }
    )
  }
}
