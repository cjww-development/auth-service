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

package services

import com.cjwwdev.auth.models.CurrentUser
import com.cjwwdev.featuremanagement.services.FeatureService
import common.responses.{FeatureStates, MissingOrgDetailsException}
import connectors.{AccountsConnector, DeversityConnector}
import javax.inject.Inject
import models.accounts.{BasicDetails, DeversityEnrolment, Settings}
import models.deversity.{OrgDetails, TeacherDetails}
import models.feed.FeedItem
import play.api.mvc.Request

import scala.concurrent.{Future, ExecutionContext => ExC}

class DefaultDashboardService @Inject()(val accountConnector: AccountsConnector,
                                        val deversityConnector: DeversityConnector,
                                        val featureService: FeatureService,
                                        val feedService: FeedService) extends DashboardService

trait DashboardService extends FeatureStates {
  val accountConnector: AccountsConnector
  val deversityConnector: DeversityConnector
  val feedService: FeedService

  def getBasicDetails(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[Option[BasicDetails]] = {
    accountConnector.getBasicDetails
  }

  def getSettings(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[Settings] = {
    accountConnector.getSettings
  }

  def getFeed(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[List[FeedItem]] = {
    feedService.processRetrievedList
  }

  def concatTeacherName(teacher: Option[TeacherDetails]): Option[String] = teacher.map {
    info => s"${info.title}. ${info.lastName}"
  }

  def getSchoolInfo(schoolName: String)(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[OrgDetails] = {
    deversityConnector.getSchoolInfo(schoolName) map {
      _.getOrElse(throw new MissingOrgDetailsException(s"Couldn't find org details for org name $schoolName"))
    }
  }

  private def getEnrolment(implicit req: Request[_], currentUser: CurrentUser, ec: ExC): Future[Option[DeversityEnrolment]] = {
    if(deversityEnabled) deversityConnector.getDeversityEnrolment else Future.successful(None)
  }

  def getDeversityEnrolment(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[Option[DeversityEnrolment]] = {
    for {
      enrolment <- getEnrolment
      school    <- enrolment match {
        case Some(enr) => deversityConnector.getSchoolInfo(enr.schoolDevId)
        case None      => Future.successful(None)
      }
      teacher   <- enrolment match {
        case Some(enr) => enr.role match {
          case "student" => deversityConnector.getTeacherInfo(enr.teacher.get, enr.schoolDevId)
          case _         => Future.successful(None)
        }
        case None => Future.successful(None)
      }
    } yield enrolment match {
      case Some(enr) => Some(enr.copy(
        schoolDevId = school.get.orgName,
        teacher     = concatTeacherName(teacher)
      ))
      case None => None
    }
  }

  def getOrgBasicDetails(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[Option[OrgDetails]] = {
    accountConnector.getOrgBasicDetails
  }

  def getTeacherList(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[List[TeacherDetails]] = {
    accountConnector.getTeacherList
  }
}
