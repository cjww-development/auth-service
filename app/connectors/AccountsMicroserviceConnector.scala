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

import javax.inject.{Inject, Singleton}

import com.cjwwdev.auth.models.AuthContext
import com.cjwwdev.http.exceptions.{ClientErrorException, ConflictException, HttpDecryptionException, NotFoundException, ServerErrorException}
import com.cjwwdev.http.verbs.Http
import com.cjwwdev.security.encryption.DataSecurity
import config._
import config.MissingBasicDetailsException
import enums.{HttpResponse, Registration}
import models.accounts._
import models.deversity.{OrgDetails, TeacherDetails}
import models.feed.FeedItem
import models.registration.{OrgRegistration, UserRegistration}
import play.api.http.Status._
import play.api.libs.json.{JsArray, JsObject, JsValue}
import play.api.mvc.Request
import play.api.Logger

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AccountsMicroserviceConnector @Inject()(http: Http) extends ApplicationConfiguration {
  def updateProfile(userProfile: UserProfile)(implicit auth: AuthContext, request: Request[_]) : Future[HttpResponse.Value] = {
    http.PATCH[UserProfile](s"$accountsMicroservice/account/${auth.user.userId}/update-profile", userProfile) map {
      _.status match {
        case OK => HttpResponse.success
      }
    } recover {
      case _: ServerErrorException => HttpResponse.failed
    }
  }

  def updatePassword(passwordSet: PasswordSet)(implicit auth: AuthContext, request: Request[_]): Future[UpdatedPasswordResponse] = {
    http.PATCH[PasswordSet](s"$accountsMicroservice/account/${auth.user.userId}/update-password", passwordSet) map {
      _.status match {
        case OK => PasswordUpdated
      }
    } recover {
      case _: ConflictException => InvalidOldPassword
    }
  }

  def updateSettings(settings: Settings)(implicit auth: AuthContext, request: Request[_]): Future[HttpResponse.Value] = {
    http.PATCH[Settings](s"$accountsMicroservice/account/${auth.user.userId}/update-settings", settings) map { resp =>
      resp.status match {
        case OK => HttpResponse.success
      }
    } recover {
      case _: ServerErrorException => HttpResponse.failed
    }
  }

  def createFeedItem(feedItem: FeedItem)(implicit auth: AuthContext, request: Request[_]) : Future[HttpResponse.Value] = {
    http.POST[FeedItem](s"$accountsMicroservice/create-feed-item", feedItem) map { resp =>
      resp.status match {
        case OK => HttpResponse.success
      }
    }
  }

  def getFeedItems(implicit auth: AuthContext, request: Request[_]) : Future[Option[List[FeedItem]]] = {
    http.GET[JsObject](s"$accountsMicroservice/account/${auth.user.userId}/get-user-feed") map {
      jsObj => Some(jsObj.value("feed-array").as[JsArray].as[List[FeedItem]])
    } recover {
      case _: HttpDecryptionException => None
      case _: NotFoundException       => None
    }
  }

  def getBasicDetails(implicit auth: AuthContext, request: Request[_]) : Future[BasicDetails] = {
    http.GET[BasicDetails](s"$accountsMicroservice${auth.basicDetailsUri}") recover {
      case _: ClientErrorException => throw new MissingBasicDetailsException(s"No basic details were found for user ${auth.user.userId}")
    }
  }

  def getEnrolments(implicit authContext: AuthContext, request: Request[_]) : Future[Option[Enrolments]] = {
    http.GET[Enrolments](s"$accountsMicroservice${authContext.enrolmentsUri}") map {
      enrolments => Some(enrolments)
    } recover {
      case _: NotFoundException => None
    }
  }

  def getSettings(implicit authContext: AuthContext, request: Request[_]) : Future[Settings] = {
    http.GET[Settings](s"$accountsMicroservice${authContext.settingsUri}") recover {
      case _: NotFoundException => Settings.default
    }
  }

  def getDeversityEnrolment(implicit authContext: AuthContext, request: Request[_]): Future[Option[DeversityEnrolment]] = {
    http.GET[DeversityEnrolment](s"$accountsMicroservice/account/${authContext.user.userId}/deversity-info") map { devEnr =>
      Some(devEnr)
    } recover {
      case _: NotFoundException => None
    }
  }

  def getSchoolInfo(school: String)(implicit authContext: AuthContext, request: Request[_]): Future[Option[OrgDetails]] = {
    val schoolEnc = DataSecurity.encryptString(school)
    http.GET[OrgDetails](s"$accountsMicroservice/school/$schoolEnc/details") map {
      orgDeets => Some(orgDeets)
    } recover {
      case _: NotFoundException => None
    }
  }

  def getTeacherInfo(teacher: String, school: String)(implicit authContext: AuthContext, request: Request[_]): Future[Option[TeacherDetails]] = {
    val teacherEnc  = DataSecurity.encryptString(teacher)
    val schoolEnc   = DataSecurity.encryptString(school)
    http.GET[TeacherDetails](s"$accountsMicroservice/teacher/$teacherEnc/school/$schoolEnc/details") map {
      teacherDeets => Some(teacherDeets)
    } recover {
      case _: NotFoundException => None
    }
  }

  def getOrgBasicDetails(implicit authContext: AuthContext, request: Request[_]): Future[Option[OrgDetails]] = {
    http.GET[OrgDetails](s"$accountsMicroservice/account/${authContext.user.userId}/org-basic-details") map {
      orgDeets => Some(orgDeets)
    } recover {
      case _: NotFoundException => None
    }
  }

  def getTeacherList(implicit authContext: AuthContext, request: Request[_]): Future[List[TeacherDetails]] = {
    http.GET[List[TeacherDetails]](s"$accountsMicroservice/account/${authContext.user.userId}/teachers")
  }

  def createNewIndividualUser(userDetails : UserRegistration)(implicit request: Request[_]) : Future[Registration.Value] = {
    http.POST[UserRegistration](s"$accountsMicroservice/account/create-new-user", userDetails) map {
      _ => Registration.success
    } recover {
      case _ => Registration.failed
    }
  }

  def createNewOrgUser(orgUserDetails: OrgRegistration)(implicit request: Request[_]): Future[Registration.Value] = {
    http.POST[OrgRegistration](s"$accountsMicroservice/account/create-new-org-user", orgUserDetails) map {
      _ => Registration.success
    } recover {
      case _ => Registration.failed
    }
  }

  def checkUserName(username : String)(implicit request: Request[_]) : Future[Boolean] = {
    val encUsername = DataSecurity.encryptString(username)
    http.HEAD(s"$accountsMicroservice/validate/user-name/$encUsername") map {
      _.status match {
        case OK => false
      }
    } recover {
      case _: ConflictException => true
    }
  }

  def checkEmailAddress(email : String)(implicit request: Request[_]) : Future[Boolean] = {
    val encEmailAddress = DataSecurity.encryptString(email)
    http.HEAD(s"$accountsMicroservice/validate/email/$encEmailAddress") map {
      _.status match {
        case OK => false
      }
    } recover {
      case _: ConflictException => true
    }
  }

  def getPendingEnrolmentCount(implicit authContext: AuthContext, request: Request[_]): Future[JsValue] = {
    http.GET[JsValue](s"$accountsMicroservice/account/${authContext.user.userId}/pending-deversity-enrolments")
  }
}
