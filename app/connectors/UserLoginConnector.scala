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
import com.cjwwdev.http.exceptions.HttpExceptions
import com.cjwwdev.http.verbs.Http
import com.google.inject.{Inject, Singleton}
import config.ApplicationConfiguration
import models.UserLogin
import com.cjwwdev.security.encryption.DataSecurity
import play.api.mvc.Request
import play.api.http.Status.{FORBIDDEN, OK}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

sealed trait UserLoginResponse
case class  UserLoginSuccessResponse(contextDetail : AuthContext) extends UserLoginResponse
case object UserLoginFailedResponse extends UserLoginResponse
case object UserLoginException extends UserLoginResponse

@Singleton
class UserLoginConnector @Inject()(http: Http) extends HttpExceptions with ApplicationConfiguration {
  def getUser(loginDetails : UserLogin)(implicit request: Request[_]) : Future[UserLoginResponse] = {
    val enc = DataSecurity.encryptData[UserLogin](loginDetails).get
    http.GET(s"$authMicroservice/login/user?enc=$enc") map { resp =>
      resp.status match {
        case OK => UserLoginSuccessResponse(DataSecurity.decryptInto[AuthContext](resp.body).get)
        case FORBIDDEN => UserLoginFailedResponse
        case _ => UserLoginException
      }
    }
  }
}
