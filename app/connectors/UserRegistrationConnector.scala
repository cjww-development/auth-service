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

import com.cjwwdev.http.verbs.Http
import com.google.inject.{Inject, Singleton}
import config.ApplicationConfiguration
import models.accounts.{OrgAccount, OrgRegister, UserRegister}
import com.cjwwdev.logging.Logger
import play.api.http.Status._
import com.cjwwdev.security.encryption.DataSecurity
import play.api.mvc.Request

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

sealed trait UserRegisterResponse
case class UserRegisterSuccessResponse(status : Int) extends UserRegisterResponse
case class UserRegisterClientErrorResponse(status : Int) extends UserRegisterResponse
case class UserRegisterServerErrorResponse(status : Int) extends UserRegisterResponse
case class UserRegisterErrorResponse(status : Int) extends UserRegisterResponse

@Singleton
class UserRegistrationConnector @Inject()(http : Http) extends ApplicationConfiguration {
  private[connectors] def processStatusCode(statusCode : Int) : UserRegisterResponse = {
    class Contains(r : Range) {
      def unapply(i : Int) : Boolean = r contains i
    }
    val success = new Contains(200 to 299)
    val client = new Contains(400 to 499)
    val server = new Contains(500 to 699)

    statusCode match {
      case success()  => UserRegisterSuccessResponse(statusCode)
      case client()   => UserRegisterClientErrorResponse(statusCode)
      case server()   => UserRegisterServerErrorResponse(statusCode)
      case _          => UserRegisterErrorResponse(statusCode)
    }
  }

  def createNewIndividualUser(userDetails : UserRegister)(implicit request: Request[_]) : Future[UserRegisterResponse] = {
    http.POST[UserRegister](s"$accountsMicroservice/account/create-new-user", userDetails) map {
      resp =>
        Logger.info(s"[UserRegistrationConnector] [createIndividualUser] Response code from API Call : ${resp.status} - ${resp.statusText}")
        processStatusCode(resp.status)
    }
  }

  def createNewOrgUser(orgUserDetails: OrgRegister)(implicit request: Request[_]): Future[UserRegisterResponse] = {
    http.POST[OrgRegister](s"$accountsMicroservice/account/create-new-org-user", orgUserDetails) map {
      resp =>
        Logger.info(s"[UserRegistrationConnector] - [createNewOrgUser] Response code from API call : ${resp.status}")
        processStatusCode(resp.status)
    }
  }

  def checkUserName(username : String)(implicit request: Request[_]) : Future[Boolean] = {
    val encUsername = DataSecurity.encryptData[String](username).get
    http.GET(s"$accountsMicroservice/validate/user-name/$encUsername") map {
      _.status match {
        case OK => false
        case CONFLICT => true
      }
    }
  }

  def checkEmailAddress(email : String)(implicit request: Request[_]) : Future[Boolean] = {
    val encEmailAddress = DataSecurity.encryptData[String](email).get
    http.GET(s"$accountsMicroservice/validate/email/$encEmailAddress") map {
      _.status match {
        case OK => false
        case CONFLICT => true
      }
    }
  }
}
