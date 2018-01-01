// Copyright (C) 2016-2017 the original author or authors.
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
package controllers.user

import javax.inject.Inject

import com.cjwwdev.auth.actions.Actions
import com.cjwwdev.auth.connectors.AuthConnector
import common.FrontendController
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import services.{DashboardService, RegistrationCodeService}
import views.html.user.GenerateCodeView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GenerateCodeControllerImpl @Inject()(val authConnector: AuthConnector,
                                           val registrationCodeService: RegistrationCodeService,
                                           val dashboardService: DashboardService,
                                           implicit val messagesApi: MessagesApi) extends GenerateCodeController

trait GenerateCodeController extends FrontendController with Actions {
  val registrationCodeService: RegistrationCodeService
  val dashboardService: DashboardService

  def getRegistrationCodeShow: Action[AnyContent] = authorisedFor(LOGIN_CALLBACK).async {
    implicit user =>
      implicit request =>
        user.user.credentialType match {
          case INDIVIDUAL   => user.user.role match {
            case Some("teacher") => registrationCodeService.getGeneratedCode map(regCode => Ok(GenerateCodeView(regCode)))
            case _               => Future.successful(NotFound)
          }
          case ORGANISATION => registrationCodeService.getGeneratedCode map(regCode => Ok(GenerateCodeView(regCode)))
          case _            => Future.successful(NotFound)
        }
  }

  def generateRegistrationCode: Action[AnyContent] = authorisedFor(LOGIN_CALLBACK).async {
    implicit user =>
      implicit requeest =>
        user.user.credentialType match {
          case INDIVIDUAL   => user.user.role match {
            case Some("teacher") => registrationCodeService.generateRegistrationCode map(_ => Redirect(routes.GenerateCodeController.getRegistrationCodeShow()))
            case _               => Future.successful(NotFound)
          }
          case ORGANISATION => registrationCodeService.generateRegistrationCode map(_ => Redirect(routes.GenerateCodeController.getRegistrationCodeShow()))
          case _            => Future.successful(NotFound)
        }
  }
}