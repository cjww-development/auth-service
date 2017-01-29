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

import config.FrontendConfiguration
import connectors.{AccountConnector, FeedEventFailedResponse, FeedEventSuccessResponse}
import mocks.CJWWSpec
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers

import scala.concurrent.Future

class FeedServiceSpec extends CJWWSpec {

  val mockAccountConnector = mock[AccountConnector]
  val mockFrontendConfig = mock[FrontendConfiguration]

  val testService = new FeedService(mockAccountConnector, mockFrontendConfig)

  "buildFeedItem" should {
    "return a populated feed item" in {
      val result = testService.buildFeedItem("testLocation", "testDescription")

      result.userId mustBe "user-766543"
      result.sourceDetail.location mustBe "testLocation"
      result.eventDetail.description mustBe "testDescription"
    }
  }

  "basicDetailsFeedEvent" should {
    "return a FeedEventSuccessResponse" in {
      when(mockAccountConnector.createFeedItem(ArgumentMatchers.any()))
        .thenReturn(Future.successful(FeedEventSuccessResponse))

      val result = await(testService.basicDetailsFeedEvent)
      result mustBe FeedEventSuccessResponse
    }

    "return a FeedEventFailedResponse" in {
      when(mockAccountConnector.createFeedItem(ArgumentMatchers.any()))
        .thenReturn(Future.successful(FeedEventFailedResponse))

      val result = await(testService.basicDetailsFeedEvent)
      result mustBe FeedEventFailedResponse
    }
  }

  "passwordUpdateFeedEvent" should {
    "return a FeedEventSuccessResponse" in {
      when(mockAccountConnector.createFeedItem(ArgumentMatchers.any()))
        .thenReturn(Future.successful(FeedEventSuccessResponse))

      val result = await(testService.passwordUpdateFeedEvent)
      result mustBe FeedEventSuccessResponse
    }

    "return a FeedEventFailedResponse" in {
      when(mockAccountConnector.createFeedItem(ArgumentMatchers.any()))
        .thenReturn(Future.successful(FeedEventFailedResponse))

      val result = await(testService.passwordUpdateFeedEvent)
      result mustBe FeedEventFailedResponse
    }
  }

  "accountSettingsFeedEvent" should {
    "return a FeedEventSuccessResponse" in {
      when(mockAccountConnector.createFeedItem(ArgumentMatchers.any()))
        .thenReturn(Future.successful(FeedEventSuccessResponse))

      val result = await(testService.accountSettingsFeedEvent)
      result mustBe FeedEventSuccessResponse
    }

    "return a FeedEventFailedResponse" in {
      when(mockAccountConnector.createFeedItem(ArgumentMatchers.any()))
        .thenReturn(Future.successful(FeedEventFailedResponse))

      val result = await(testService.accountSettingsFeedEvent)
      result mustBe FeedEventFailedResponse
    }
  }

  "processRetrievedList" should {
    "return an optional feed event list" in {
      when(mockAccountConnector.getFeedItems(ArgumentMatchers.any()))
        .thenReturn(Future.successful(None))

      val result = await(testService.processRetrievedList("user-1234567890"))
      result mustBe None
    }
  }
}
