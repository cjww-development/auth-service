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
import models.accounts.UserRegister
import play.api.Logger
import utils.httpverbs.HttpVerbs

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

sealed trait UserRegisterResponse
case class UserRegisterSuccessResponse(status : Int) extends UserRegisterResponse
case class UserRegisterClientErrorResponse(status : Int) extends UserRegisterResponse
case class UserRegisterServerErrorResponse(status : Int) extends UserRegisterResponse
case class UserRegisterErrorResponse(status : Int) extends UserRegisterResponse

object UserRegistrationConnector extends UserRegistrationConnector with WSConfiguration {
  val http = new HttpVerbs(getWSClient)
}

trait UserRegistrationConnector extends FrontendConfiguration {

  val http : HttpVerbs

  protected def processStatusCode(statusCode : Int) : UserRegisterResponse = {
    class Contains(r : Range) {
      def unapply(i : Int) : Boolean = r contains i
    }
    val success = new Contains(200 to 299)
    val client = new Contains(400 to 499)
    val server = new Contains(500 to 699)

    statusCode match {
      case success() => UserRegisterSuccessResponse(statusCode)
      case client() => UserRegisterClientErrorResponse(statusCode)
      case server() => UserRegisterServerErrorResponse(statusCode)
      case _ => UserRegisterErrorResponse(statusCode)
    }
  }

  def createNewIndividualUser(userDetails : UserRegister) : Future[UserRegisterResponse] = {
    http.postUser[UserRegister](s"$apiCall/create-new-user", userDetails) map {
      resp =>
        Logger.info(s"[UserRegistrationConnector] [createIndividualUser] Response code from API Call : ${resp.status} - ${resp.statusText}")
        processStatusCode(resp.status)
    }
  }

  def checkUserName(username : String) : Future[Boolean] = {
    http.checkUserName(s"$apiCall/check-user-name", username) map {
      resp =>
        Logger.info(s"[UserRegistrationConnector] - [checkUserName] Response body : ${resp.body}")
        resp.body.toBoolean
    }
  }

  def checkEmailAddress(email : String) : Future[Boolean] = {
    http.checkEmailAddress(s"$apiCall/check-email", email) map {
      resp =>
        Logger.info(s"[UserRegistrationConnector] - [checkEmailAddress] Response body ${resp.body}")
        resp.body.toBoolean
    }
  }
}
