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

import javax.inject.Inject

import play.api.Logger
import config.FrontendConfiguration
import play.api.libs.json.Format
import play.api.libs.ws.{WSClient, WSResponse}
import security.{Encryption, JsonSecurity}

import scala.concurrent.Future

class HttpVerbs @Inject()(implicit http : WSClient) extends JsonSecurity with FrontendConfiguration {

  def post[T](url: String, data: T, additionalHeaders : (String, String) = "" -> "")(implicit format: Format[T]): Future[WSResponse] = {
    Logger.info(s"[HttpPost] [post] Url call : $url")
    val body = encryptModel[T](data).get
    http.url(s"$url")
      .withHeaders(
        "appID" -> Encryption.sha512(APPLICATION_ID),
        "Content-Type" -> "text/plain",
        additionalHeaders
      )
      .withBody(body)
      .post(body)
  }

  def get[T](url: String, data: T, additionalHeaders : (String, String) = "" -> "")(implicit format: Format[T]): Future[WSResponse] = {
    Logger.info(s"[HttPost] [get] Url call : $url")
    val body = encryptModel[T](data).get
    http.url(s"$url")
      .withHeaders(
        "appID" -> Encryption.sha512(APPLICATION_ID),
        additionalHeaders
      )
      .withBody(body).get()
  }
}
