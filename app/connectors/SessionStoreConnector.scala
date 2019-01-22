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

package connectors

import com.cjwwdev.http.responses.WsResponseHelpers
import com.cjwwdev.http.responses.EvaluateResponse._
import com.cjwwdev.http.session.SessionUtils
import com.cjwwdev.http.verbs.Http
import com.cjwwdev.security.deobfuscation.DeObfuscator
import common.ApplicationConfiguration
import enums.SessionCache
import javax.inject.Inject
import models.SessionUpdateSet
import play.api.libs.json.{OFormat, Reads}
import play.api.mvc.Request

import scala.concurrent.{ExecutionContext => ExC, Future}

class DefaultSessionStoreConnector @Inject()(val http : Http) extends SessionStoreConnector

trait SessionStoreConnector extends ApplicationConfiguration with SessionUtils with WsResponseHelpers {
  val http: Http

  def cache(sessionId : String)(implicit req: Request[_], ec: ExC) : Future[SessionCache.Value] = {
    http.postString(s"$sessionStore/session/$sessionId", "") map {
      case SuccessResponse(_) => SessionCache.cached
      case ErrorResponse(_)   => SessionCache.cacheFailure
    }
  }

  def getDataElement[T](key : String)(implicit deObfuscator: DeObfuscator[T], reads: Reads[T], req: Request[_], ec: ExC) : Future[Option[T]] = {
    http.get(s"$sessionStore/session/$getCookieId/data?key=$key") map {
      case SuccessResponse(resp) => resp.toDataType[T](needsDecrypt = true).fold(Some(_), _ => None)
      case ErrorResponse(_)      => None
    }
  }

  def updateSession(updateSet : SessionUpdateSet, sessionId: Option[String])
                   (implicit format: OFormat[SessionUpdateSet], req: Request[_], ec: ExC) : Future[SessionCache.Value] = {
    val id = sessionId.getOrElse(getCookieId)
    http.patch[SessionUpdateSet](s"$sessionStore/session/$id", updateSet, secure = false) map {
      case SuccessResponse(_) => SessionCache.cacheUpdated
      case ErrorResponse(_)   => SessionCache.cacheUpdateFailure
    }
  }

  def destroySession(implicit req: Request[_], ec: ExC) : Future[SessionCache.Value] = {
    http.delete(s"$sessionStore/session/$getCookieId") map {
      case SuccessResponse(_)  => SessionCache.cacheDestroyed
      case ErrorResponse(resp) => resp.status match {
        case client if (400 to 499).contains(client) => SessionCache.cacheDestroyed
        case server if (500 to 599).contains(server) => SessionCache.cacheDestructionFailure
      }
    }
  }
}
