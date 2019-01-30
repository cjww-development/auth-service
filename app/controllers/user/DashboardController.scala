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

package controllers.user

import com.cjwwdev.auth.connectors.AuthConnector
import com.cjwwdev.auth.models.CurrentUser
import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.featuremanagement.services.FeatureService
import common.helpers.AuthController
import common.{FeatureManagement, RedirectUrls}
import javax.inject.Inject
import models.accounts.{BasicDetails, DeversityEnrolment, Settings}
import models.deversity.{OrgDetails, TeacherDetails}
import models.feed.FeedItem
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request}
import services.DashboardService
import views.html.user.{Dashboard, OrgDashboard}

import scala.concurrent.{ExecutionContext, Future}

class DefaultDashboardController @Inject()(val dashboardService: DashboardService,
                                           val controllerComponents: ControllerComponents,
                                           val authConnector: AuthConnector,
                                           val config: ConfigurationLoader,
                                           val featureService: FeatureService,
                                           implicit val ec: ExecutionContext) extends DashboardController with RedirectUrls

trait DashboardController extends AuthController with FeatureManagement {
  val dashboardService: DashboardService

  private def buildOrgDashboard(implicit req: Request[_], currentUser: CurrentUser): Future[(OrgDetails, List[TeacherDetails])]  = {
    for {
      Some(details) <- dashboardService.getOrgBasicDetails
      teacherList   <- dashboardService.getTeacherList
    } yield (details, teacherList)
  }

  private def buildUserDashboard(implicit req: Request[_],
                                 currentUser: CurrentUser): Future[(BasicDetails, Settings, List[FeedItem], Option[DeversityEnrolment])] = {
    for {
      Some(details) <- dashboardService.getBasicDetails
      settings      <- dashboardService.getSettings
      feed          <- dashboardService.getFeed
      enrolment     <- dashboardService.getDeversityEnrolment
    } yield (details, settings, feed, enrolment)
  }

  def show : Action[AnyContent] = isAuthorised { implicit req => implicit user =>
    user.credentialType match {
      case "organisation" => buildOrgDashboard map { case (details, teacherList) =>
        Ok(OrgDashboard(details, teacherList, deversityEnabled))
      }
      case "individual" => buildUserDashboard map { case (details, settings, feed, enrolment) =>
        Ok(Dashboard(feed, details, settings, deversityEnabled, enrolment))
      }
    }
  }
}
