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

import connectors.AccountConnector
import controllers.user.EditProfileController
import forms.{NewPasswordForm, UserProfileForm}
import mocks.CJWWSpec
import models.auth.AuthContext
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import play.api.Configuration
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class EditProfileControllerSpec extends CJWWSpec {

  val mockMessagesApi : MessagesApi = mock[MessagesApi]
  val mockConfiguration : Configuration = mock[Configuration]
  val mockAccountConnector : AccountConnector = mock[AccountConnector]

  val testController =
    new EditProfileController(
      mockMessagesApi,
      mockConfiguration,
      mockSessionStoreConnector,
      mockAccountConnector,
      mockFeedEventService,
      mockHttpVerbs,
      actions
    )

  "show" should {
    "return an OK" in {
      val request = FakeRequest()
        .withSession(
          "cookieId"  -> s"session-0987654321",
          "contextId" -> s"context-1234567890",
          "firstName" -> "firstName",
          "lastName"  -> "lastName"
        )

      when(mockAccountConnector.getBasicDetails(ArgumentMatchers.any[AuthContext]()))
        .thenReturn(Future.successful(Some(testBasicDetails)))

      when(mockAccountConnector.getSettings(ArgumentMatchers.any[AuthContext]()))
        .thenReturn(Future.successful(Some(testSettings)))

      showWithAuthorisedUser(testController.show, mockAccountConnector, mockHttpVerbs, testContext) {
        result =>
          status(result) mustBe OK
      }
    }
  }


}
