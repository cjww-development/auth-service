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
package connectors

import com.cjwwdev.auth.models.CurrentUser
import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.http.exceptions._
import com.cjwwdev.http.responses.WsResponseHelpers
import com.cjwwdev.http.verbs.Http
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.implicits.ImplicitJsValues._
import common.{MissingBasicDetailsException, _}
import enums.{HttpResponse, Registration}
import javax.inject.Inject
import models.accounts._
import models.deversity.{OrgDetails, TeacherDetails}
import models.feed.FeedItem
import models.registration.{OrgRegistration, UserRegistration}
import play.api.http.Status._
import play.api.libs.json.{JsObject, OFormat}
import play.api.mvc.Request

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AccountsMicroserviceConnectorImpl @Inject()(val http: Http,
                                                  val configurationLoader: ConfigurationLoader) extends AccountsMicroserviceConnector

trait AccountsMicroserviceConnector extends ApplicationConfiguration with WsResponseHelpers {
  val http: Http

  def updateProfile(userProfile: UserProfile)(implicit user: CurrentUser, request: Request[_]) : Future[HttpResponse.Value] = {
    http.patch[UserProfile](s"$accountsMicroservice/account/${user.id}/update-profile", userProfile) map {
      _.status match {
        case OK => HttpResponse.success
      }
    } recover {
      case _: ServerErrorException => HttpResponse.failed
    }
  }

  def updatePassword(passwordSet: PasswordSet)
                    (implicit user: CurrentUser, format: OFormat[PasswordSet], request: Request[_]): Future[UpdatedPasswordResponse] = {
    http.patch[PasswordSet](s"$accountsMicroservice/account/${user.id}/update-password", passwordSet) map {
      _.status match {
        case OK => PasswordUpdated
      }
    } recover {
      case _: ConflictException => InvalidOldPassword
    }
  }

  def updateSettings(settings: Settings)(implicit user: CurrentUser, format: OFormat[Settings], request: Request[_]): Future[HttpResponse.Value] = {
    http.patch[Settings](s"$accountsMicroservice/account/${user.id}/update-settings", settings) map {
      _.status match {
        case OK => HttpResponse.success
      }
    } recover {
      case _: ServerErrorException => HttpResponse.failed
    }
  }

  def createFeedItem(feedItem: FeedItem)(implicit user: CurrentUser, request: Request[_]) : Future[HttpResponse.Value] = {
    http.post[FeedItem](s"$accountsMicroservice/create-feed-item", feedItem) map {
      _.status match {
        case OK => HttpResponse.success
      }
    }
  }

  def getFeedItems(implicit user: CurrentUser, request: Request[_]) : Future[Option[List[FeedItem]]] = {
    http.get(s"$accountsMicroservice/account/${user.id}/get-user-feed") map { resp =>
      Some(resp.toDataType[JsObject](needsDecrypt = true).get[List[FeedItem]]("feed-array"))
    } recover {
      case _: NotFoundException => None
    }
  }

  def getBasicDetails(implicit user: CurrentUser, request: Request[_]) : Future[BasicDetails] = {
    http.get(s"$accountsMicroservice/account/${user.id}/basic-details") map {
      _.toDataType[BasicDetails](needsDecrypt = true)
    } recover {
      case _: ClientErrorException =>
        throw new MissingBasicDetailsException(s"No basic details were found for user ${user.id}")
    }
  }

  def getEnrolments(implicit user: CurrentUser, request: Request[_]) : Future[Option[Enrolments]] = {
    http.get(s"$accountsMicroservice/account/${user.id}/enrolments") map { resp =>
      Some(resp.toDataType[Enrolments](needsDecrypt = true))
    } recover {
      case _: NotFoundException => None
    }
  }

  def getSettings(implicit user: CurrentUser, request: Request[_]) : Future[Settings] = {
    http.get(s"$accountsMicroservice/account/${user.id}/settings") map {
      _.toDataType[Settings](needsDecrypt = true)
    } recover {
      case _: NotFoundException => Settings.default
    }
  }

  def getOrgBasicDetails(implicit user: CurrentUser, request: Request[_]): Future[Option[OrgDetails]] = {
    http.get(s"$accountsMicroservice/account/${user.id}/org-basic-details") map { resp =>
      Some(resp.toDataType[OrgDetails](needsDecrypt = true))
    } recover {
      case _: NotFoundException => None
    }
  }

  def getTeacherList(implicit user: CurrentUser, request: Request[_]): Future[List[TeacherDetails]] = {
    http.get(s"$accountsMicroservice/account/${user.id}/teachers") map {
      _.toDataType[List[TeacherDetails]](needsDecrypt = true)
    }
  }

  def createNewIndividualUser(userDetails : UserRegistration)(implicit request: Request[_], format: OFormat[UserRegistration]) : Future[Registration.Value] = {
    http.post[UserRegistration](s"$accountsMicroservice/account/create-new-user", userDetails) map {
      _ => Registration.success
    } recover {
      case _ => Registration.failed
    }
  }

  def createNewOrgUser(orgUserDetails: OrgRegistration)(implicit request: Request[_], format: OFormat[OrgRegistration]): Future[Registration.Value] = {
    http.post[OrgRegistration](s"$accountsMicroservice/account/create-new-org-user", orgUserDetails) map {
      _ => Registration.success
    } recover {
      case _ => Registration.failed
    }
  }

  def checkUserName(username : String)(implicit request: Request[_]) : Future[Boolean] = {
    http.head(s"$accountsMicroservice/validate/user-name/${username.encrypt}") map {
      _.status match {
        case OK => false
      }
    } recover {
      case _: ConflictException => true
    }
  }

  def checkEmailAddress(email : String)(implicit request: Request[_]) : Future[Boolean] = {
    http.head(s"$accountsMicroservice/validate/email/${email.encrypt}") map {
      _.status match {
        case OK => false
      }
    } recover {
      case _: ConflictException => true
    }
  }
}
