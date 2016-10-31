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
package models

import org.scalatestplus.play.PlaySpec

class UserLoginSpec extends PlaySpec {

  val testData =
    UserLogin(
      "testUserName",
      "testPassword"
    )

  "UserLogin" should {
    "encrypt the password AND should not equal testPassword" in {
      val result = testData.encryptPassword

      assert(result.password != "testPassword")
      assert(result.password.length == 128)
    }

    "return an empty UserLogin model" in {
      val result = UserLogin.empty

      assert(result.userName == "")
      assert(result.password == "")
    }
  }
}
