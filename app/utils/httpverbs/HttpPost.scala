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


package utils.httpverbs

import play.api.Logger
import config.FrontendConfiguration
import play.api.libs.json.Format
import play.api.libs.ws.{WS, WSResponse}
import play.api.Play.current
import security.JsonSecurity

import scala.concurrent.Future

object HttpPost extends HttpPost

trait HttpPost extends JsonSecurity with FrontendConfiguration {

  private def post[T](url : String, data : T)(implicit format : Format[T]): Future[WSResponse] = {
    Logger.debug(s"[HttpPost] [post] Url call : $apiCall$url")
    val body = encryptModel[T](data).get
    WS.url(s"$apiCall$url").withHeaders("appID" -> APPLICATION_ID).withBody(body).post(body)
  }

  def postUser[T](url : String, data : T)(implicit format : Format[T]) : Future[WSResponse] = {
    post[T](url, data)
  }
}
