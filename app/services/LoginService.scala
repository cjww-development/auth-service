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
import com.cjwwdev.security.obfuscation.Obfuscation._
import com.cjwwdev.logging.Logging
import connectors._
import javax.inject.Inject
import models.{SessionUpdateSet, UserLogin}
import play.api.mvc.{Request, Session}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DefaultLoginService @Inject()(val authConnector: AuthMicroserviceConnector,
                                    val sessionStoreConnector: SessionStoreConnector) extends LoginService {
  override def generateSessionId: String = s"session-${UUID.randomUUID()}"
}

trait LoginService extends Logging {
  val authConnector: AuthMicroserviceConnector
  val sessionStoreConnector: SessionStoreConnector

  def generateSessionId: String

  private def sessionMap(user: CurrentUser): Map[String, String] = {
    user.credentialType match {
      case "organisation" => Map(
        "cookieId"        -> generateSessionId,
        "orgName"         -> user.orgName.get,
        "credentialType"  -> user.credentialType
      )
      case "individual" => Map(
        "cookieId"        -> generateSessionId,
        "firstName"       -> user.firstName.get,
        "lastName"        -> user.lastName.get,
        "credentialType"  -> user.credentialType,
        if(user.role.isDefined) "role" -> user.role.get else "" -> ""
      )
    }
  }

  def processLoginAttempt(credentials: UserLogin)(implicit request: Request[_]): Future[Option[Session]] = {
    authConnector.getUser(credentials) flatMap {
      _.fold(Future(Option.empty[Session])) { user =>
        val session = Session(sessionMap(user))
        sessionStoreConnector.cache(session("cookieId")) flatMap { _ =>
          sessionStoreConnector.updateSession(SessionUpdateSet("contextId", user.contextId.encrypt), Some(session("cookieId"))) map {
            _ => Some(session)
          }
        }
      }
    }
  }
}
