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

import config.{FrontendConfiguration, WSConfiguration}
import models.accounts._
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.{JsArray, JsObject}
import security.JsonSecurity
import utils.httpverbs.HttpVerbs

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

sealed trait UpdatedPasswordResponse
case object InvalidOldPassword extends UpdatedPasswordResponse
case object PasswordUpdated extends UpdatedPasswordResponse

sealed trait UpdatedSettingsResponse
case object UpdatedSettingsSuccess extends UpdatedSettingsResponse
case object UpdatedSettingsFailed extends UpdatedSettingsResponse

sealed trait FeedEventResponse
case object FeedEventSuccessResponse extends FeedEventResponse
case object FeedEventFailedResponse extends FeedEventResponse

object AccountConnector extends AccountConnector with WSConfiguration {
  val http = new HttpVerbs()
}

trait AccountConnector extends FrontendConfiguration {

  val http : HttpVerbs

  def getAccountData(userID : String) : Future[Option[UserAccount]] = {
    http.get[String](s"$apiCall/get-account", userID) map {
      resp =>
        JsonSecurity.decryptInto[UserAccount](resp.body)
    }
  }

  def updateProfile(userProfile: UserProfile) : Future[Int] = {
    http.post[UserProfile](s"$apiCall/update-profile", userProfile) map {
      resp =>
        Logger.info(s"[AccountConnector] - [updateProfile] Response from API Call ${resp.status} - ${resp.statusText}")
        resp.status
    }
  }

  def updatePassword(passwordSet: PasswordSet) : Future[UpdatedPasswordResponse] = {
    http.post[PasswordSet](s"$apiCall/update-password", passwordSet) map {
      _.status match {
        case CONFLICT => InvalidOldPassword
        case OK => PasswordUpdated
      }
    }
  }

  def updateSettings(settings: AccountSettings) : Future[UpdatedSettingsResponse] = {
    http.post[AccountSettings](s"$apiCall/update-settings", settings) map {
      _.status match {
        case OK => UpdatedSettingsSuccess
        case INTERNAL_SERVER_ERROR => UpdatedSettingsFailed
      }
    }
  }

  def createFeedItem(feedItem: FeedItem) : Future[FeedEventResponse] = {
    http.post[FeedItem](s"$apiCall/create-feed-item", feedItem) map {
      resp => resp.status match {
        case OK => FeedEventSuccessResponse
        case INTERNAL_SERVER_ERROR => FeedEventFailedResponse
      }
    }
  }

  def getFeedItems(userId : String) : Future[Option[List[FeedItem]]] = {
    http.get[String](s"$apiCall/get-feed", userId) map {
      resp =>
        resp.status match {
          case NOT_FOUND => None
          case OK =>
            JsonSecurity.decryptInto[JsObject](resp.body) match {
              case None => None
              case Some(obj) => Some(obj.value("feed-array").as[JsArray].as[List[FeedItem]])
            }
        }
    }
  }
}
