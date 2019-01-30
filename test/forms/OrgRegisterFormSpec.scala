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

class OrgRegisterFormSpec extends PlaySpec {
  "OrgRegisterForm" should {
    "bind fully" when {
      "the form is filled with valid data" in {
        val testOrgRegister = Map(
          "orgName"         -> "TestOrgName",
          "initials"        -> "TI",
          "orgUserName"     -> "tUserName",
          "location"        -> "TestLocation",
          "orgEmail"        -> "test@email.com",
          "password"        -> "testPassword123",
          "confirmPassword" -> "testPassword123"
        )

        val result = OrgRegisterForm.orgRegisterForm.bind(testOrgRegister)
        result.errors.isEmpty mustBe true
      }
    }

    "have a full list of errors" when {
      "no data has been input" in {
        val testOrgRegister = Map(
          "orgName"         -> "",
          "initials"        -> "",
          "orgUserName"     -> "",
          "location"        -> "",
          "orgEmail"        -> "",
          "password"        -> "",
          "confirmPassword" -> ""
        )

        val result = OrgRegisterForm.orgRegisterForm.bind(testOrgRegister)
        result.errors mustBe List(
          FormError("orgName", List("You have not entered a valid organisation name")),
          FormError("initials", List("Enter five characters or less for your initials")),
          FormError("orgUserName", List("You have not entered a valid user name")),
          FormError("location", List("You have not entered a valid location")),
          FormError("orgEmail", List("You have not entered a valid email address")),
          FormError("password", List("You have not entered your password")),
          FormError("confirmPassword", List("You have not confirmed your password"))
        )
      }
    }

    "have some errors" when {
      "the passwords don't match" in {
        val testOrgRegister = Map(
          "orgName"         -> "TestOrgName",
          "initials"        -> "TI",
          "orgUserName"     -> "tUserName",
          "location"        -> "TestLocation",
          "orgEmail"        -> "test@email.com",
          "password"        -> "testPassword123",
          "confirmPassword" -> "testPassword12"
        )

        val result = OrgRegisterForm.orgRegisterForm.bind(testOrgRegister)
        result.errors mustBe List(
          FormError("", List("The passwords you have entered don't match"))
        )
      }
    }
  }
}
