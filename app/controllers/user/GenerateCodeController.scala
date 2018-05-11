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
package controllers.user

import com.cjwwdev.auth.connectors.AuthConnector
import common.FrontendController
import javax.inject.Inject
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import services.{DashboardService, DeversityService, RegistrationCodeService}
import views.html.user.GenerateCodeView
import com.cjwwdev.views.html.templates.errors.{NotFoundView, ServerErrorView, StandardErrorView}
import connectors.DeversityMicroserviceConnector

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GenerateCodeControllerImpl @Inject()(val authConnector: AuthConnector,
                                           val registrationCodeService: RegistrationCodeService,
                                           val dashboardService: DashboardService,
                                           val deversityConnector: DeversityMicroserviceConnector,
                                           implicit val messagesApi: MessagesApi) extends GenerateCodeController

trait GenerateCodeController extends FrontendController {
  val registrationCodeService: RegistrationCodeService
  val dashboardService: DashboardService
  val deversityConnector: DeversityMicroserviceConnector

  def getRegistrationCodeShow: Action[AnyContent] = isAuthorised {
    implicit request =>
      implicit user =>
        user.credentialType match {
          case INDIVIDUAL => for {
            enr <- deversityConnector.getDeversityEnrolment
            res <- enr.fold(Future(NotFound(NotFoundView())))(_ => registrationCodeService.getGeneratedCode map(
              regCode => Ok(GenerateCodeView(regCode))
            ))
          } yield res
          case ORGANISATION => registrationCodeService.getGeneratedCode map {
            regCode => Ok(GenerateCodeView(regCode))
          }
          case _ => Future(NotFound(NotFoundView()))
        }
  }

  def generateRegistrationCode: Action[AnyContent] = isAuthorised {
    implicit request =>
      implicit user =>
        user.credentialType match {
          case INDIVIDUAL => for {
            enr <- deversityConnector.getDeversityEnrolment
            res <- enr.fold(Future(NotFound(NotFoundView())))(_ => registrationCodeService.generateRegistrationCode map(
              _ => Redirect(routes.GenerateCodeController.getRegistrationCodeShow())
            ))
          } yield res
          case ORGANISATION => registrationCodeService.generateRegistrationCode map {
            _ => Redirect(routes.GenerateCodeController.getRegistrationCodeShow())
          }
          case _ => Future(NotFound(NotFoundView()))
        }
  }
}