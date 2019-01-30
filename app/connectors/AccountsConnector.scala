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
package connectors

import com.cjwwdev.auth.models.CurrentUser
import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.http.responses.EvaluateResponse._
import com.cjwwdev.http.responses.WsResponseHelpers
import com.cjwwdev.http.verbs.Http
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.implicits.ImplicitJsValues._
import com.cjwwdev.security.deobfuscation.DeObfuscation._
import com.cjwwdev.security.obfuscation.Obfuscation._
import common.responses.{InvalidOldPassword, PasswordUpdated, UpdatedPasswordResponse}
import enums.{HttpResponse, Registration}
import javax.inject.Inject
import models.accounts._
import models.deversity.{OrgDetails, TeacherDetails}
import models.feed.FeedItem
import models.registration.{OrgRegistration, UserRegistration}
import play.api.libs.json.{JsObject, OFormat}
import play.api.mvc.Request

import scala.concurrent.{Future, ExecutionContext => ExC}

class DefaultAccountsConnector @Inject()(val http: Http,
                                         val config: ConfigurationLoader) extends AccountsConnector {
  override val accounts: String = config.getServiceUrl("accounts-microservice")
}

trait AccountsConnector extends WsResponseHelpers {
  val http: Http

  val accounts: String

  def updateProfile(userProfile: UserProfile)(implicit user: CurrentUser, req: Request[_], ec: ExC) : Future[HttpResponse.Value] = {
    http.patch[UserProfile](s"$accounts/account/${user.id}/update-profile", userProfile) map {
      case SuccessResponse(_) => HttpResponse.success
      case ErrorResponse(_)   => HttpResponse.failed
    }
  }

  def updatePassword(passwordSet: PasswordSet)
                    (implicit user: CurrentUser, format: OFormat[PasswordSet], req: Request[_], ec: ExC): Future[UpdatedPasswordResponse] = {
    http.patch[PasswordSet](s"$accounts/account/${user.id}/update-password", passwordSet) map {
      case SuccessResponse(_) => PasswordUpdated
      case ErrorResponse(_)   => InvalidOldPassword
    }
  }

  def updateSettings(settings: Settings)(implicit user: CurrentUser, format: OFormat[Settings], req: Request[_], ec: ExC): Future[HttpResponse.Value] = {
    http.patch[Settings](s"$accounts/account/${user.id}/update-settings", settings) map {
      case SuccessResponse(_) => HttpResponse.success
      case ErrorResponse(_)   => HttpResponse.failed
    }
  }

  def createFeedItem(feedItem: FeedItem)(implicit user: CurrentUser, req: Request[_], ec: ExC) : Future[HttpResponse.Value] = {
    http.post[FeedItem](s"$accounts/create-feed-item", feedItem) map {
      case SuccessResponse(_) => HttpResponse.success
      case ErrorResponse(_)   => HttpResponse.failed
    }
  }

  def getFeedItems(implicit user: CurrentUser, req: Request[_], ec: ExC) : Future[List[FeedItem]] = {
    http.get(s"$accounts/account/${user.id}/get-user-feed") map {
      case SuccessResponse(resp) => resp.toDataType[JsObject](needsDecrypt = true).fold(
        _.get[List[FeedItem]]("feed-array"),
        _ => List.empty[FeedItem]
      )
      case ErrorResponse(_)      => List.empty[FeedItem]
    }
  }

  def getBasicDetails(implicit user: CurrentUser, req: Request[_], ec: ExC) : Future[Option[BasicDetails]] = {
    http.get(s"$accounts/account/${user.id}/basic-details") map {
      case SuccessResponse(resp) => resp.toDataType[BasicDetails](needsDecrypt = true).fold(Some(_), _ => None)
      case ErrorResponse(_)      => None
    }
  }

  def getEnrolments(implicit user: CurrentUser, req: Request[_], ec: ExC) : Future[Option[Enrolments]] = {
    http.get(s"$accounts/account/${user.id}/enrolments") map {
      case SuccessResponse(resp) => resp.toDataType[Enrolments](needsDecrypt = true).fold(Some(_), _ => None)
      case ErrorResponse(_)      => None
    }
  }

  def getSettings(implicit user: CurrentUser, req: Request[_], ec: ExC) : Future[Settings] = {
    http.get(s"$accounts/account/${user.id}/settings") map {
      case SuccessResponse(resp) => resp.toDataType[Settings](needsDecrypt = true).fold(identity, _ => Settings.default)
      case ErrorResponse(_)      => Settings.default
    }
  }

  def getOrgBasicDetails(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[Option[OrgDetails]] = {
    http.get(s"$accounts/account/${user.id}/org-basic-details") map {
      case SuccessResponse(resp) => resp.toDataType[OrgDetails](needsDecrypt = true).fold(Some(_), _ => None)
      case ErrorResponse(_)      => None
    }
  }

  def getTeacherList(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[List[TeacherDetails]] = {
    http.get(s"$accounts/account/${user.id}/teachers") map {
      case SuccessResponse(resp) => resp.toDataType[List[TeacherDetails]](needsDecrypt = true).fold(identity, _ => List.empty[TeacherDetails])
      case ErrorResponse(_)      => List.empty
    }
  }

  def createNewIndividualUser(userDetails : UserRegistration)
                             (implicit req: Request[_], ec: ExC, format: OFormat[UserRegistration]) : Future[Registration.Value] = {
    http.post[UserRegistration](s"$accounts/account/create-new-user", userDetails) map {
      case SuccessResponse(_) => Registration.success
      case ErrorResponse(_)   => Registration.failed
    }
  }

  def createNewOrgUser(orgUserDetails: OrgRegistration)(implicit req: Request[_], ec: ExC, format: OFormat[OrgRegistration]): Future[Registration.Value] = {
    http.post[OrgRegistration](s"$accounts/account/create-new-org-user", orgUserDetails) map {
      case SuccessResponse(_) => Registration.success
      case ErrorResponse(_)   => Registration.failed
    }
  }

  def checkUserName(username : String)(implicit req: Request[_], ec: ExC) : Future[Boolean] = {
    http.head(s"$accounts/validate/user-name/${username.encrypt}") map {
      case SuccessResponse(_) => false
      case ErrorResponse(_)   => true
    }
  }

  def checkEmailAddress(email : String)(implicit req: Request[_], ec: ExC) : Future[Boolean] = {
    http.head(s"$accounts/validate/email/${email.encrypt}") map {
      case SuccessResponse(_) => false
      case ErrorResponse(_)   => true
    }
  }
}
