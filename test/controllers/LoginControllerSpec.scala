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

import connectors.SessionStoreConnector
import controllers.traits.login.LoginCtrl
import mocks.MockResponse
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import org.mockito.Mockito._
import org.mockito.Matchers
import play.api.mvc.Session
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.LoginService

import scala.concurrent.Future

class LoginControllerSpec extends PlaySpec with OneAppPerSuite with MockitoSugar with MockResponse {

  val mockLoginService = mock[LoginService]
  val mockSessionStoreConnector = mock[SessionStoreConnector]

  val testSession = Session(testUserDetails.sessionMap)

  val successReponse = mockWSResponse(statusCode = CREATED)

  class Setup {
    class TestController extends LoginCtrl {
      val userLogin = mockLoginService
      val sessionStoreConnector = mockSessionStoreConnector
    }

    val testController = new TestController
  }

  "show" should {
    "return an OK" in new Setup {
      val result = testController.show("diagnostics")(FakeRequest())
      status(result) mustBe OK
    }
  }

  "submit" should {
    "return bad request" when {
      "neither inputs are filled" in new Setup {
        val request = FakeRequest().withFormUrlEncodedBody(
          "userName" -> "",
          "password" -> ""
        )

        val result = testController.submit()(request)
        status(result) mustBe BAD_REQUEST
      }

      "password isnt entered" in new Setup {
        val request = FakeRequest().withFormUrlEncodedBody(
          "userName" -> "testUserName",
          "password" -> ""
        )

        val result = testController.submit()(request)
        status(result) mustBe BAD_REQUEST
      }

      "username isnt entered" in new Setup {
        val request = FakeRequest().withFormUrlEncodedBody(
          "userName" -> "",
          "password" -> "testPassword"
        )

        val result = testController.submit()(request)
        status(result) mustBe BAD_REQUEST
      }
    }

    "return an OK" when {
      "given invalid user credentials" in new Setup {
        val request = FakeRequest().withFormUrlEncodedBody(
          "userName" -> "testUserName",
          "password" -> "testPassword"
        )

        when(mockLoginService.processLoginAttempt(Matchers.any()))
          .thenReturn(Future.successful(None))

        val result = testController.submit()(request)
        status(result) mustBe OK
      }
    }

    "return a SEE OTHER" when {
      "given a set of valid credentials" in new Setup {
        val request = FakeRequest(POST, "/testUrl?redirect=diagnostics").withFormUrlEncodedBody(
          "userName" -> "testUserName",
          "password" -> "testPassword"
        )

        when(mockLoginService.processLoginAttempt(Matchers.eq(testUserCredentials)))
          .thenReturn(Future.successful(Some((testSession, encryptedUserDetails))))

        when(mockSessionStoreConnector.cache(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(successReponse))

        val result = testController.submit()(request)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("http://localhost:9970/diagnostics")
      }
    }
  }
}
