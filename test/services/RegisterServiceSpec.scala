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

import connectors.UserRegisterSuccessResponse
import mocks.CJWWSpec
import models.accounts.UserRegister
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers
import play.api.test.Helpers.OK

import scala.concurrent.Future

class RegisterServiceSpec extends CJWWSpec {

  val testService = new RegisterService(mockUserRegisterConnector)

  "registerIndividual" should {

    val testUserRegister =
      UserRegister(
        "testFirstName",
        "testLastName",
        "testUserName",
        "test@email.com",
        "testPass",
        "testPass"
      )

    "return a UserRegisterResponse" in {
      when(mockUserRegisterConnector.createNewIndividualUser(ArgumentMatchers.any()))
        .thenReturn(Future.successful(UserRegisterSuccessResponse(OK)))

      val result = await(testService.registerIndividual(testUserRegister))
      result mustBe UserRegisterSuccessResponse(OK)
    }
  }
}
