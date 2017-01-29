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

import connectors.{UserLoginFailedResponse, UserLoginSuccessResponse}
import mocks.CJWWSpec
import models.UserLogin
import models.auth.AuthContextDetails
import play.api.mvc.Session
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers
import play.api.test.Helpers.OK

import scala.concurrent.Future

class LoginServiceSpec extends CJWWSpec {

  val testService = new LoginService(mockUserLoginConnector, mockSessionStoreConnector)

  "processLoginAttempt" should {

    val testCredentials = UserLogin("testUserName","testPass")
    val testResp = mockWSResponse(OK)
    val testSession = Session(testContextDetails.sessionMap)

    "return no session if the user can't be validated" in {
      when(mockUserLoginConnector.getUser(ArgumentMatchers.any()))
        .thenReturn(Future.successful(UserLoginFailedResponse))

      val result = await(testService.processLoginAttempt(testCredentials))
      result mustBe None
    }

    "return a populated session if the user can be successfully validated" in {
      when(mockUserLoginConnector.getUser(ArgumentMatchers.any()))
        .thenReturn(Future.successful(UserLoginSuccessResponse(testContextDetails)))

      when(mockSessionStoreConnector.cache[String](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(testResp))

      val result = await(testService.processLoginAttempt(testCredentials))
      result.get("contextId") mustBe "context-1234567890"
      result.get("firstName") mustBe "testFirstName"
      result.get("lastName") mustBe "testLastName"
    }
  }
}
