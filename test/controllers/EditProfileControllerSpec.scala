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

import connectors._
import controllers.traits.user.EditProfileCtrl
import models.accounts.{AccountSettings, PasswordSet, UserAccount}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import org.mockito.Mockito._
import org.mockito.Matchers
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{EditProfileService, FeedService}

import scala.concurrent.Future

class EditProfileControllerSpec extends PlaySpec with OneAppPerSuite with MockitoSugar {

  val mockAccountConnector = mock[AccountConnector]
  val mockSessionStoreConnector = mock[SessionStoreConnector]
  val mockEditProfileService = mock[EditProfileService]
  val mockFeedEventService = mock[FeedService]

  val testUser = UserAccount(Some("testUserId"),"testFirstName","testLastName","testUserName","testEmail","testPassword",None,None)

  class Setup {
    class TestController extends EditProfileCtrl {
      val sessionStoreConnector = mockSessionStoreConnector
      val accountConnector = mockAccountConnector
      val editProfileService = mockEditProfileService
      val feedEventService = mockFeedEventService
    }

    val testController = new TestController
  }

  "show" should {
    "return an OK" in new Setup {
      when(mockSessionStoreConnector.getDataElement[UserAccount](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Some(testUser)))

      val result = testController.show()(FakeRequest().withSession("cookieID" -> "testUserId"))
      status(result) mustBe OK
    }
  }

  "updateProfile" should {
    "redirect to the login page" in new Setup {
      when(mockSessionStoreConnector.getDataElement[UserAccount](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Some(testUser)))

      val result = testController.updateProfile()(FakeRequest())
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/account-services/login")
    }

    "return a BadRequest" in new Setup {
      when(mockSessionStoreConnector.getDataElement[UserAccount](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Some(testUser)))

      val request = FakeRequest()
        .withSession("cookieID" -> "testCookieID")
        .withFormUrlEncodedBody(
          "firstName" -> "",
          "lastName" -> "",
          "userName" -> "",
          "email" -> ""
        )

      val result = testController.updateProfile()(request)
      status(result) mustBe BAD_REQUEST
    }

    "redirect to the edit profile page" in new Setup {
      when(mockAccountConnector.updateProfile(Matchers.any()))
        .thenReturn(Future.successful(OK))

      when(mockFeedEventService.basicDetailsFeedEvent(Matchers.any()))
        .thenReturn(Future.successful(FeedEventSuccessResponse))

      when(mockEditProfileService.updateSession(Matchers.eq("userInfo"))(Matchers.any()))
        .thenReturn(Future.successful(true))

      val request = FakeRequest()
        .withSession("cookieID" -> "testCookieID")
        .withFormUrlEncodedBody(
          "firstName" -> "testFirstName",
          "lastName" -> "testLastName",
          "userName" -> "testUserName",
          "email" -> "test@email.com"
        )

      val result = testController.updateProfile()(request)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/account-services/edit-your-profile")
    }
  }

  "updatePassword" should {
    "redirect to the login" when {
      "there is no active user session" in new Setup {
        val result = testController.updatePassword()(FakeRequest())
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/account-services/login")
      }
    }

    "return a bad request" when {
      "the bound form has errors" in new Setup {
        val request = FakeRequest()
          .withSession("cookieID" -> "testCookieID")
          .withFormUrlEncodedBody(
          "oldPassword" -> "",
          "newPassword" -> "",
          "confirmPassword" -> ""
        )

        when(mockSessionStoreConnector.getDataElement[UserAccount](Matchers.any())(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(Some(testUser)))

        val result = testController.updatePassword()(request)
        status(result) mustBe BAD_REQUEST
      }

      "the old password doesn't match the one stored" in new Setup {

        val set = PasswordSet("testOld","testNew","testNew")

        val request = FakeRequest()
          .withSession("cookieID" -> "testCookieID")
          .withFormUrlEncodedBody(
          "oldPassword" -> "testOld",
          "newPassword" -> "testNew",
          "confirmPassword" -> "testNew"
        )

        when(mockSessionStoreConnector.getDataElement[UserAccount](Matchers.any())(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(Some(testUser)))

        when(mockAccountConnector.updatePassword(Matchers.any()))
          .thenReturn(Future.successful(InvalidOldPassword))

        val result = testController.updatePassword()(request)
        status(result) mustBe BAD_REQUEST
      }
    }

    "return an ok" when {
      "the old password doesn't match the one stored" in new Setup {
        val set = PasswordSet("testOld","testNew","testNew")

        val request = FakeRequest()
          .withSession("cookieID" -> "testCookieID")
          .withFormUrlEncodedBody(
          "oldPassword" -> "testOld",
          "newPassword" -> "testNew",
          "confirmPassword" -> "testNew"
        )

        when(mockSessionStoreConnector.getDataElement[UserAccount](Matchers.any())(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(Some(testUser)))

        when(mockAccountConnector.updatePassword(Matchers.any()))
          .thenReturn(Future.successful(PasswordUpdated))

        val result = testController.updatePassword()(request)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/account-services/edit-your-profile#password")
      }
    }
  }

  "updateSettings" should {
    "redirect to login" when {
      "there is no active user session" in new Setup {
        val result = testController.updateSettings()(FakeRequest())
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/account-services/login")
      }
    }

    "return an InternalServerError" when {
      "there was a problem updating the user settings" in new Setup {
        val request = FakeRequest()
          .withSession("cookieID" -> "testCookieID")
          .withFormUrlEncodedBody(
            "displayName" -> "full",
            "displayNameColour" -> "#FFFFFF"
          )

        when(mockSessionStoreConnector.getDataElement[UserAccount](Matchers.any())(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(Some(testUser)))

        when(mockAccountConnector.updateSettings(Matchers.any()))
          .thenReturn(Future.successful(UpdatedSettingsFailed))

        val result = testController.updateSettings()(request)
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }

    "redirect to edit profile" when {
      "the user settings have been updated" in new Setup {
        val request = FakeRequest()
          .withSession(
            "cookieID" -> "testCookieID",
            "_id" -> "testID"
          ).withFormUrlEncodedBody(
            "displayName" -> "full",
            "displayNameColour" -> "#FFFFFF"
          )

        val settings = AccountSettings("testID", Map("displayName" -> "full", "displayNameColour" -> "#FFFFFF"))

        when(mockSessionStoreConnector.getDataElement[UserAccount](Matchers.any())(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(Some(testUser)))

        when(mockAccountConnector.updateSettings(Matchers.any()))
          .thenReturn(Future.successful(UpdatedSettingsSuccess))

        when(mockFeedEventService.accountSettingsFeedEvent(Matchers.any()))
          .thenReturn(Future.successful(FeedEventSuccessResponse))

        when(mockEditProfileService.updateSession(Matchers.any())(Matchers.any()))
          .thenReturn(Future.successful(true))

        val result = testController.updateSettings()(request)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/account-services/edit-your-profile")
      }
    }
  }
}
