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
import com.cjwwdev.http.exceptions.{ClientErrorException, ForbiddenException, NotFoundException, ServerErrorException}
import com.cjwwdev.http.session.SessionUtils
import com.cjwwdev.http.verbs.Http
import com.cjwwdev.security.encryption.DataSecurity
import com.google.inject.Inject
import common.ApplicationConfiguration
import enums.SessionCache
import models.SessionUpdateSet
import play.api.libs.json.{OFormat, OWrites, Reads}
import play.api.mvc.Request

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SessionStoreConnectorImpl @Inject()(val http : Http,
                                          val configurationLoader: ConfigurationLoader) extends SessionStoreConnector

trait SessionStoreConnector extends ApplicationConfiguration with SessionUtils {
  val http: Http

  def cache[T](sessionId : String, data : T)(implicit format: OFormat[T], request: Request[_]) : Future[SessionCache.Value] = {
    http.post[T](s"$sessionStore/session/$sessionId/cache", data) map {
      _ => SessionCache.cached
    } recover {
      case _: ServerErrorException => SessionCache.cacheFailure
    }
  }

  def getDataElement[T](key : String)(implicit reads: Reads[T], request: Request[_]) : Future[Option[T]] = {
    http.get(s"$sessionStore/session/$getCookieId/data/$key") map { resp =>
      Some(resp.body.decryptType[T])
    } recover {
      case _: NotFoundException => None
    }
  }

  def updateSession(updateSet : SessionUpdateSet)(implicit format: OFormat[SessionUpdateSet], request: Request[_]) : Future[SessionCache.Value] = {
    http.put[SessionUpdateSet](s"$sessionStore/session/$getCookieId", updateSet) map {
      _ => SessionCache.cacheUpdated
    } recover {
      case _: ServerErrorException => SessionCache.cacheUpdateFailure
    }
  }

  def destroySession(implicit request: Request[_]) : Future[SessionCache.Value] = {
    http.delete(s"$sessionStore/session/$getCookieId/destroy") map {
      _ => SessionCache.cacheDestroyed
    } recover {
      case _: ClientErrorException => SessionCache.cacheDestroyed
      case _: ServerErrorException => SessionCache.cacheDestructionFailure
    }
  }
}
