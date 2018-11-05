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
import common.helpers.AuthController
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.DashboardService
import views.html.user.{Dashboard, OrgDashboard}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DefaultDashboardController @Inject()(val dashboardService: DashboardService,
                                           val controllerComponents: ControllerComponents,
                                           val authConnector: AuthConnector) extends DashboardController

trait DashboardController extends AuthController {
  val dashboardService: DashboardService

  def show : Action[AnyContent] = isAuthorised {
    implicit request =>
      implicit user =>
        user.credentialType match {
          case "organisation" => for {
            Some(basicDetails)  <- dashboardService.getOrgBasicDetails
            teacherList         <- dashboardService.getTeacherList
          } yield Ok(OrgDashboard(basicDetails, teacherList, deversityEnabled))
          case "individual" => for {
            basicDetails        <- dashboardService.getBasicDetails
            settings            <- dashboardService.getSettings
            feed                <- dashboardService.getFeed
            deversityEnrolment  <- if(deversityEnabled) {
              dashboardService.getDeversityEnrolment
            } else {
              Future(None)
            }
          } yield Ok(Dashboard(feed, basicDetails, settings, deversityEnabled, deversityEnrolment))
        }
  }
}
