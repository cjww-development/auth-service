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

import connectors._
import controllers.traits.register.UserRegisterCtrl
import models.UserRegister
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import org.mockito.Mockito._
import org.mockito.Matchers
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class RegisterControllerSpec extends PlaySpec with OneAppPerSuite with MockitoSugar {

  val mockUserRegisterConnector = mock[UserRegistrationConnector]

  val testUser = UserRegister("testFirstName","testLastName","testUserName","test@email.com","testPassword","testPassword")

  class Setup {
    class TestController extends UserRegisterCtrl {
      val userRegister = mockUserRegisterConnector
    }

    val testController = new TestController
  }

  "show" should {
    "return an OK" in new Setup {
      val result = testController.show()(FakeRequest())
      status(result) mustBe OK
    }
  }

  "submit" should {
    "return a bad request" when {
      "a firstName is not input" in new Setup {
        val request = FakeRequest().withFormUrlEncodedBody(
          "firstName" -> "",
          "lastName" -> "testLastName",
          "userName" -> "testUserName",
          "email" -> "test@email.com",
          "password" -> "testPassword",
          "confirmPassword" -> "testPassword"
        )

        val result = testController.submit()(request)
        status(result) mustBe BAD_REQUEST
      }

      "a lastName is not input" in new Setup {
        val request = FakeRequest().withFormUrlEncodedBody(
          "firstName" -> "testFirstName",
          "lastName" -> "",
          "userName" -> "testUserName",
          "email" -> "test@email.com",
          "password" -> "testPassword",
          "confirmPassword" -> "password"
        )

        val result = testController.submit()(request)
        status(result) mustBe BAD_REQUEST
      }

      "a userName is not input" in new Setup {
        val request = FakeRequest().withFormUrlEncodedBody(
          "firstName" -> "testFirstName",
          "lastName" -> "testLastName",
          "userName" -> "",
          "email" -> "test@email.com",
          "password" -> "testPassword",
          "confirmPassword" -> "password"
        )

        val result = testController.submit()(request)
        status(result) mustBe BAD_REQUEST
      }

      "an email is not input" in new Setup {
        val request = FakeRequest().withFormUrlEncodedBody(
          "firstName" -> "testFirstName",
          "lastName" -> "testLastName",
          "userName" -> "testUserName",
          "email" -> "test@email.com",
          "password" -> "testPassword",
          "confirmPassword" -> "password"
        )

        val result = testController.submit()(request)
        status(result) mustBe BAD_REQUEST
      }

      "a password is not input" in new Setup {
        val request = FakeRequest().withFormUrlEncodedBody(
          "firstName" -> "testFirstName",
          "lastName" -> "testLastName",
          "userName" -> "testUserName",
          "email" -> "test@email.com",
          "password" -> "",
          "confirmPassword" -> ""
        )

        val result = testController.submit()(request)
        status(result) mustBe BAD_REQUEST
      }

      "nothing is input" in new Setup {
        val request = FakeRequest().withFormUrlEncodedBody(
          "firstName" -> "",
          "lastName" -> "",
          "userName" -> "",
          "email" -> "",
          "password" -> "",
          "confirmPassword" -> ""
        )

        val result = testController.submit()(request)
        status(result) mustBe BAD_REQUEST
      }

      "passwords dont match" in new Setup {
        val request = FakeRequest().withFormUrlEncodedBody(
          "firstName" -> "testFirstName",
          "lastName" -> "testLastName",
          "userName" -> "testUserName",
          "email" -> "test@email.com",
          "password" -> "testPassword",
          "confirmPassword" -> "password"
        )

        val result = testController.submit()(request)
        status(result) mustBe BAD_REQUEST
      }

      "an email is not in the correct format" in new Setup {
        val request = FakeRequest().withFormUrlEncodedBody(
          "firstName" -> "testFirstName",
          "lastName" -> "testLastName",
          "userName" -> "testUserName",
          "email" -> "testEmail",
          "password" -> "testPassword",
          "confirmPassword" -> "password"
        )

        val result = testController.submit()(request)
        status(result) mustBe BAD_REQUEST
      }
    }

    "return an OK" when {
      "all entered data is valid" in new Setup {
        val request = FakeRequest().withFormUrlEncodedBody(
          "firstName" -> "testFirstName",
          "lastName" -> "testLastName",
          "userName" -> "testUserName",
          "email" -> "test@email.com",
          "password" -> "testPassword",
          "confirmPassword" -> "testPassword"
        )

        when(mockUserRegisterConnector.createNewIndividualUser(Matchers.any[UserRegister]()))
          .thenReturn(Future.successful(UserRegisterSuccessResponse(CREATED)))

        val result = testController.submit()(request)
        status(result) mustBe OK
      }
    }

    "return an BAD REQUEST" when {
      "all entered data is valid but the server responds with a 4xx response" in new Setup {
        val request = FakeRequest().withFormUrlEncodedBody(
          "firstName" -> "testFirstName",
          "lastName" -> "testLastName",
          "userName" -> "testUserName",
          "email" -> "test@email.com",
          "password" -> "testPassword",
          "confirmPassword" -> "testPassword"
        )

        when(mockUserRegisterConnector.createNewIndividualUser(Matchers.any[UserRegister]()))
          .thenReturn(Future.successful(UserRegisterClientErrorResponse(BAD_REQUEST)))

        val result = testController.submit()(request)
        status(result) mustBe BAD_REQUEST
      }
    }

    "return an INTERNAL SERVER ERROR" when {
      "all entered data is valid but the server responds with a 5xx response" in new Setup {
        val request = FakeRequest().withFormUrlEncodedBody(
          "firstName" -> "testFirstName",
          "lastName" -> "testLastName",
          "userName" -> "testUserName",
          "email" -> "test@email.com",
          "password" -> "testPassword",
          "confirmPassword" -> "testPassword"
        )

        when(mockUserRegisterConnector.createNewIndividualUser(Matchers.any[UserRegister]()))
          .thenReturn(Future.successful(UserRegisterServerErrorResponse(INTERNAL_SERVER_ERROR)))

        val result = testController.submit()(request)
        status(result) mustBe INTERNAL_SERVER_ERROR
      }

      "all entered data is valid but the server responds with a code that isn't 2xx, 4xx or 5xx" in new Setup {
        val request = FakeRequest().withFormUrlEncodedBody(
          "firstName" -> "testFirstName",
          "lastName" -> "testLastName",
          "userName" -> "testUserName",
          "email" -> "test@email.com",
          "password" -> "testPassword",
          "confirmPassword" -> "testPassword"
        )

        when(mockUserRegisterConnector.createNewIndividualUser(Matchers.any[UserRegister]()))
          .thenReturn(Future.successful(UserRegisterErrorResponse(700)))

        val result = testController.submit()(request)
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
