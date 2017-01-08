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
import models.accounts.UserRegister
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import org.mockito.Mockito._
import org.mockito.Matchers
import utils.httpverbs.HttpVerbs
import play.api.test.Helpers._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class UserRegistrationConnectorSpec extends PlaySpec with OneAppPerSuite with MockitoSugar with MockResponse {

  val mockHttp = mock[HttpVerbs]

  val successResponse = mockWSResponse(statusCode = CREATED)
  val clientResponse = mockWSResponse(statusCode = BAD_REQUEST)
  val serverResponse = mockWSResponse(statusCode = INTERNAL_SERVER_ERROR)
  val errorResponse = mockWSResponse(statusCode = 700)

  val trueResponse = mockWSResponseWithBody("true")
  val falseResponse = mockWSResponseWithBody("false")

  val testNewUser = UserRegister("testFirstName","testLastName","testUserName","testEmail","testPassword","testPassword")

  class Setup {
    object TestConnector extends UserRegistrationConnector {
      val http = mockHttp
    }
  }

  "createNewIndividualUser" should {
    "return a UserRegisterSuccessResponse" in new Setup {
      when(mockHttp.post[UserRegister](Matchers.any(), Matchers.any(), Matchers.any())(Matchers.any()))
        .thenReturn(Future.successful(successResponse))

      val result = TestConnector.createNewIndividualUser(testNewUser)

      Await.result(result, 5.seconds) mustBe UserRegisterSuccessResponse(CREATED)
    }

    "return a UserRegisterClientErrorResponse" in new Setup {
      when(mockHttp.post[UserRegister](Matchers.any(), Matchers.any(), Matchers.any())(Matchers.any()))
        .thenReturn(Future.successful(clientResponse))

      val result = TestConnector.createNewIndividualUser(testNewUser)

      Await.result(result, 5.seconds) mustBe UserRegisterClientErrorResponse(BAD_REQUEST)
    }

    "return a UserRegisterServerErrorResponse" in new Setup {
      when(mockHttp.post[UserRegister](Matchers.any(), Matchers.any(), Matchers.any())(Matchers.any()))
        .thenReturn(Future.successful(serverResponse))

      val result = TestConnector.createNewIndividualUser(testNewUser)

      Await.result(result, 5.seconds) mustBe UserRegisterServerErrorResponse(INTERNAL_SERVER_ERROR)
    }

    "return a UserRegisterErrorResponse" in new Setup {
      when(mockHttp.post[UserRegister](Matchers.any(), Matchers.any(), Matchers.any())(Matchers.any()))
        .thenReturn(Future.successful(errorResponse))

      val result = TestConnector.createNewIndividualUser(testNewUser)

      Await.result(result, 5.seconds) mustBe UserRegisterErrorResponse(700)
    }
  }

  "checkUserName" should {
    "return true" when {
      "the given user name is in use" in new Setup {
        when(mockHttp.get[String](Matchers.any(), Matchers.any(), Matchers.any())(Matchers.any()))
          .thenReturn(Future.successful(trueResponse))

        val result = Await.result(TestConnector.checkUserName("testUserName"), 5.seconds)
        result mustBe true
      }
    }

    "return false" when {
      "the given user name is not in use" in new Setup {
        when(mockHttp.get[String](Matchers.any(), Matchers.any(), Matchers.any())(Matchers.any()))
          .thenReturn(Future.successful(falseResponse))

        val result = Await.result(TestConnector.checkUserName("testUserName"), 5.seconds)
        result mustBe false
      }
    }
  }

  "checkEmailAddress" should {
    "return true" when {
      "the given email address is in use" in new Setup {
        when(mockHttp.get[String](Matchers.any(), Matchers.any(), Matchers.any())(Matchers.any()))
          .thenReturn(Future.successful(trueResponse))

        val result = Await.result(TestConnector.checkEmailAddress("test@email.com"), 5.seconds)
        result mustBe true
      }
    }

    "return false" when {
      "the given email address is not in use" in new Setup {
        when(mockHttp.get[String](Matchers.any(), Matchers.any(), Matchers.any())(Matchers.any()))
          .thenReturn(Future.successful(falseResponse))

        val result = Await.result(TestConnector.checkEmailAddress("test@email.com"), 5.seconds)
        result mustBe false
      }
    }
  }
}
