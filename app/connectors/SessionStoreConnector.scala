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
import models.SessionUpdateSet
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.Format
import play.api.libs.ws.WSResponse
import play.api.mvc.Request
import utils.httpverbs.HttpVerbs
import utils.security.DataSecurity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SessionStoreConnector @Inject()(http : HttpVerbs, config : FrontendConfiguration) {

  def cache[T](sessionId : String, data : T)(implicit format: Format[T]) : Future[WSResponse] = {
    http.post[T](s"${config.sessionStore}/cache", data, "sessionID" -> sessionId) map {
      resp =>
        Logger.info(s"[SessionStoreController] - [cache] Response from API Call ${resp.status} - ${resp.statusText}")
        resp
    }
  }

  def getDataElement[T](key : String)(implicit format : Format[T], request: Request[_]) : Future[Option[T]] = {
    http.get[String](s"${config.sessionStore}/get-data-element", key, "sessionID" -> request.session("cookieId")) map {
      data =>
        Logger.info(s"[SessionStoreController] - [getDataElement] Response from API Call ${data.status} - ${data.statusText}")
        DataSecurity.decryptInto[T](data.body)
    }
  }

  def updateSession(updateSet : SessionUpdateSet)(implicit format : Format[SessionUpdateSet], request: Request[_]) : Future[Boolean] = {
    http.post[SessionUpdateSet](s"${config.sessionStore}/update-session", updateSet, "sessionID" -> request.session("cookieId")) map {
      _.status match {
        case OK => true
        case INTERNAL_SERVER_ERROR => false
      }
    }
  }

  def destroySession(sessionId : String) : Future[WSResponse] = {
    http.get[String](s"${config.sessionStore}/destroy", sessionId) map {
      resp =>
        Logger.info(s"[SessionStoreController] - [destroySession] Response from API Call ${resp.status} - ${resp.statusText}")
        resp
    }
  }
}
