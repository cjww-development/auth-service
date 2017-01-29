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
import models.UserLogin
import models.auth.{AuthContext, AuthContextDetails}
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers
import utils.security.DataSecurity

import scala.concurrent.Future

class UserLoginConnectorSpec extends CJWWSpec {

  val mockFrontendConfig = mock[FrontendConfiguration]

  val testConnector = new UserLoginConnector(mockHttpVerbs, mockFrontendConfig)

  val successResponse = mockWSResponseWithBody(DataSecurity.encryptData[AuthContextDetails](testContextDetails).get)
  val failedResponse = mockWSResponseWithBody("INVALID_PAYLOAD")

  "getUser" should {
    "return a UserLoginSuccessResponse" in {
      when(mockHttpVerbs.getUrl(ArgumentMatchers.any()))
        .thenReturn(Future.successful(successResponse))

      val result = await(testConnector.getUser(UserLogin("testUserName","testPass")))
      result mustBe UserLoginSuccessResponse(testContextDetails)
    }

    "return a UserLoginFailedResponse" in {
      when(mockHttpVerbs.getUrl(ArgumentMatchers.any()))
        .thenReturn(Future.successful(failedResponse))

      val result = await(testConnector.getUser(UserLogin("testUserName","testPass")))
      result mustBe UserLoginFailedResponse
    }
  }
}
