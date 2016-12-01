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
import models.UserLogin
import models.accounts.UserAccount
import play.api.Logger
import security.JsonSecurity
import utils.httpverbs.HttpVerbs

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

sealed trait UserLoginResponse
case class UserLoginSuccessResponse(account : UserAccount) extends UserLoginResponse
case object UserLoginFailedResponse extends UserLoginResponse
case object UserLoginException extends UserLoginResponse

object UserLoginConnector extends UserLoginConnector with WSConfiguration {
  val http = new HttpVerbs(getWSClient)
}

trait UserLoginConnector extends FrontendConfiguration{

  val http : HttpVerbs

  def getUserAccountInformation(loginDetails : UserLogin) : Future[UserLoginResponse] = {
    http.getUserDetails[UserLogin](s"$apiCall/individual-user-login", loginDetails) map {
      resp =>
        Logger.info(s"[UserLoginConnector] [getUserAccountInformation] Response code from api call : ${resp.status} - ${resp.statusText}")
        JsonSecurity.decryptInto[UserAccount](resp.body) match {
          case Some(account) => UserLoginSuccessResponse(account)
          case None => UserLoginFailedResponse
        }
    }
  }
}
