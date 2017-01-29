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

import config.FrontendConfiguration
import mocks.CJWWSpec
import models.SessionUpdateSet
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.security.DataSecurity
import play.api.test.FakeApplication

import scala.concurrent.Future

class SessionStoreConnectorSpec extends CJWWSpec {

  val mockFrontendConfig = mock[FrontendConfiguration]

  val testConnector = new SessionStoreConnector(mockHttpVerbs, mockFrontendConfig)

  val testResponse = mockWSResponse(OK)
  val insResp = mockWSResponse(INTERNAL_SERVER_ERROR)
  val testRespBody = mockWSResponseWithBody(DataSecurity.encryptData[String]("testData").get)

  val testSessionUpdate = SessionUpdateSet("testKey","testData")

  "cache" should {
    "return a WSResponse" in {
      when(mockHttpVerbs.post(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(testResponse))

      val result = await(testConnector.cache[String]("user-1234567890","testData"))
      result mustBe testResponse
    }
  }

  "getDataElement" should {
    "return an optional string" in {
      implicit val request = FakeRequest().withSession("cookieId" -> "session-1234567890")

      when(mockHttpVerbs.get[String](ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(testRespBody))

      val result = await(testConnector.getDataElement[String]("testKey"))
      result mustBe Some("testData")
    }
  }

  "updateSession" should {
    "return a true" in {
      implicit val request = FakeRequest().withSession("cookieId" -> "session-1234567890")

      when(mockHttpVerbs.post(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(testResponse))

      val result = await(testConnector.updateSession(testSessionUpdate))
      result mustBe true
    }

    "return a false" in {

      implicit val request = FakeRequest().withSession("cookieId" -> "session-1234567890")

      when(mockHttpVerbs.post(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(insResp))

      val result = await(testConnector.updateSession(testSessionUpdate))
      result mustBe false
    }
  }

  "destroySession" should {
    "return a WSResponse" in {
      when(mockHttpVerbs.get[String](ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(testResponse))

      val result = await(testConnector.destroySession("session-1234567890"))
      result mustBe testResponse
    }
  }
}
