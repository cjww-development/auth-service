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

import forms.validation.InvalidSettingsException
import org.scalatestplus.play.PlaySpec
import play.api.data.FormError

class SettingsFormSpec extends PlaySpec {
  "SettingsForm" should {
    "bind fully" when {
      "all values are populated" in {
        val testSettings = Map(
          "displayName"       -> "full",
          "displayNameColour" -> "#FFFFFF",
          "displayImageURL"   -> "/account-services/assets/images/background.jpg"
        )

        val result = SettingsForm.form.bind(testSettings)
        result.errors.isEmpty mustBe true
      }
    }

    "throw an InvalidSettingsException" when {
      "an invalid display name option is entered" in {
        val testSettings = Map(
          "displayName"       -> "invalid option",
          "displayNameColour" -> "#FFFFFF",
          "displayImageURL"   -> "/account-services/assets/images/background.jpg"
        )

        intercept[InvalidSettingsException](SettingsForm.form.bind(testSettings))
      }
    }

    "contain errors" when {
      "an invalid colour and image url are entered" in {
        val testSettings = Map(
          "displayName"       -> "full",
          "displayNameColour" -> "wef8123we013",
          "displayImageURL"   -> "/test/uri"
        )

        val result = SettingsForm.form.bind(testSettings)
        result.errors mustBe List(
          FormError("displayNameColour", List("Enter a valid hexadecimal colour")),
          FormError("displayImageURL", List("Enter a valid URL for your background image"))
        )
      }
    }
  }
}
