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

import java.util.UUID

import com.cjwwdev.auth.models.AuthContext
import com.google.inject.{Inject, Singleton}
import connectors._
import enums.SessionCache
import models.UserLogin
import models.accounts.UserProfile
import play.api.libs.json.{JsValue, Json, OWrites}
import play.api.mvc.{Request, Session}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class LoginService @Inject()(userLogin: AuthMicroserviceConnector, sessionStoreConnector: SessionStoreConnector) {
  private def sessionMap(context: AuthContext): Map[String, String] = {
    context.user.credentialType match {
      case "organisation" => Map(
        "cookieId"        -> s"session-${UUID.randomUUID()}",
        "contextId"       -> context.contextId,
        "orgName"       -> context.user.orgName.get,
        "credentialType"  -> context.user.credentialType
      )
      case "individual" => Map(
        "cookieId"        -> s"session-${UUID.randomUUID()}",
        "contextId"       -> context.contextId,
        "firstName"       -> context.user.firstName.get,
        "lastName"        -> context.user.lastName.get,
        "credentialType"  -> context.user.credentialType,
        if(context.user.role.isDefined) "role" -> context.user.role.get else "" -> ""
      )
    }
  }

  def processLoginAttempt(credentials : UserLogin)(implicit request: Request[_]) : Future[Option[Session]] = {
    userLogin.getUser(credentials) flatMap { context =>
      val session = Session(sessionMap(context))
      sessionStoreConnector.cache[AuthContext](session("cookieId"), context) map {
        case SessionCache.cached => Some(session)
      } recover {
        case e: Throwable => throw e
      }
    } recover {
      case _: Throwable => None
    }
  }
}
