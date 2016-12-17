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

import connectors.{SessionStoreConnector, UserLoginConnector, UserLoginFailedResponse, UserLoginSuccessResponse}
import models.UserLogin
import models.accounts.UserAccount
import play.api.Logger
import play.api.mvc.Session

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object LoginService extends LoginService {
  val userLogin = UserLoginConnector
  val sessionStoreConnector = SessionStoreConnector
}

trait LoginService {

  val userLogin : UserLoginConnector

  val sessionStoreConnector : SessionStoreConnector

  def processLoginAttempt(credentials : UserLogin) : Future[Option[Session]] = {
    userLogin.getUserAccountInformation(credentials.encryptPassword) flatMap {
      case UserLoginSuccessResponse(user) =>
        val session = Session(user.sessionMap)
        Logger.debug(s"[Login Service] - [processLoginAttempt] : Resp Model - $user")
        sessionStoreConnector.cache[UserAccount](session("cookieID"), user).map {
          _ => Some(session)
        }
      case UserLoginFailedResponse => Future.successful(None)
    }
  }
}
