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

package controllers.redirect

import com.cjwwdev.auth.connectors.AuthConnector
import common.helpers.AuthController
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import views.html.misc.ServiceUnavailableView
import views.html.redirect.ServiceSelector

import scala.concurrent.Future

class DefaultRedirectController @Inject()(val authConnector: AuthConnector,
                                          val controllerComponents: ControllerComponents) extends RedirectController

trait RedirectController extends AuthController {
  def chooseService : Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    Future.successful(Ok(ServiceSelector()))
  }

  def redirectToDeversity: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Redirect(deversityFrontend))
  }

  def redirectToDiagnostics: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Redirect(diagnosticsFrontend))
  }

  def redirectToHub: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Redirect(hubFrontend))
  }

  def redirectToServiceOutage: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(ServiceUnavailable(ServiceUnavailableView()))
  }
}
