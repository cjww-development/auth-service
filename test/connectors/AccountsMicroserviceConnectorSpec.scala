// Copyright (C) 2016-2017 the original author or authors.
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

import com.cjwwdev.http.exceptions.{ConflictException, ServerErrorException}
import common.{InvalidOldPassword, PasswordUpdated}
import enums.HttpResponse
import mocks.CJWWSpec
import models.accounts.{PasswordSet, Settings, UserProfile}
import models.feed.FeedItem
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class AccountsMicroserviceConnectorSpec extends CJWWSpec {

  class Setup {
    implicit val request = FakeRequest()
    val testConnector = new AccountsMicroserviceConnector {
      override val http = mockHttpVerbs
    }
  }

  "updateProfile" should {
    "return a HttpResponse success" when {
      "a users profile has been successfully updated" in new Setup {
        when(mockHttpVerbs.PATCH[UserProfile](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(fakeHttpResponse(OK)))

        val result = await(testConnector.updateProfile(testProfile))
        result mustBe HttpResponse.success
      }
    }

    "return a HttpResponse failed" when {
      "there was a problem updating the users profile" in new Setup {
        when(mockHttpVerbs.PATCH[UserProfile](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.failed(new ServerErrorException("test message", 500)))

        val result = await(testConnector.updateProfile(testProfile))
        result mustBe HttpResponse.failed
      }
    }
  }

  "updatePassword" should {
    "return a PasswordUpdated" when {
      "the users passwords has been updated" in new Setup {
        when(mockHttpVerbs.PATCH[PasswordSet](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(fakeHttpResponse(OK)))

        val result = await(testConnector.updatePassword(testPasswordSet))
        result mustBe PasswordUpdated
      }
    }

    "return a InvalidOldPassword" when {
      "the old password didn't match what is stored" in new Setup {
        when(mockHttpVerbs.PATCH[PasswordSet](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.failed(new ConflictException("test message")))

        val result = await(testConnector.updatePassword(testPasswordSet))
        result mustBe InvalidOldPassword
      }
    }
  }

  "updateSettings" should {
    "return a HttpResponse success" when {
      "the users settings have been updated" in new Setup {
        when(mockHttpVerbs.PATCH[Settings](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(fakeHttpResponse(OK)))

        val result = await(testConnector.updateSettings(testSettings))
        result mustBe HttpResponse.success
      }
    }

    "return a HttpResponse failed" when {
      "there was a problem updating the users settings" in new Setup {
        when(mockHttpVerbs.PATCH[Settings](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.failed(new ServerErrorException("test message", 500)))

        val result = await(testConnector.updateSettings(testSettings))
        result mustBe HttpResponse.failed
      }
    }
  }

  "createFeedItem" should {
    "return a successful http response" in new Setup {
      when(mockHttpVerbs.POST[FeedItem](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(fakeHttpResponse(OK)))

      val result = await(testConnector.createFeedItem(testFeedItem))
      result mustBe HttpResponse.success
    }
  }
}
