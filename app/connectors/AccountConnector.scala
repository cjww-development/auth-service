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
package connectors

import com.cjwwdev.auth.models.AuthContext
import com.google.inject.{Inject, Singleton}
import config.ApplicationConfiguration
import models.accounts._
import com.cjwwdev.logging.Logger
import play.api.http.Status._
import play.api.libs.json.{JsArray, JsObject}
import com.cjwwdev.http.verbs.Http
import com.cjwwdev.security.encryption.DataSecurity
import models.deversity.{OrgDetails, TeacherDetails}
import play.api.mvc.Request

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

sealed trait UpdatedPasswordResponse
case object InvalidOldPassword extends UpdatedPasswordResponse
case object PasswordUpdated extends UpdatedPasswordResponse

sealed trait UpdatedSettingsResponse
case object UpdatedSettingsSuccess extends UpdatedSettingsResponse
case object UpdatedSettingsFailed extends UpdatedSettingsResponse

sealed trait FeedEventResponse
case object FeedEventSuccessResponse extends FeedEventResponse
case object FeedEventFailedResponse extends FeedEventResponse

@Singleton
class AccountConnector @Inject()(http : Http) extends ApplicationConfiguration {

  def updateProfile(userProfile: UserProfile)(implicit auth: AuthContext, request: Request[_]) : Future[Int] = {
    http.PATCH[UserProfile](s"$accountsMicroservice/account/${auth.user.userId}/update-profile", userProfile) map { resp =>
      resp.status match {
        case OK => resp.status
        case _ =>
          Logger.info(s"[AccountConnector] - [updateProfile] Update profile encountered a problem")
          resp.status
      }
    }
  }

  def updatePassword(passwordSet: PasswordSet)(implicit auth: AuthContext, request: Request[_]): Future[UpdatedPasswordResponse] = {
    http.PATCH[PasswordSet](s"$accountsMicroservice/account/${auth.user.userId}/update-password", passwordSet) map {
      _.status match {
        case CONFLICT => InvalidOldPassword
        case OK => PasswordUpdated
      }
    }
  }

  def updateSettings(settings: AccountSettings)(implicit auth: AuthContext, request: Request[_]): Future[UpdatedSettingsResponse] = {
    http.PATCH[AccountSettings](s"$accountsMicroservice/account/${auth.user.userId}/update-settings", settings) map {
      _.status match {
        case OK => UpdatedSettingsSuccess
        case INTERNAL_SERVER_ERROR => UpdatedSettingsFailed
      }
    }
  }

  def createFeedItem(feedItem: FeedItem)(implicit request: Request[_]) : Future[FeedEventResponse] = {
    http.POST[FeedItem](s"$accountsMicroservice/create-feed-item", feedItem) map {
      resp => resp.status match {
        case OK => FeedEventSuccessResponse
        case INTERNAL_SERVER_ERROR => FeedEventFailedResponse
      }
    }
  }

  def getFeedItems(implicit auth: AuthContext, request: Request[_]) : Future[Option[List[FeedItem]]] = {
    http.GET(s"$accountsMicroservice/account/${auth.user.userId}/get-user-feed") map { resp =>
      resp.status match {
        case OK => DataSecurity.decryptInto[JsObject](resp.body) match {
          case Some(json) => Some(json.value("feed-array").as[JsArray].as[List[FeedItem]])
          case _ => None
        }
        case NOT_FOUND => None
      }
    }
  }

  def getBasicDetails(implicit authContext: AuthContext, request: Request[_]) : Future[Option[BasicDetails]] = {
    http.GET(s"$accountsMicroservice${authContext.basicDetailsUri}") map { resp =>
      resp.status match {
        case OK => DataSecurity.decryptInto[BasicDetails](resp.body)
        case _  => None
      }
    }
  }

  def getEnrolments(implicit authContext: AuthContext, request: Request[_]) : Future[Option[Enrolments]] = {
    http.GET(s"$accountsMicroservice${authContext.enrolmentsUri}") map { resp =>
      resp.status match {
        case OK => DataSecurity.decryptInto[Enrolments](resp.body)
        case _  => None
      }
    }
  }

  def getSettings(implicit authContext: AuthContext, request: Request[_]) : Future[Option[Settings]] = {
    http.GET(s"$accountsMicroservice${authContext.settingsUri}") map { resp =>
      resp.status match {
        case OK => DataSecurity.decryptInto[Settings](resp.body)
        case _  => None
      }
    }
  }

  def getDeversityEnrolment(implicit authContext: AuthContext, request: Request[_]): Future[Option[DeversityEnrolment]] = {
    http.GET(s"$accountsMicroservice/account/${authContext.user.userId}/deversity-info") map { resp =>
      resp.status match {
        case OK => DataSecurity.decryptInto[DeversityEnrolment](resp.body)
        case _  => None
      }
    }
  }

  def getSchoolInfo(school: String)(implicit authContext: AuthContext, request: Request[_]): Future[Option[OrgDetails]] = {
    val schoolEnc = DataSecurity.encryptData(school).get
    http.GET(s"$accountsMicroservice/school/$schoolEnc/details") map { resp =>
      DataSecurity.decryptInto[OrgDetails](resp.body)
    }
  }

  def getTeacherInfo(teacher: String, school: String)(implicit authContext: AuthContext, request: Request[_]): Future[Option[TeacherDetails]] = {
    val teacherEnc  = DataSecurity.encryptData(teacher).get
    val schoolEnc   = DataSecurity.encryptData(school).get
    http.GET(s"$accountsMicroservice/teacher/$teacherEnc/school/$schoolEnc/details") map { resp =>
      DataSecurity.decryptInto[TeacherDetails](resp.body)
    }
  }

  def getOrgBasicDetails(implicit authContext: AuthContext, request: Request[_]): Future[Option[OrgDetails]] = {
    http.GET(s"$accountsMicroservice/account/${authContext.user.userId}/org-basic-details") map { resp =>
      DataSecurity.decryptInto[OrgDetails](resp.body)
    }
  }

  def getTeacherList(implicit authContext: AuthContext, request: Request[_]): Future[List[TeacherDetails]] = {
    http.GET(s"$accountsMicroservice/account/${authContext.user.userId}/teachers") map { resp =>
      resp.status match {
        case OK => DataSecurity.decryptInto[List[TeacherDetails]](resp.body).get
        case _  => List()
      }
    }
  }
}
