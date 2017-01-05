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

package services

import connectors.{AccountConnector, SessionStoreConnector}
import models.SessionUpdateSet
import models.accounts.UserAccount
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import org.mockito.Mockito._
import org.mockito.Matchers
import play.api.test.FakeRequest
import security.JsonSecurity

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class EditProfileServiceSpec extends PlaySpec with MockitoSugar with OneAppPerSuite {

  val mockAccountConnector = mock[AccountConnector]
  val mockSessionStoreConnector = mock[SessionStoreConnector]

  class Setup {
    object TestService extends EditProfileService {
      val accountConnector = mockAccountConnector
      val sessionStoreConnector = mockSessionStoreConnector
    }

    val userAccountWithSettings =
      UserAccount(
        Some("testUserId"),
        "testFirstName",
        "testLastName",
        "testUserName",
        "testEmail",
        "testPassword",
        Some(
          Map(
            "displayName" -> "user",
            "displayNameColour" -> "#124AA0"
          )
        ),
        None)

    val userAccountWithoutSettings =
      UserAccount(
        Some("testUserId"),
        "testFirstName",
        "testLastName",
        "testUserName",
        "testEmail",
        "testPassword",
        None,
        None
      )

    val sessionUpdateSet =
      SessionUpdateSet(
        "userInfo",
        JsonSecurity.encryptModel(userAccountWithSettings).get
      )
  }

  "getDisplayOption" should {
    "return full" when {
      "no settings map is defined" in new Setup {
        val result = TestService.getDisplayOption(Some(userAccountWithoutSettings))
        result mustBe Some("full")
      }

      "the account is not defined" in new Setup {
        val result = TestService.getDisplayOption(None)
        result mustBe Some("full")
      }
    }

    "return user" in new Setup {
      val result = TestService.getDisplayOption(Some(userAccountWithSettings))
      result mustBe Some("user")
    }
  }

  "getDisplayNameColour" should {
    "return White" when {
      "no account is defined" in new Setup {
        val result = TestService.getDisplayNameColour(None)
        result mustBe Some("#FFFFFF")
      }

      "no settings map is defined" in new Setup {
        val result = TestService.getDisplayNameColour(Some(userAccountWithoutSettings))
        result mustBe Some("#FFFFFF")
      }
    }

    "return a shade of blue" when {
      "the user has edited their profile and chosen a colour" in new Setup {
        val result = TestService.getDisplayNameColour(Some(userAccountWithSettings))
        result mustBe Some("#124AA0")
      }
    }
  }

  "updateSession" should {
    "return true" when {
      "the users session has been updated" in new Setup {

        implicit val request =
          FakeRequest()
            .withSession(
              "_id" -> "testUserID",
              "cookieID" -> "testCookieID"
            )

        when(mockAccountConnector.getAccountData(Matchers.eq("testUserID")))
          .thenReturn(Future.successful(Some(userAccountWithSettings)))

        when(mockSessionStoreConnector.updateSession(Matchers.eq(sessionUpdateSet))(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(true))

        val result = Await.result(TestService.updateSession("userInfo"), 5.seconds)
        result mustBe true
      }
    }

    "return false" when {
      "the users session has not been updated" in new Setup {

        implicit val request =
          FakeRequest()
            .withSession(
              "_id" -> "testUserID",
              "cookieID" -> "testCookieID"
            )

        when(mockAccountConnector.getAccountData(Matchers.eq("testUserID")))
          .thenReturn(Future.successful(Some(userAccountWithSettings)))

        when(mockSessionStoreConnector.updateSession(Matchers.eq(sessionUpdateSet))(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(false))

        val result = Await.result(TestService.updateSession("userInfo"), 5.seconds)
        result mustBe false
      }
    }
  }
}
