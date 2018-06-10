
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

package controllers.test

import helpers.controllers.ControllerSpec
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._

class FeatureControllerSpec extends ControllerSpec {

  class Setup {
    val testController = new FeatureController {
      override val featureService       = mockFeatureService
      override val controllerComponents = stubControllerComponents()
    }
  }

  "show" should {
    "return an OK" in new Setup {

      mockGetAllBooleanFeatureStates()

      assertFutureResult(testController.show()(addCSRFToken(request))) { result =>
        status(result) mustBe OK
      }
    }
  }

  "submit" should {
    "return an Ok" when {
      "the controller method has bound the data" in new Setup {
        val formRequest = request.withFormUrlEncodedBody(
          "featureState[0]" -> "true",
          "featureState[1]" -> "false",
          "featureState[2]" -> "false"
        )

        assertFutureResult(testController.submit()(addCSRFToken(formRequest))) { result =>
          status(result)           mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(routes.FeatureController.show().url)
        }
      }
    }
  }
}