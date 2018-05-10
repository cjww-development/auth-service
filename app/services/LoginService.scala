/*
 * Copyright 2018 CJWW Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package services

import java.util.UUID

import com.cjwwdev.auth.models.CurrentUser
import com.cjwwdev.implicits.ImplicitDataSecurity._
import common.Logging
import connectors._
import javax.inject.Inject
import models.{SessionUpdateSet, UserLogin}
import play.api.libs.json._
import play.api.mvc.{Request, Session}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginServiceImpl @Inject()(val authConnector: AuthMicroserviceConnector,
                                 val sessionStoreConnector: SessionStoreConnector) extends LoginService

trait LoginService extends Logging {
  val authConnector: AuthMicroserviceConnector
  val sessionStoreConnector: SessionStoreConnector

  private val contextIdWriter: OWrites[String] = new OWrites[String] {
    override def writes(o: String) = Json.obj(
      "contextId" -> Json.toJsFieldJsValueWrapper(o)(stringWrites)
    )
  }

  private val contextIdReads: Reads[String] = new Reads[String] {
    override def reads(json: JsValue) = JsSuccess(json.\("contextId").as[String](stringReads))
  }

  private val contextIdFormat: OFormat[String] = OFormat(contextIdReads, contextIdWriter)

  private def sessionMap(user: CurrentUser): Map[String, String] = {
    user.credentialType match {
      case "organisation" => Map(
        "cookieId"        -> s"session-${UUID.randomUUID()}",
        "orgName"         -> user.orgName.get,
        "credentialType"  -> user.credentialType
      )
      case "individual" => Map(
        "cookieId"        -> s"session-${UUID.randomUUID()}",
        "firstName"       -> user.firstName.get,
        "lastName"        -> user.lastName.get,
        "credentialType"  -> user.credentialType,
        if(user.role.isDefined) "role" -> user.role.get else "" -> ""
      )
    }
  }

  def processLoginAttempt(credentials : UserLogin)(implicit request: Request[_]) : Future[Option[Session]] = {
    (for {
      user    <- authConnector.getUser(credentials)
      session =  Session(sessionMap(user))
      _       <- sessionStoreConnector.cache(session("cookieId"))
      _       <- sessionStoreConnector.updateSession(SessionUpdateSet("contextId", user.contextId.encrypt), Some(session("cookieId")))
    } yield Some(session)).recover {
      case _ => None
    }
  }
}
