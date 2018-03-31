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
package connectors.test

import javax.inject.Inject

import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.http.verbs.Http
import common.ApplicationConfiguration
import play.api.libs.ws.WSResponse
import play.api.mvc.Request

import scala.concurrent.Future

class TeardownConnectorImpl @Inject()(val http: Http,
                                      val configurationLoader: ConfigurationLoader) extends TeardownConnector

trait TeardownConnector extends ApplicationConfiguration {
  val http: Http

  def deleteTestAccountInstance(userName: String, credentialType: String)(implicit request: Request[_]): Future[WSResponse] = {
    http.delete(s"$accountsMicroservice/test-only/test-user/$userName/credential-type/$credentialType/tear-down")
  }
}
