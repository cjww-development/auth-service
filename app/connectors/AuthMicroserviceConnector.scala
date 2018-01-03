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

import javax.inject.Inject

import com.cjwwdev.auth.models.AuthContext
import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.http.exceptions.ForbiddenException
import com.cjwwdev.http.verbs.Http
import com.cjwwdev.security.encryption.DataSecurity
import common._
import models.UserLogin
import play.api.mvc.Request

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthMicroserviceConnectorImpl @Inject()(val http: Http,
                                              val configurationLoader: ConfigurationLoader) extends AuthMicroserviceConnector

trait AuthMicroserviceConnector extends ApplicationConfiguration {
  val http: Http

  def getUser(loginDetails : UserLogin)(implicit request: Request[_]) : Future[AuthContext] = {
    val enc = DataSecurity.encryptType[UserLogin](loginDetails)
    http.GET[AuthContext](s"$authMicroservice/login/user?enc=$enc") recover {
      case e: ForbiddenException => throw e
    }
  }
}
