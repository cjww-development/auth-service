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
package controllers.traits.user

import connectors.SessionStoreConnector
import models.UserAccount
import play.api.mvc.{Action, AnyContent}
import utils.application.FrontendController
import views.html.user.Dashboard

import scala.concurrent.ExecutionContext.Implicits.global

trait DashboardCtrl extends FrontendController {

  val sessionStoreConnector : SessionStoreConnector

  def show : Action[AnyContent] = Action.async {
    implicit request =>
      sessionStoreConnector.getDataElement[UserAccount](request.session("cookieID"), "userInfo") map {
        account =>
          Ok(Dashboard(account.get))
      }
  }
}
