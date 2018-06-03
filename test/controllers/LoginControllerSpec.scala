/*
 * Copyright 2018 CJWW Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import com.cjwwdev.auth.connectors.AuthConnector
import connectors.SessionStoreConnector
import controllers.login.LoginController
import enums.SessionCache
import helpers.controllers.ControllerSpec
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.LoginService

class LoginControllerSpec extends ControllerSpec {

  class Setup {
    val testController = new LoginController {
      override val controllerComponents: ControllerComponents           = stubControllerComponents()
      override val loginFailed: String                                  = "testMessage"
      override val loginService: LoginService                           = mockLoginService
      override val sessionStoreConnector: SessionStoreConnector         = mockSessionStoreConnector
      override def authConnector: AuthConnector                         = mockAuthConnector
    }
  }

  "show" ignore {
    "return an Ok" in new Setup {
      runActionWithoutAuth(testController.show(redirect = None), FakeRequest()) {
        status(_) mustBe OK
      }
    }
  }

  "submit" ignore {
    val request = FakeRequest().withFormUrlEncodedBody(
      "userName" -> "testUserName",
      "password" -> "testPassword"
    )

    "return a BadRequest" when {
      "the form could not be validated" in new Setup {
        runActionWithoutAuth(testController.submit, FakeRequest()) {
          status(_) mustBe BAD_REQUEST
        }
      }
    }

    "return an Ok" when {
      "the credentials could not be validated" in new Setup {
        mockProcessIndividualLoginAttempt(success = false)

        runActionWithoutAuth(testController.submit, request) {
          status(_) mustBe OK
        }
      }
    }

    "return a SeeOther" when {
      "the credentials were validated" in new Setup {
        mockProcessIndividualLoginAttempt(success = true)

        runActionWithoutAuth(testController.submit, request) { res =>
          status(res) mustBe SEE_OTHER
          assert(redirectLocation(res).get.contains("/account-services/session/session-"))
        }
      }
    }
  }

  "activateAuthServiceSession" ignore {
    "return a SeeOther" in new Setup {
      runActionWithoutAuth(testController.activateAuthServiceSession("testCookieId"), FakeRequest()) { res =>
        status(res)           mustBe SEE_OTHER
        redirectLocation(res) mustBe Some("http://localhost:9986/deversity/private/build-deversity-session/testCookieId")
      }
    }
  }

  "redirectToServiceSelector" ignore {
    "return a SeeOther" in new Setup {
      runActionWithoutAuth(testController.redirectToServiceSelector, FakeRequest()) { res =>
        status(res)           mustBe SEE_OTHER
        redirectLocation(res) mustBe Some(controllers.redirect.routes.RedirectController.chooseService().url)
      }
    }
  }

  "signOut" ignore {
    "return a SeeOther" in new Setup {

      mockDestroySession(cacheValue = SessionCache.cacheDestroyed)

      runActionWithAuth(testController.signOut, request, "individual") { res =>
        status(res)           mustBe SEE_OTHER
        redirectLocation(res) mustBe Some(controllers.login.routes.LoginController.show(None).url)
      }
    }
  }
}
