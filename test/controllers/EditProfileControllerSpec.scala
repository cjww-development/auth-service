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

import connectors.{AccountConnector, SessionStoreConnector}
import controllers.traits.user.EditProfileCtrl
import models.accounts.UserAccount
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import org.mockito.Mockito._
import org.mockito.Matchers
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class EditProfileControllerSpec extends PlaySpec with OneAppPerSuite with MockitoSugar {

  val mockAccountConnector = mock[AccountConnector]
  val mockSessionStoreConnector = mock[SessionStoreConnector]

  val testUser = UserAccount(Some("testUserId"),"testFirstName","testLastName","testUserName","testEmail","testPassword",None,None)

  class Setup {
    class TestController extends EditProfileCtrl {
      val sessionStoreConnector = mockSessionStoreConnector
      val accountConnector = mockAccountConnector
    }

    val testController = new TestController
  }

  "show" should {
    "return an OK" in new Setup {
      when(mockSessionStoreConnector.getDataElement[UserAccount](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Some(testUser)))

      val result = testController.show()(FakeRequest().withSession("cookieID" -> "testUserId"))
      status(result) mustBe OK
    }
  }
}
