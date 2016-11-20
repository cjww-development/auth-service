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
import models.accounts.{UserAccount, UserProfile}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import org.mockito.Mockito._
import org.mockito.Matchers
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class DashboardControllerSpec extends PlaySpec with OneAppPerSuite with MockitoSugar {

  val mockSessionStore = mock[SessionStoreConnector]
  val mockAccountConnector = mock[AccountConnector]

  val testUser = UserAccount(Some("testID"), "testFirstName", "testLastName", "testUserName", "testEmail", "testPassword")

  class Setup {
    class TestController extends DashboardCtrl {
      val sessionStoreConnector = mockSessionStore
      val accountConnector = mockAccountConnector
    }

    val testController = new TestController
  }

  "show" should {
    "return an OK" in new Setup {
      when(mockSessionStore.getDataElement[UserAccount](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Some(testUser)))

      val result = testController.show()(FakeRequest()
        .withSession(
          "cookieID" -> "sessionID",
          "firstName" -> "testFirstName",
          "lastName" -> "testLastName"))
      status(result) mustBe OK
    }
  }

  "updateProfile" should {
    "return a bad request" when {
      "given a set of invalid user profile information" in new Setup {
        when(mockSessionStore.getDataElement[UserAccount](Matchers.any())(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(Some(testUser)))

        val request = FakeRequest().withFormUrlEncodedBody(
          "firstName" -> "",
          "lastName" -> "",
          "userName" -> "",
          "email" -> ""
        ).withSession(
          "cookieID" -> "sessionID",
          "firstName" -> "testFirstName",
          "lastName" -> "testLastName"
        )

        val result = testController.updateProfile()(request)
        status(result) mustBe BAD_REQUEST
      }
    }

    "return an Ok" when {
      "given a set of valid user profile information" in new Setup {
        when(mockAccountConnector.updateProfile(Matchers.any()))
          .thenReturn(Future.successful(OK))

        when(mockSessionStore.getDataElement[UserAccount](Matchers.any())(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(Some(testUser)))

        val request = FakeRequest().withFormUrlEncodedBody(
          "firstName" -> "aaa",
          "lastName" -> "bbb",
          "userName" -> "testUserName",
          "email" -> "ccc"
        ).withSession(
          "cookieID" -> "sessionID",
          "firstName" -> "testFirstName",
          "lastName" -> "testLastName"
        )

        val result = testController.updateProfile()(request)
        status(result) mustBe OK
      }
    }
  }
}
