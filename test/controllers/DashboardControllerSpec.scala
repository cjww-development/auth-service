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

package controllers

import auth.{Actions, AuthActions, AuthenticatedAction, UnauthenticatedAction}
import connectors.{AccountConnector, SessionStoreConnector}
import controllers.user.DashboardController
import mocks.CJWWSpec
import models.accounts._
import models.auth.AuthContext
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.inject.Injector
import play.api.mvc.{Action, AnyContent, Request, Result}
import services.FeedService
import utils.httpverbs.HttpVerbs

import scala.concurrent.Future

class DashboardControllerSpec extends CJWWSpec {

  val testUser = UserAccount(Some("testID"), "testFirstName", "testLastName", "testUserName", "testEmail", "testPassword")

  val messagesApi: MessagesApi = mock[MessagesApi]
  val configuration : Configuration = mock[Configuration]
  val sessionStoreConnector : SessionStoreConnector = mock[SessionStoreConnector]
  val accountConnector : AccountConnector = mock[AccountConnector]
  val feedService : FeedService = mock[FeedService]
  val http : HttpVerbs = mock[HttpVerbs]

  val testController = new DashboardController(messagesApi,configuration,sessionStoreConnector,accountConnector,feedService,http, actions)

  "show" should {
    "return an OK" in {
      val request = FakeRequest()
        .withSession(
          "cookieId"  -> s"session-0987654321",
          "contextId" -> s"context-1234567890",
          "firstName" -> "firstName",
          "lastName"  -> "lastName"
        )

      when(accountConnector.getBasicDetails(ArgumentMatchers.any[AuthContext]()))
        .thenReturn(Future.successful(Some(testBasicDetails)))

      when(accountConnector.getSettings(ArgumentMatchers.any[AuthContext]()))
        .thenReturn(Future.successful(Some(testSettings)))

      when(feedService.processRetrievedList(ArgumentMatchers.any()))
        .thenReturn(Future.successful(None))

      showWithAuthorisedUser(testController.show,accountConnector,http,testContext) {
        result =>
          status(result) mustBe OK
      }
    }
  }
}
