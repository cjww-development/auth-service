/*
 * Copyright 2019 CJWW Development
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
import com.cjwwdev.featuremanagement.services.FeatureService
import connectors.SessionStoreConnector
import controllers.login.LoginController
import enums.SessionCache
import helpers.controllers.ControllerSpec
import play.api.mvc.ControllerComponents
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.LoginService

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits

class LoginControllerSpec extends ControllerSpec {

  class Setup(deversity: Boolean = false) {
    val testController = new LoginController {
      override protected val diagnosticsFrontend: String        = "/test/diagnostics"
      override protected val deversityFrontend: String          = "/test/deversity"
      override protected val hubFrontend: String                = "/test/hub"
      override val featureService: FeatureService               = mockFeatureService
      override implicit val ec: ExecutionContext                = Implicits.global
      override def deversityEnabled: Boolean                    = deversity
      override val controllerComponents: ControllerComponents   = stubControllerComponents()
      override val loginService: LoginService                   = mockLoginService
      override val sessionStoreConnector: SessionStoreConnector = mockSessionStoreConnector
      override val authConnector: AuthConnector                 = mockAuthConnector
    }
  }

  "show" should {
    "return an Ok" in new Setup {
      runActionWithoutAuth(testController.show(redirect = None), addCSRFToken(FakeRequest())) {
        status(_) mustBe OK
      }
    }
  }

  "submit" should {
    val request = FakeRequest().withFormUrlEncodedBody(
      "userName" -> "testUserName",
      "password" -> "testPassword"
    )

    "return a BadRequest" when {
      "the form could not be validated" in new Setup {
        runActionWithoutAuth(testController.submit, addCSRFToken(FakeRequest())) {
          status(_) mustBe BAD_REQUEST
        }
      }
    }

    "return an Ok" when {
      "the credentials could not be validated" in new Setup {
        mockProcessIndividualLoginAttempt(success = false)

        runActionWithoutAuth(testController.submit, addCSRFToken(request)) {
          status(_) mustBe OK
        }
      }
    }

    "return a SeeOther" when {
      "the credentials were validated" in new Setup {
        mockProcessIndividualLoginAttempt(success = true)

        runActionWithoutAuth(testController.submit, addCSRFToken(request)) { res =>
          status(res) mustBe SEE_OTHER
          assert(redirectLocation(res).get.contains("/session/session-"))
        }
      }
    }
  }

  "activateAuthServiceSession" should {
    "return a SeeOther and redirect to deversity frontend" in new Setup {
      runActionWithoutAuth(testController.activateAuthServiceSession("testCookieId"), addCSRFToken(FakeRequest())) { res =>
        status(res)           mustBe SEE_OTHER
        redirectLocation(res) mustBe Some("/where-do-you-want-to-go")
      }
    }

    "return a SeeOther and redirect to the service director page" in new Setup(deversity = true) {
      runActionWithoutAuth(testController.activateAuthServiceSession("testCookieId"), addCSRFToken(FakeRequest())) { res =>
        status(res)           mustBe SEE_OTHER
        redirectLocation(res) mustBe Some("/test/deversity/private/build-deversity-session/testCookieId")
      }
    }
  }

  "redirectToServiceSelector" should {
    "return a SeeOther" in new Setup {
      runActionWithoutAuth(testController.redirectToServiceSelector, addCSRFToken(FakeRequest())) { res =>
        status(res)           mustBe SEE_OTHER
        redirectLocation(res) mustBe Some(controllers.redirect.routes.RedirectController.chooseService().url)
      }
    }
  }

  "signOut" should {
    "return a SeeOther" in new Setup {

      mockDestroySession(cacheValue = SessionCache.cacheDestroyed)

      runActionWithAuth(testController.signOut, addCSRFToken(request), "individual") { res =>
        status(res)           mustBe SEE_OTHER
        redirectLocation(res) mustBe Some(controllers.login.routes.LoginController.show(None).url)
      }
    }
  }
}
