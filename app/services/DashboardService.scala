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

package services

import javax.inject.{Inject, Singleton}

import com.cjwwdev.auth.models.AuthContext
import connectors.{AccountsMicroserviceConnector, DeversityMicroserviceConnector}
import models.accounts.{BasicDetails, DeversityEnrolment, Settings}
import models.deversity.{OrgDetails, TeacherDetails}
import models.feed.FeedItem
import play.api.Logger
import play.api.mvc.Request

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class DashboardService @Inject()(accountConnector: AccountsMicroserviceConnector,
                                 deversityConnector: DeversityMicroserviceConnector,
                                 feedService: FeedService) {

  def getBasicDetails(implicit authContext: AuthContext, request: Request[_]): Future[BasicDetails] = {
    accountConnector.getBasicDetails
  }

  def getSettings(implicit authContext: AuthContext, request: Request[_]): Future[Settings] = {
    accountConnector.getSettings
  }

  def getFeed(implicit authContext: AuthContext, request: Request[_]): Future[Option[List[FeedItem]]] = {
    feedService.processRetrievedList
  }

  def concatTeacherName(teacher: Option[TeacherDetails]): Option[String] = {
    teacher match {
      case Some(info) => Some(s"${info.title}. ${info.lastName}")
      case _          => None
    }
  }

  def getDeversityEnrolment(implicit authContext: AuthContext, request: Request[_]): Future[Option[DeversityEnrolment]] = {
    for {
      enrolment <- deversityConnector.getDeversityEnrolment
      school    <- enrolment match {
        case Some(enr) => deversityConnector.getSchoolInfo(enr.schoolName)
        case None      => Future.successful(None)
      }
      teacher   <- enrolment match {
        case Some(enr) => enr.role match {
          case "student" => deversityConnector.getTeacherInfo(enr.teacher.get, enr.schoolName)
          case _         => Future.successful(None)
        }
        case None => Future.successful(None)
      }
    } yield {
      enrolment match {
        case Some(enr) => Some(
          enr.copy(schoolName = school.get.orgName, teacher = concatTeacherName(teacher))
        )
        case None => None
      }
    }
  }

//  def getDeversityEnrolmentForConfirmation(userId: String)(implicit authContext: AuthContext, request: Request[_]): Future[DeversityEnrolment] = {
//
//  }

  def getOrgBasicDetails(implicit authContext: AuthContext, request: Request[_]): Future[Option[OrgDetails]] = {
    accountConnector.getOrgBasicDetails
  }

  def getTeacherList(implicit authContext: AuthContext, request: Request[_]): Future[List[TeacherDetails]] = {
    accountConnector.getTeacherList
  }

  def getPendingEnrolmentCount(implicit authContext: AuthContext, request: Request[_]): Future[Int] = {
    deversityConnector.getPendingEnrolmentCount map(_.\("pendingCount").as[Int])
  }
}
