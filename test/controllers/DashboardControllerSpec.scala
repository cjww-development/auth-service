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

import connectors.{AccountConnector, SessionStoreConnector}
import controllers.traits.user.DashboardCtrl
import models.accounts._
import org.joda.time.DateTime
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import org.mockito.Mockito._
import org.mockito.Matchers
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.FeedService

import scala.concurrent.Future

class DashboardControllerSpec extends PlaySpec with OneAppPerSuite with MockitoSugar {

  val mockSessionStore = mock[SessionStoreConnector]
  val mockFeedService = mock[FeedService]

  val testUser = UserAccount(Some("testID"), "testFirstName", "testLastName", "testUserName", "testEmail", "testPassword")

  class Setup {
    class TestController extends DashboardCtrl {
      val sessionStoreConnector = mockSessionStore
      val feedService = mockFeedService
    }

    val testController = new TestController
  }

  "show" should {
    "return an OK" in new Setup {
      when(mockSessionStore.getDataElement[UserAccount](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Some(testUser)))

      when(mockFeedService.processRetrievedList(Matchers.any()))
        .thenReturn(Future.successful(None))

      val result = testController.show()(FakeRequest()
        .withSession(
          "cookieID" -> "sessionID",
          "_id" -> "testUserID",
          "firstName" -> "testFirstName",
          "lastName" -> "testLastName"))
      status(result) mustBe OK
    }
  }
}
