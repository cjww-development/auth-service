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

import mocks.MockModels
import org.scalatestplus.play.PlaySpec

class EditProfileServiceSpec extends PlaySpec with MockModels {

  "getDisplayOption" should {
    "return full" in {
      val result = EditProfileService.getDisplayOption(None)
      result mustBe Some("full")
    }

    "return testOption" in {
      val result = EditProfileService.getDisplayOption(Some(testSettings))
      result mustBe Some("user")
    }
  }

  "getDisplayNameColour" should {
    "return white" in {
      val result = EditProfileService.getDisplayNameColour(None)
      result mustBe Some("#FFFFFF")
    }

    "return testColour" in {
      val result = EditProfileService.getDisplayNameColour(Some(testSettings))
      result mustBe Some("#124AAO")
    }
  }

  "getDisplayImageURL" should {
    "return /account-services/assets/images/background.jpg" in {
      val result = EditProfileService.getDisplayImageURL(None)
      result mustBe Some("/account-services/assets/images/background.jpg")
    }

    "return testLink" in {
      val result = EditProfileService.getDisplayImageURL(Some(testSettings))
      result mustBe Some("/test/Link")
    }
  }
}
