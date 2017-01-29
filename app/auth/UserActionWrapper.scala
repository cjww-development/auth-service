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

package auth

import com.google.inject.{Inject, Singleton}
import connectors.AccountConnector
import controllers.login.routes
import models.auth.AuthContext
import play.api.mvc.{Action, AnyContent, Results}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class UserActionWrapper @Inject()(accountConnector: AccountConnector) extends Results {

  private[auth] def withAuthenticatedUser()(userAction : AuthContext => Action[AnyContent]) : Action[AnyContent] = {
    Action.async {
      implicit request =>
        val contextId = request.session.get("contextId")
        contextId.isDefined match {
          case false => Action(Redirect(routes.LoginController.show(None)))(request)
          case true => accountConnector.getContext(contextId.get) flatMap {
            case Some(context) => userAction(context)(request)
            case None => Action(Redirect(routes.LoginController.show(None)))(request)
          }
        }
    }
  }

  private[auth] def withPotentialUser()(userAction : Option[AuthContext] => Action[AnyContent]) : Action[AnyContent] = {
    Action.async {
      implicit request =>
        val userId = request.session.get("_id")
        userId.isDefined match {
          case false => userAction(None)(request)
          case true => accountConnector.getContext(userId.get) flatMap {
            context => userAction(context)(request)
          }
        }
    }
  }
}
