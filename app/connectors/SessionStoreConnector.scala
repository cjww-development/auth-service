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
import play.api.Logger
import play.api.libs.json.Format
import play.api.libs.ws.WSResponse
import play.api.mvc.Request
import security.JsonSecurity
import utils.httpverbs.HttpVerbs

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object SessionStoreConnector extends SessionStoreConnector with WSConfiguration {
  val http = new HttpVerbs(getWSClient)
}

trait SessionStoreConnector extends FrontendConfiguration {

  val http : HttpVerbs

  def cache[T](sessionId : String, data : T)(implicit format: Format[T]) : Future[WSResponse] = {
    Logger.debug(s"data being inserted = $data")
    http.cache[T](s"$sessionStore/cache", sessionId, data)
  }

  //TODO Test this [getDataElement]
  // $COVERAGE-OFF$
  def getDataElement[T](key : String)(implicit format : Format[T], request: Request[_]) : Future[Option[T]] = {
    http.getDataEntry(s"$sessionStore/get-data-element", request.session("cookieID"), key) map {
      data =>
        Logger.debug(s"[SessionStoreConnector] - [getDataElement] : Data from session store = ${data.body}")
        JsonSecurity.decryptInto[T](data.body)
    }
  }
  // $COVERAGE-ON$

  def destroySession(sessionId : String) : Future[WSResponse] = {
    http.destroySession(s"$sessionStore/destroy", sessionId)
  }
}
