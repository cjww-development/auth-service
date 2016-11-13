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

import connectors.{SessionStoreConnector, UserLoginConnector}
import mocks.MockResponse
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.mvc.Session
import play.api.test.Helpers._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class LoginServiceSpec extends PlaySpec with OneAppPerSuite with MockitoSugar with MockResponse {

  val mockConnector = mock[UserLoginConnector]
  val mockSessionStoreConnector = mock[SessionStoreConnector]

  final val testSession = Session(testUserDetails.sessionMap)

  val successResponse = mockWSResponse(statusCode = CREATED)

  class Setup {
    object TestService extends LoginService {
      val userLogin = mockConnector
      val sessionStoreConnector = mockSessionStoreConnector
    }
  }

  "processLoginAttempt" should {
    "return a tuple containing a session and an optional string" when {
      "given valid credentials" in new Setup {
        when(mockConnector.getUserAccountInformation(Matchers.any()))
          .thenReturn(Future.successful(Some(testUserDetails)))

        when(mockSessionStoreConnector.cache(Matchers.any(), Matchers.any())(Matchers.any()))
          .thenReturn(Future.successful(successResponse))

        val result = Await.result(TestService.processLoginAttempt(testUserCredentials), 5.seconds)
        result.get("_id") mustBe "testID"
      }
    }

    "return none" when {
      "given invalid credentials" in new Setup {
        when(mockConnector.getUserAccountInformation(Matchers.any()))
          .thenReturn(Future.successful(None))

        val result = Await.result(TestService.processLoginAttempt(testUserCredentials), 5.seconds)
        result mustBe None
      }
    }
  }
}
