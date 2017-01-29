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
package controllers.user

import javax.inject.Inject

import auth.{Actions, AuthActions}
import connectors.{AccountConnector, SessionStoreConnector}
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import services.FeedService
import utils.application.FrontendController
import utils.httpverbs.HttpVerbs
import views.html.user.Dashboard

import scala.concurrent.ExecutionContext.Implicits.global

class DashboardController @Inject() (messagesApi: MessagesApi,
                                     configuration: Configuration,
                                     sessionStoreConnector: SessionStoreConnector,
                                     accountConnector: AccountConnector,
                                     feedService: FeedService,
                                     http : HttpVerbs,
                                     actions : AuthActions) extends FrontendController {

  def show : Action[AnyContent] = actions.authorisedFor.async {
    implicit user =>
      implicit request =>
        for {
          Some(basicDetails) <- accountConnector.getBasicDetails
          settings <- accountConnector.getSettings
          feed <- feedService.processRetrievedList(user.user.userId)
        } yield {
          Ok(Dashboard(feed, basicDetails, settings))
        }
  }
}