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
package services

import com.cjwwdev.auth.models.CurrentUser
import connectors.DeversityMicroserviceConnector
import enums.HttpResponse
import javax.inject.Inject
import models.RegistrationCode
import play.api.mvc.Request

import scala.concurrent.{ExecutionContext => ExC, Future}

class DefaultRegistrationCodeService @Inject()(val deversityConnector: DeversityMicroserviceConnector) extends RegistrationCodeService

trait RegistrationCodeService {
  val deversityConnector: DeversityMicroserviceConnector

  def getGeneratedCode(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[RegistrationCode] = {
    deversityConnector.getRegistrationCode
  }

  def generateRegistrationCode(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[HttpResponse.Value] = {
    deversityConnector.generateRegistrationCode
  }
}
