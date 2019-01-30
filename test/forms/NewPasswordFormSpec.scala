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

class NewPasswordFormSpec extends PlaySpec {
  "NewPasswordForm" should {
    "bind fully" when {
      "the form is filled with valid data" in {
        val testNewPasswords = Map(
          "oldPassword"     -> "testOldPassword123",
          "newPassword"     -> "testPassword123",
          "confirmPassword" -> "testPassword123"
        )

        val result = NewPasswordForm.form.bind(testNewPasswords)
        result.errors.isEmpty mustBe true
      }
    }

    "have a full list of errors" when {
      "no data has been input" in {
        val testNewPasswords = Map(
          "oldPassword"     -> "",
          "newPassword"     -> "",
          "confirmPassword" -> ""
        )

        val result = NewPasswordForm.form.bind(testNewPasswords)

        result.error("oldPassword").get.message mustBe "You have not entered your old password"
        result.error("newPassword").get.message mustBe "You have not entered your password"
        result.error("confirmPassword").get.message mustBe "You have not confirmed your password"
      }
    }

    "have some errors" when {
      "the passwords don't match" in {
        val testNewPasswords = Map(
          "oldPassword"     -> "testOldPassword123",
          "newPassword"     -> "testPassword123",
          "confirmPassword" -> "testPassword12"
        )

        val result = NewPasswordForm.form.bind(testNewPasswords)
        result.errors mustBe List(
          FormError("", List("The passwords you have entered don't match"))
        )
      }
    }
  }
}
