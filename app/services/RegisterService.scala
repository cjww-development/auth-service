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

package services

import connectors.AccountsConnector
import enums.Registration
import javax.inject.Inject
import models.registration.{OrgRegistration, UserRegistration}
import play.api.mvc.Request

import scala.concurrent.{Future, ExecutionContext => ExC}

class DefaultRegisterService @Inject()(val accountsConnector: AccountsConnector) extends RegisterService

trait RegisterService {
  val accountsConnector: AccountsConnector

  def registerIndividual(user : UserRegistration)(implicit req: Request[_], ec: ExC): Future[Registration.Value] = {
    for {
      userNameInUse <- accountsConnector.checkUserName(user.userName)
      emailInUse    <- accountsConnector.checkEmailAddress(user.email)
      registered    <- if(!userNameInUse & !emailInUse) {
        accountsConnector.createNewIndividualUser(user)
      } else {
        Future.successful(evaluateInUseResponses(userNameInUse, emailInUse))
      }
    } yield registered
  }

  def registerOrg(org: OrgRegistration)(implicit req: Request[_], ec: ExC): Future[Registration.Value] = {
    for {
      userNameInUse <- accountsConnector.checkUserName(org.orgUserName)
      emailInUse    <- accountsConnector.checkEmailAddress(org.orgEmail)
      registered    <- if(!userNameInUse & !emailInUse) {
        accountsConnector.createNewOrgUser(org)
      } else {
        Future.successful(evaluateInUseResponses(userNameInUse, emailInUse))
      }
    } yield registered
  }

  private def evaluateInUseResponses(inUse: (Boolean, Boolean)): Registration.Value = {
    inUse match {
      case (true, true)   => Registration.bothInUse
      case (false, true)  => Registration.emailInUse
      case (true, false)  => Registration.userNameInUse
      case _              => Registration.failed
    }
  }
}
