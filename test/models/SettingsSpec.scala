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

package models

import models.accounts.Settings
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class SettingsSpec extends PlaySpec {

  val testSettings = Settings(
    displayName = "full",
    displayNameColour = "#FFFFFF",
    displayImageURL = "/test/uri"
  )

  val testSettingsJson = Json.parse(
    """
      |{
      | "displayName" : "full",
      | "displayNameColour" : "#FFFFFF",
      | "displayImageURL" : "/test/uri"
      |}
    """.stripMargin)

  "Settings" should {
    "write into json" in {
      Json.toJson(testSettings)(Settings.settingsWrite) mustBe testSettingsJson
    }

    "be read from json" in {
      Json.fromJson(testSettingsJson)(Settings.settingsRead) mustBe JsSuccess(testSettings)
    }
  }
}
