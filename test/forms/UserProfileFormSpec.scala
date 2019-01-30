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

class UserProfileFormSpec extends PlaySpec {
  "UserProfileForm" should {
    "bind fully" when {
      "the form is filled with valid data" in {
        val testProfile = Map(
          "firstName" -> "TestFirstName",
          "lastName"  -> "TestLastName",
          "userName"  -> "tUserName",
          "email"     -> "test@email.com"
        )

        val result = UserProfileForm.form.bind(testProfile)
        result.errors.isEmpty mustBe true
      }
    }

    "have a full list of errors" when {
      "no data has been input" in {
        val testProfile = Map(
          "firstName" -> "",
          "lastName"  -> "",
          "userName"  -> "",
          "email"     -> ""
        )

        val result = UserProfileForm.form.bind(testProfile)
        result.errors mustBe List(
          FormError("firstName", List("You have not entered a valid first name")),
          FormError("lastName", List("You have not entered a valid last name")),
          FormError("userName", List("You have not entered a valid user name")),
          FormError("email", List("You have not entered a valid email address"))
        )
      }
    }
  }
}
