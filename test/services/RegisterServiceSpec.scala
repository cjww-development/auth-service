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
package services

import connectors._
import models.accounts.UserRegister
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import org.mockito.Mockito._
import org.mockito.Matchers
import play.api.test.Helpers._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class RegisterServiceSpec extends PlaySpec with OneAppPerSuite with MockitoSugar {

  val mockConnector = mock[UserRegistrationConnector]

  val testData = UserRegister("testFirstName","testLastName","testUserName","test@email.com","testPassword","testPassword")

  class Setup {
    object TestService extends RegisterService {
      val userRegistration = mockConnector
    }
  }

  "RegisterService" should {
    "use the correct UserRegistrationConnector" in {
      RegisterService.userRegistration mustBe UserRegistrationConnector
    }
  }

  "registerIndividual" should {
    "return a UserRegisterSuccessResponse" in new Setup {
      when(mockConnector.createNewIndividualUser(Matchers.eq(testData)))
        .thenReturn(Future.successful(UserRegisterSuccessResponse(CREATED)))

      val result = TestService.registerIndividual(testData)
      Await.result(result, 5.seconds) mustBe UserRegisterSuccessResponse(CREATED)
    }

    "return a UserRegisterClientErrorResponse" in new Setup {
      when(mockConnector.createNewIndividualUser(Matchers.eq(testData)))
        .thenReturn(Future.successful(UserRegisterClientErrorResponse(BAD_REQUEST)))

      val result = TestService.registerIndividual(testData)
      Await.result(result, 5.seconds) mustBe UserRegisterClientErrorResponse(BAD_REQUEST)
    }

    "return a UserRegisterServerErrorResponse" in new Setup {
      when(mockConnector.createNewIndividualUser(Matchers.eq(testData)))
        .thenReturn(Future.successful(UserRegisterServerErrorResponse(INTERNAL_SERVER_ERROR)))

      val result = TestService.registerIndividual(testData)
      Await.result(result, 5.seconds) mustBe UserRegisterServerErrorResponse(INTERNAL_SERVER_ERROR)
    }

    "return a UserRegisterErrorResponse" in new Setup {
      when(mockConnector.createNewIndividualUser(Matchers.eq(testData)))
        .thenReturn(Future.successful(UserRegisterErrorResponse(status = 700)))

      val result = TestService.registerIndividual(testData)
      Await.result(result, 5.seconds) mustBe UserRegisterErrorResponse(700)
    }
  }
}
