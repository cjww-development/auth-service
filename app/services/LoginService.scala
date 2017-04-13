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

import java.util.UUID

import com.cjwwdev.auth.models.AuthContext
import com.google.inject.{Inject, Singleton}
import connectors._
import models.UserLogin
import play.api.mvc.{Request, Session}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class LoginService @Inject()(userLogin: UserLoginConnector, sessionStoreConnector: SessionStoreConnector) {
  private def sessionMap(context: AuthContext) : Map[String, String] = Map(
    "cookieId" -> s"session-${UUID.randomUUID()}",
    "contextId" -> context.contextId,
    "firstName" -> context.user.firstName,
    "lastName" -> context.user.lastName
  )

  def processLoginAttempt(credentials : UserLogin)(implicit request: Request[_]) : Future[Option[Session]] = {
    userLogin.getUser(credentials.encryptPassword) flatMap {
      case UserLoginSuccessResponse(context) =>
        val session = Session(sessionMap(context))
        sessionStoreConnector.cache[String](session("cookieId"), context.contextId).map {
          _ => Some(session)
        }
      case UserLoginFailedResponse | UserLoginException => Future.successful(None)
    }
  }
}
