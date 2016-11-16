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
package forms

import models.UserRegister
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import org.mockito.Mockito._
import org.mockito.Matchers

class UserRegisterFormSpec extends PlaySpec with OneAppPerSuite with MockitoSugar {

  class Setup {
    val testData = UserRegister("testFirstName","testLastName","testUserName","test@email.com","testPassword","testPassword")
    val testForm =  UserRegisterForm.RegisterUserForm
  }

  "Populate form" should {
    "return a form with no errors" in new Setup {
      val result = testForm.fill(testData)

      result.get.firstName mustBe "testFirstName"
      result.get.lastName mustBe "testLastName"
      result.get.userName mustBe "testUserName"
      result.get.email mustBe "test@email.com"
      result.get.password mustBe "testPassword"
      result.get.confirmPassword mustBe "testPassword"
    }

    "return a form with errors" when {
      "no first name is entered" in new Setup {
        val result = testForm.bind(
          Map(
            "firstName" -> "",
            "lastName" -> testData.lastName,
            "userName" -> testData.userName,
            "email" -> testData.email,
            "password" -> testData.password,
            "confirmPassword" -> testData.confirmPassword
          )
        )

        result.error("firstName").get.message mustBe "You have not entered your first name"
      }

      "no last name is entered" in new Setup {
        val result = testForm.bind(
          Map(
            "firstName" -> testData.firstName,
            "lastName" -> "",
            "userName" -> testData.userName,
            "email" -> testData.email,
            "password" -> testData.password,
            "confirmPassword" -> testData.confirmPassword
          )
        )

        result.error("lastName").get.message mustBe "You have not entered your last name"
      }

//      "no user name is entered" in new Setup {
//        val result = testForm.bind(
//          Map(
//            "firstName" -> testData.firstName,
//            "lastName" -> testData.lastName,
//            "userName" -> "",
//            "email" -> testData.email,
//            "password" -> testData.password,
//            "confirmPassword" -> testData.confirmPassword
//          )
//        )
//
//        result.error("userName").get.message mustBe "You have not entered your user name"
//      }

      "no email name is entered" in new Setup {
        val result = testForm.bind(
          Map(
            "firstName" -> testData.firstName,
            "lastName" -> testData.lastName,
            "userName" -> testData.userName,
            "email" -> "",
            "password" -> testData.password,
            "confirmPassword" -> testData.confirmPassword
          )
        )

        result.error("email").get.message mustBe "Please enter a valid email address"
      }

      "no password is entered" in new Setup {
        val result = testForm.bind(
          Map(
            "firstName" -> testData.firstName,
            "lastName" -> testData.lastName,
            "userName" -> testData.userName,
            "email" -> testData.email,
            "password" -> "",
            "confirmPassword" -> testData.confirmPassword
          )
        )

        result.error("password").get.message mustBe "You have not entered a password"
      }

      "no confirm password is entered" in new Setup {
        val result = testForm.bind(
          Map(
            "firstName" -> testData.firstName,
            "lastName" -> testData.lastName,
            "userName" -> testData.userName,
            "email" -> testData.email,
            "password" -> testData.password,
            "confirmPassword" -> ""
          )
        )

        result.error("confirmPassword").get.message mustBe "You have not confirmed your password"
      }

      "if passwords dont match" in new Setup {
        val result = testForm.bind(
          Map(
            "firstName" -> testData.firstName,
            "lastName" -> testData.lastName,
            "userName" -> testData.userName,
            "email" -> testData.email,
            "password" -> testData.password,
            "confirmPassword" -> "testPassword1"
          )
        )

        result.globalError.get.messages mustBe List("The passwords you have entered do not match")
      }
    }
  }
}
