// Copyright (C) 2016-2017 the original author or authors.
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

package forms

import org.scalatestplus.play.PlaySpec
import play.api.data.FormError

class UserRegisterFormSpec extends PlaySpec {
  "UserRegisterForm" should {
    "bind fully" when {
      "the form is filled with valid data" in {
        val testUserRegistration = Map(
          "firstName"       -> "Testfirstname",
          "lastName"        -> "Testsurname",
          "userName"        -> "tUserName",
          "email"           -> "test@email.com",
          "password"        -> "testPassword123",
          "confirmPassword" -> "testPassword123"
        )

        val result = UserRegisterForm.RegisterUserForm.bind(testUserRegistration)
        result.errors.isEmpty mustBe true
      }
    }

    "have a full list of errors" when {
      "no data has been input" in {
        val testUserRegistration = Map(
          "firstName"       -> "",
          "lastName"        -> "",
          "userName"        -> "",
          "email"           -> "",
          "password"        -> "",
          "confirmPassword" -> ""
        )

        val result = UserRegisterForm.RegisterUserForm.bind(testUserRegistration)
        result.errors mustBe List(
          FormError("firstName", List("You have not entered a valid first name")),
          FormError("lastName", List("You have not entered a valid last name")),
          FormError("userName", List("You have not entered a valid user name")),
          FormError("email", List("You have not entered a valid email address")),
          FormError("password", List("You have not entered a password")),
          FormError("confirmPassword", List("You have not confirmed your password"))
        )
      }
    }

    "have some errors" when {
      "the passwords don't match" in {
        val testUserRegistration = Map(
          "firstName"       -> "testFirstName",
          "lastName"        -> "testLastName",
          "userName"        -> "tUserName",
          "email"           -> "test@email.com",
          "password"        -> "testPassword123",
          "confirmPassword" -> "testPassword12"
        )

        val result = UserRegisterForm.RegisterUserForm.bind(testUserRegistration)
        result.globalErrors mustBe List(FormError("", List("The passwords you have entered don't match")))
      }
    }
  }
}
