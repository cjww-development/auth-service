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

import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.http.exceptions.{ClientErrorException, NotFoundException, ServerErrorException}
import com.cjwwdev.http.responses.WsResponseHelpers
import com.cjwwdev.http.session.SessionUtils
import com.cjwwdev.http.verbs.Http
import common.ApplicationConfiguration
import enums.SessionCache
import javax.inject.Inject
import models.SessionUpdateSet
import play.api.libs.json.{OFormat, Reads}
import play.api.mvc.Request
import services.FeatureService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DefaultSessionStoreConnector @Inject()(val http : Http,
                                             val featureService: FeatureService) extends SessionStoreConnector

trait SessionStoreConnector extends ApplicationConfiguration with SessionUtils with WsResponseHelpers {
  val http: Http

  def cache(sessionId : String)(implicit request: Request[_]) : Future[SessionCache.Value] = {
    http.postString(s"$sessionStore/session/$sessionId", "") map {
      _ =>
        SessionCache.cached
    } recover {
      case _: ServerErrorException => SessionCache.cacheFailure
    }
  }

  def getDataElement[T](key : String)(implicit reads: Reads[T], request: Request[_]) : Future[Option[T]] = {
    http.get(s"$sessionStore/session/$getCookieId/data?key=$key") map { resp =>
      Some(resp.toDataType[T](needsDecrypt = true))
    } recover {
      case _: NotFoundException => None
    }
  }

  def updateSession(updateSet : SessionUpdateSet, sessionId: Option[String])
                   (implicit format: OFormat[SessionUpdateSet], request: Request[_]) : Future[SessionCache.Value] = {
    val id = sessionId.getOrElse(getCookieId)
    http.patch[SessionUpdateSet](s"$sessionStore/session/$id", updateSet, secure = false) map {
      _ => SessionCache.cacheUpdated
    } recover {
      case _: ServerErrorException => SessionCache.cacheUpdateFailure
    }
  }

  def destroySession(implicit request: Request[_]) : Future[SessionCache.Value] = {
    http.delete(s"$sessionStore/session/$getCookieId") map {
      _ => SessionCache.cacheDestroyed
    } recover {
      case _: ClientErrorException => SessionCache.cacheDestroyed
      case _: ServerErrorException => SessionCache.cacheDestructionFailure
    }
  }
}
