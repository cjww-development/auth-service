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
import common.{ApplicationConfiguration, FrontendController}
import javax.inject.Inject
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import views.html.misc.ServiceUnavailableView
import views.html.redirect.ServiceSelector

import scala.concurrent.Future

class RedirectControllerImpl @Inject()(val authConnector: AuthConnector,
                                       implicit val messagesApi: MessagesApi) extends RedirectController

trait RedirectController extends FrontendController with ApplicationConfiguration {
  def chooseService : Action[AnyContent] = isAuthorised { implicit user => implicit request =>
    Future.successful(Ok(ServiceSelector()))
  }

  def redirectToDeversity: Action[AnyContent] = Action.async {
    implicit request =>
      Future.successful(Redirect(deversityFrontend))
  }

  def redirectToDiagnostics: Action[AnyContent] = Action.async {
    implicit request =>
      Future.successful(Redirect(diagnosticsFrontend))
  }

  def redirectToHub: Action[AnyContent] = Action.async {
    implicit request =>
      Future.successful(Redirect(hubFrontend))
  }

  def redirectToServiceOutage: Action[AnyContent] = Action.async {
    implicit request =>
      Future.successful(ServiceUnavailable(ServiceUnavailableView()))
  }
}
