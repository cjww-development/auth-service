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

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import config.FrontendConfiguration
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.Format
import play.api.libs.ws.{WSClient, WSResponse}
import utils.security.{DataSecurity, Encryption}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class HttpVerbs @Inject()(http : WSClient, applicationLifecycle: ApplicationLifecycle, frontendConfiguration: FrontendConfiguration)
  extends DataSecurity {

  applicationLifecycle.addStopHook {
    () => Future {
      ActorSystem().terminate()
      http.close()
    }
  }

  def post[T](url: String, data: T, additionalHeaders : (String, String) = "" -> "")(implicit format: Format[T]): Future[WSResponse] = {
    Logger.info(s"[HttpPost] [post] Url call : $url")
    val body = encryptData[T](data).get
    http.url(s"$url")
      .withHeaders(
        "appID" -> Encryption.sha512(frontendConfiguration.APPLICATION_ID),
        "Content-Type" -> "text/plain",
        additionalHeaders
      )
      .withBody(body)
      .post(body)
  }

  def get[T](url: String, data: T, additionalHeaders : (String, String) = "" -> "")(implicit format: Format[T]): Future[WSResponse] = {
    Logger.info(s"[HttPost] [get] Url call : $url")
    val body = encryptData[T](data).get
    http.url(s"$url")
      .withHeaders(
        "appID" -> Encryption.sha512(frontendConfiguration.APPLICATION_ID),
        additionalHeaders
      )
      .withBody(body).get()
  }

  def getUrl(url : String) : Future[WSResponse] = {
    Logger.info(s"[HttpVerbs] - [getUrl] : Url call : $url")
    http.url(url).withHeaders("appID" -> Encryption.sha512(frontendConfiguration.APPLICATION_ID)).get()
  }
}
