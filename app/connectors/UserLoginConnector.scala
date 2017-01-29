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
import models.UserLogin
import models.auth.AuthContextDetails
import play.api.Logger
import utils.httpverbs.HttpVerbs
import utils.security.DataSecurity
import play.api.http.Status.{OK, FORBIDDEN}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

sealed trait UserLoginResponse
case class  UserLoginSuccessResponse(contextDetail : AuthContextDetails) extends UserLoginResponse
case object UserLoginFailedResponse extends UserLoginResponse
case object UserLoginException extends UserLoginResponse

@Singleton
class UserLoginConnector @Inject()(http: HttpVerbs, config : FrontendConfiguration) {

  def getUser(loginDetails : UserLogin) : Future[UserLoginResponse] = {
    val enc = DataSecurity.encryptData[UserLogin](loginDetails).get
    http.getUrl(s"${config.authMicroservice}/login?enc=$enc") map {
      resp =>
        Logger.info(s"[UserLoginConnector] [getUser] Response code from api call : ${resp.status} - ${resp.statusText}")
        resp.status match {
          case OK =>
            DataSecurity.decryptInto[AuthContextDetails](resp.body) match {
              case Some(context) => UserLoginSuccessResponse(context)
              case None => UserLoginFailedResponse
            }
          case FORBIDDEN => UserLoginFailedResponse
          case _ => UserLoginException
        }
    }
  }
}
