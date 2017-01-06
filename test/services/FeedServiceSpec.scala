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

import connectors.{AccountConnector, FeedEventSuccessResponse}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import org.mockito.Mockito._
import org.mockito.Matchers
import play.api.test.FakeRequest

import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration._

class FeedServiceSpec extends PlaySpec with MockitoSugar with OneAppPerSuite {

  val mockAccountConnector : AccountConnector = mock[AccountConnector]

  class Setup {
    object TestService extends FeedService {
      val accountConnector : AccountConnector = mockAccountConnector
    }

    implicit val request = FakeRequest().withSession("_id" -> "testUserId")
  }

  "basicDetailsFeedEvent" should {
    "return a FeedEventResponse" in new Setup {
      when(mockAccountConnector.createFeedItem(Matchers.any()))
        .thenReturn(Future.successful(FeedEventSuccessResponse))

      val result = Await.result(TestService.basicDetailsFeedEvent, 5.seconds)

      result mustBe FeedEventSuccessResponse
    }
  }

  "passwordUpdateFeedEvent" should {
    "return a FeedEventResponse" in new Setup {
      when(mockAccountConnector.createFeedItem(Matchers.any()))
        .thenReturn(Future.successful(FeedEventSuccessResponse))

      val result = Await.result(TestService.passwordUpdateFeedEvent, 5.seconds)

      result mustBe FeedEventSuccessResponse
    }
  }

  "accountSettingsFeedEvent" should {
    "return a FeedEventResponse" in new Setup {
      when(mockAccountConnector.createFeedItem(Matchers.any()))
        .thenReturn(Future.successful(FeedEventSuccessResponse))

      val result = Await.result(TestService.accountSettingsFeedEvent, 5.seconds)

      result mustBe FeedEventSuccessResponse
    }
  }
}
