/*
 * Copyright 2019 CJWW Development
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

import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.http.verbs.Http
import javax.inject.Inject
import play.api.libs.ws.WSResponse
import play.api.mvc.Request

import scala.concurrent.{Future, ExecutionContext => ExC}

class DefaultTeardownConnector @Inject()(val http: Http,
                                         val config: ConfigurationLoader) extends TeardownConnector {
  override val accounts: String = config.getServiceUrl("accounts-microservice")
}

trait TeardownConnector {
  val http: Http

  val accounts: String

  def deleteTestAccountInstance(userName: String, credentialType: String)(implicit req: Request[_], ec: ExC): Future[WSResponse] = {
    http.delete(s"$accounts/test-only/test-user/$userName/credential-type/$credentialType/tear-down") map {
      _.fold(identity, identity)
    }
  }
}
