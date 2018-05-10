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

package services

import javax.inject.Inject

import com.cjwwdev.auth.models.CurrentUser
import common.MissingOrgDetailsException
import connectors.{AccountsMicroserviceConnector, DeversityMicroserviceConnector}
import models.accounts.{BasicDetails, DeversityEnrolment, Settings}
import models.deversity.{OrgDetails, TeacherDetails}
import models.feed.FeedItem
import play.api.mvc.Request

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DashboardServiceImpl @Inject()(val accountConnector: AccountsMicroserviceConnector,
                                     val deversityConnector: DeversityMicroserviceConnector,
                                     val feedService: FeedService) extends DashboardService

trait DashboardService {
  val accountConnector: AccountsMicroserviceConnector
  val deversityConnector: DeversityMicroserviceConnector
  val feedService: FeedService

  def getBasicDetails(implicit user: CurrentUser, request: Request[_]): Future[BasicDetails] = {
    accountConnector.getBasicDetails
  }

  def getSettings(implicit user: CurrentUser, request: Request[_]): Future[Settings] = {
    accountConnector.getSettings
  }

  def getFeed(implicit user: CurrentUser, request: Request[_]): Future[Option[List[FeedItem]]] = {
    feedService.processRetrievedList
  }

  def concatTeacherName(teacher: Option[TeacherDetails]): Option[String] = {
    teacher match {
      case Some(info) => Some(s"${info.title}. ${info.lastName}")
      case _          => None
    }
  }

  def getSchoolInfo(schoolName: String)(implicit user: CurrentUser, request: Request[_]): Future[OrgDetails] = {
    deversityConnector.getSchoolInfo(schoolName) map {
      _.getOrElse(throw new MissingOrgDetailsException(s"Couldn't find org details for org name $schoolName"))
    }
  }

  def getDeversityEnrolment(implicit user: CurrentUser, request: Request[_]): Future[Option[DeversityEnrolment]] = {
    for {
      enrolment <- deversityConnector.getDeversityEnrolment
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
    } yield {
      enrolment match {
        case Some(enr) => Some(enr.copy(
          schoolDevId = school.get.orgName,
          teacher     = concatTeacherName(teacher)
        ))
        case None => None
      }
    }
  }

  def getOrgBasicDetails(implicit user: CurrentUser, request: Request[_]): Future[Option[OrgDetails]] = {
    accountConnector.getOrgBasicDetails
  }

  def getTeacherList(implicit user: CurrentUser, request: Request[_]): Future[List[TeacherDetails]] = {
    accountConnector.getTeacherList
  }
}
