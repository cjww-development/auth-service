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

import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.http.exceptions.{ForbiddenException, NotFoundException, ServerErrorException}
import com.cjwwdev.http.utils.SessionUtils
import com.google.inject.{Inject, Singleton}
import config.ApplicationConfiguration
import models.SessionUpdateSet
import play.api.libs.json.{OWrites, Reads}
import play.api.mvc.Request
import play.api.http.Status.{CREATED, OK}
import com.cjwwdev.http.verbs.Http
import enums.SessionCache

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SessionStoreConnector @Inject()(http : Http, val config: ConfigurationLoader) extends ApplicationConfiguration with SessionUtils {
  def cache[T](sessionId : String, data : T)(implicit writes: OWrites[T], request: Request[_]) : Future[SessionCache.Value] = {
    http.POST[T](s"$sessionStore/session/$sessionId/cache", data) map {
      _.status match {
        case CREATED => SessionCache.cached
      }
    } recover {
      case _: ServerErrorException => SessionCache.cacheFailure
      case e: ForbiddenException  => throw e
    }
  }

  def getDataElement[T](key : String)(implicit reads: Reads[T], request: Request[_]) : Future[Option[T]] = {
    http.GET(s"$sessionStore/session/$getCookieId/data/$key") map {
      data => Some(data)
    } recover {
      case _: NotFoundException   => None
      case e: ForbiddenException  => throw e
    }
  }

  def updateSession(updateSet : SessionUpdateSet)(implicit writes: OWrites[SessionUpdateSet], request: Request[_]) : Future[SessionCache.Value] = {
    http.PUT[SessionUpdateSet](s"$sessionStore/session/$getCookieId", updateSet) map {
      _.status match {
        case OK => SessionCache.cacheUpdated
      }
    } recover {
      case _: ServerErrorException => SessionCache.cacheUpdateFailure
    }
  }

  def destroySession(implicit request: Request[_]) : Future[SessionCache.Value] = {
    http.DELETE(s"$sessionStore/session/$getCookieId/destroy") map {
      _.status match {
        case OK => SessionCache.cacheDestroyed
      }
    } recover {
      case _: ServerErrorException => SessionCache.cacheDestructionFailure
    }
  }
}
