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
import models.accounts.UserRegister
import play.api.test.Helpers._
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers

import scala.concurrent.Future

class UserRegistrationConnectorSpec extends CJWWSpec {

  val mockFrontendConfig = mock[FrontendConfiguration]

  val testConnector = new UserRegistrationConnector(mockHttpVerbs, mockFrontendConfig)

  val ERROR_CODE = 700

  "processStatusCode" should {
    "return a UserRegisterSuccessResponse" in {
      val result = testConnector.processStatusCode(OK)
      result mustBe UserRegisterSuccessResponse(OK)
    }

    "return a UserRegisterClientErrorResponse" in {
      val result = testConnector.processStatusCode(BAD_REQUEST)
      result mustBe UserRegisterClientErrorResponse(BAD_REQUEST)
    }

    "return a UserRegisterServerErrorResponse" in {
      val result = testConnector.processStatusCode(INTERNAL_SERVER_ERROR)
      result mustBe UserRegisterServerErrorResponse(INTERNAL_SERVER_ERROR)
    }

    "return a UserRegisterErrorResponse" in {
      val result = testConnector.processStatusCode(ERROR_CODE)
      result mustBe UserRegisterErrorResponse(ERROR_CODE)
    }
  }

  "createNewIndividualUser" should {

    val testUserRegister =
      UserRegister(
        "testFirstName",
        "testLastName",
        "testUserName",
        "test@email.com",
        "testPass",
        "testPass"
      )

    "return a UserRegisterSuccessResponse" in {
      val okResp = mockWSResponse(OK)

      when(mockHttpVerbs.post(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(okResp))

      val result = await(testConnector.createNewIndividualUser(testUserRegister))
      result mustBe UserRegisterSuccessResponse(OK)
    }

    "return a UserRegisterClientErrorResponse" in {
      val badRequestResp = mockWSResponse(BAD_REQUEST)

      when(mockHttpVerbs.post(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(badRequestResp))

      val result = await(testConnector.createNewIndividualUser(testUserRegister))
      result mustBe UserRegisterClientErrorResponse(BAD_REQUEST)
    }

    "return a UserRegisterServerErrorResponse" in {
      val insResp = mockWSResponse(INTERNAL_SERVER_ERROR)

      when(mockHttpVerbs.post(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(insResp))

      val result = await(testConnector.createNewIndividualUser(testUserRegister))
      result mustBe UserRegisterServerErrorResponse(INTERNAL_SERVER_ERROR)
    }

    "return a UserRegisterErrorResponse" in {
      val errorResp = mockWSResponse(ERROR_CODE)

      when(mockHttpVerbs.post(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(errorResp))

      val result = await(testConnector.createNewIndividualUser(testUserRegister))
      result mustBe UserRegisterErrorResponse(ERROR_CODE)
    }
  }

  "checkUserName" should {
    "return false" in {
      val okResp = mockWSResponse(OK)

      when(mockHttpVerbs.getUrl(ArgumentMatchers.any()))
        .thenReturn(Future.successful(okResp))

      val result = await(testConnector.checkUserName("testUserName"))
      result mustBe false
    }

    "return true" in {
      val conflictResp = mockWSResponse(CONFLICT)

      when(mockHttpVerbs.getUrl(ArgumentMatchers.any()))
        .thenReturn(Future.successful(conflictResp))

      val result = await(testConnector.checkUserName("testUserName"))
      result mustBe true
    }
  }

  "checkEmailAddress" should {
    "return false" in {
      val okResp = mockWSResponse(OK)

      when(mockHttpVerbs.getUrl(ArgumentMatchers.any()))
        .thenReturn(Future.successful(okResp))

      val result = await(testConnector.checkEmailAddress("testUserName"))
      result mustBe false
    }

    "return true" in {
      val conflictResp = mockWSResponse(CONFLICT)

      when(mockHttpVerbs.getUrl(ArgumentMatchers.any()))
        .thenReturn(Future.successful(conflictResp))

      val result = await(testConnector.checkEmailAddress("testUserName"))
      result mustBe true
    }
  }
}
