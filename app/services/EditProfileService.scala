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
package services

import connectors.{AccountConnector, SessionStoreConnector}
import models.SessionUpdateSet
import models.accounts.UserAccount
import play.api.mvc.Request
import security.JsonSecurity

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object EditProfileService extends EditProfileService {
  val accountConnector = AccountConnector
  val sessionStoreConnector = SessionStoreConnector

}

trait EditProfileService {

  val accountConnector : AccountConnector
  val sessionStoreConnector : SessionStoreConnector

  def getDisplayOption(account : Option[UserAccount]) : Option[String] = {
    account.isDefined match {
      case false => Some("full")
      case true => account.get.settings.isDefined match {
        case false => Some("full")
        case true => Some(account.get.settings.get("displayName"))
      }
    }
  }

  def getDisplayNameColour(account: Option[UserAccount]) : Option[String] = {
    account.isDefined match {
      case false => Some("#FFFFFF")
      case true => account.get.settings.isDefined match {
        case false => Some("#FFFFFF")
        case true => account.get.settings.get.get("displayNameColour")
      }
    }
  }

  def getDisplayImageURL(account: Option[UserAccount]) : Option[String] = {
    account.isDefined match {
      case false => Some("/account-services/assets/images/background.jpg")
      case true => account.get.settings.isDefined match {
        case false => Some("/account-services/assets/images/background.jpg")
        case true => account.get.settings.get.get("displayImageURL") match {
          case None => Some("/account-services/assets/images/background.jpg")
          case _ => account.get.settings.get.get("displayImageURL")
        }
      }
    }
  }

  def updateSession(key : String)(implicit request: Request[_]) : Future[Boolean] = {
    for {
      Some(user) <- accountConnector.getAccountData(request.session("_id"))
      uwr <- sessionStoreConnector.updateSession(SessionUpdateSet(key, JsonSecurity.encryptModel[UserAccount](user).get))
    } yield {
      uwr
    }
  }
}
