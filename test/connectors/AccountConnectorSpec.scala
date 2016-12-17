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
package connectors

import mocks.MockResponse
import models.accounts.{AccountSettings, PasswordSet, UserProfile}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import org.mockito.Mockito._
import org.mockito.Matchers
import play.api.test.Helpers._
import utils.httpverbs.HttpVerbs

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class AccountConnectorSpec extends PlaySpec with OneAppPerSuite with MockitoSugar with MockResponse {

  val mockHttp = mock[HttpVerbs]

  val successResponse = mockWSResponse(OK)

  val testProfile = UserProfile("testFirstName","testLastName","testUserName","test@email.com")
  val testPassSet = PasswordSet("testUserId","testOldPassword","testNewPassword")
  val testAccSettings = AccountSettings("testUserId", Map("displayName" -> "testValue"))

  class Setup {
    object TestConnector extends AccountConnector {
      val http = mockHttp
    }
  }

  "updateProfile" should {
    "return the http response status code" when {
      "given a set of user profile information" in new Setup {
        when(mockHttp.updateProfile(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(successResponse))

        val result = Await.result(TestConnector.updateProfile(testProfile), 5.seconds)
        result mustBe OK
      }
    }
  }

  "updatePassword" should {
    "return the http response status code" when {
      "given a PasswordSet" in new Setup {
        when(mockHttp.updatePassword(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(successResponse))

        val result = Await.result(TestConnector.updatePassword(testPassSet), 5.seconds)
        result mustBe PasswordUpdated
      }
    }
  }

  "updateSettings" should {
    "return the http response status code" when {
      "given a set of AccSettings" in new Setup {
        when(mockHttp.updateSettings(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(successResponse))

        val result = Await.result(TestConnector.updateSettings(testAccSettings), 5.seconds)
        result mustBe UpdatedSettingsSuccess
      }
    }
  }
}
