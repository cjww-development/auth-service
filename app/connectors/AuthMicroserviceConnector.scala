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

import com.cjwwdev.auth.models.CurrentUser
import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.http.exceptions.ForbiddenException
import com.cjwwdev.http.responses.WsResponseHelpers
import com.cjwwdev.http.verbs.Http
import com.cjwwdev.implicits.ImplicitDataSecurity._
import common._
import javax.inject.Inject
import models.UserLogin
import play.api.mvc.Request

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DefaultAuthMicroserviceConnector @Inject()(val http: Http,
                                                 val configurationLoader: ConfigurationLoader) extends AuthMicroserviceConnector

trait AuthMicroserviceConnector extends ApplicationConfiguration with WsResponseHelpers {
  val http: Http

  def getUser(loginDetails : UserLogin)(implicit request: Request[_]): Future[Option[CurrentUser]] = {
    http.get(s"$authMicroservice/login/user?enc=${loginDetails.encryptType}") map { resp =>
      Some(resp.toDataType[CurrentUser](needsDecrypt = true))
    } recover {
      case e: ForbiddenException => None
    }
  }
}
