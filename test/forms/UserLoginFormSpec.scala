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

package forms

import org.scalatestplus.play.PlaySpec
import play.api.data.FormError

class UserLoginFormSpec extends PlaySpec {
  "UserLoginForm" should {
    "bind fully" when {
      "the form is filled with valid data" in {
        val testUserLogin = Map(
          "userName" -> "tUserName",
          "password" -> "testPassword123"
        )

        val result = UserLoginForm.loginForm.bind(testUserLogin)
        result.errors.isEmpty mustBe true
      }
    }

    "have a full list of errors" when {
      "no data has been input" in {
        val testUserLogin = Map(
          "userName" -> "",
          "password" -> ""
        )

        val result = UserLoginForm.loginForm.bind(testUserLogin)
        result.errors mustBe List(
          FormError("userName", List("You have not entered your user name")),
          FormError("password", List("You have not entered your password"))
        )
      }
    }
  }
}
