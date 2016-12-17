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

import models.accounts.UserAccount
import org.scalatestplus.play.PlaySpec

class EditProfileServiceSpec extends PlaySpec {

  class Setup {
    object TestService extends EditProfileService

    val userAccountWithSettings =
      UserAccount(
        Some("testUserId"),
        "testFirstName",
        "testLastName",
        "testUserName",
        "testEmail",
        "testPassword",
        Some(Map("displayName" -> "user")),
        None)

    val userAccountWithoutSettings =
      UserAccount(
        Some("testUserId"),
        "testFirstName",
        "testLastName",
        "testUserName",
        "testEmail",
        "testPassword",
        None,
        None
      )
  }

  "getDisplayOption" should {
    "return full" when {
      "no settings map is defined" in new Setup {
        val result = TestService.getDisplayOption(Some(userAccountWithoutSettings))
        result mustBe "full"
      }

      "the account is not defined" in new Setup {
        val result = TestService.getDisplayOption(None)
        result mustBe "full"
      }
    }

    "return user" in new Setup {
      val result = TestService.getDisplayOption(Some(userAccountWithSettings))
      result mustBe "user"
    }
  }
}
