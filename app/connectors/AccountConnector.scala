// Copyright (C) 2011-2012 the original author or authors.
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

import com.google.inject.{Inject, Singleton}
import config.FrontendConfiguration
import models.accounts._
import models.auth.AuthContext
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.{JsArray, JsObject}
import utils.httpverbs.HttpVerbs
import utils.security.DataSecurity

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
class AccountConnector @Inject()(http : HttpVerbs, config: FrontendConfiguration) {

  def getContext(contextId : String) : Future[Option[AuthContext]] = {
    http.getUrl(s"${config.authMicroservice}/get-context/$contextId") map {
      resp => DataSecurity.decryptInto[AuthContext](resp.body)
    }
  }

  def updateProfile(userProfile: UserProfile) : Future[Int] = {
    http.post[UserProfile](s"${config.accountsMicroservice}/accounts/update-profile", userProfile) map {
      resp =>
        Logger.info(s"[AccountConnector] - [updateProfile] Response from API Call ${resp.status} - ${resp.statusText}")
        resp.status
    }
  }

  def updatePassword(passwordSet: PasswordSet) : Future[UpdatedPasswordResponse] = {
    http.post[PasswordSet](s"${config.accountsMicroservice}/accounts/update-password", passwordSet) map {
      _.status match {
        case CONFLICT => InvalidOldPassword
        case OK => PasswordUpdated
      }
    }
  }

  def updateSettings(settings: AccountSettings) : Future[UpdatedSettingsResponse] = {
    http.post[AccountSettings](s"${config.accountsMicroservice}/accounts/update-settings", settings) map {
      _.status match {
        case OK => UpdatedSettingsSuccess
        case INTERNAL_SERVER_ERROR => UpdatedSettingsFailed
      }
    }
  }

  def createFeedItem(feedItem: FeedItem) : Future[FeedEventResponse] = {
    http.post[FeedItem](s"${config.accountsMicroservice}/accounts/create-feed-item", feedItem) map {
      resp => resp.status match {
        case OK => FeedEventSuccessResponse
        case INTERNAL_SERVER_ERROR => FeedEventFailedResponse
      }
    }
  }

  def getFeedItems(userId : String) : Future[Option[List[FeedItem]]] = {
    http.get[String](s"${config.accountsMicroservice}/accounts/get-feed", userId) map {
      resp =>
        DataSecurity.decryptInto[JsObject](resp.body) match {
          case Some(obj) => Some(obj.value("feed-array").as[JsArray].as[List[FeedItem]])
          case None => None
        }
    }
  }

  def getBasicDetails(implicit authContext: AuthContext) : Future[Option[BasicDetails]] = {
    http.getUrl(s"${config.accountsMicroservice}${authContext.basicDetailsUri}") map {
      resp => resp.status match {
        case OK => DataSecurity.decryptInto[BasicDetails](resp.body)
        case NOT_FOUND => None
      }
    }
  }

  def getEnrolments(implicit authContext: AuthContext) : Future[Option[Enrolments]] = {
    http.getUrl(s"${config.accountsMicroservice}${authContext.enrolmentsUri}") map {
      resp => resp.status match {
        case OK => DataSecurity.decryptInto[Enrolments](resp.body)
        case NOT_FOUND => None
      }
    }
  }

  def getSettings(implicit authContext: AuthContext) : Future[Option[Settings]] = {
    http.getUrl(s"${config.accountsMicroservice}${authContext.settingsUri}") map {
      resp => resp.status match {
        case OK => DataSecurity.decryptInto[Settings](resp.body)
        case NOT_FOUND => None
      }
    }
  }
}
