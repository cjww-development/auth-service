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

package models.accounts

import models.UserLogin
import org.joda.time.DateTime
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class AccountSettingsSpec extends PlaySpec {

  val testAccountSettings =
    AccountSettings(
      "user-1234567890",
      Map(
        "displayName" -> "user",
        "displayNameColour" -> "#124AAO",
        "displayImageURL" -> "test/link"
      )
    )

  val testAccountSettingsJson = Json.parse(
    """
      |{
      |   "userId" : "user-1234567890",
      |   "settings" : {
      |       "displayName" : "user",
      |       "displayNameColour" : "#124AAO",
      |       "displayImageURL" : "test/link"
      |   }
      |}
    """.stripMargin)

  "AccountSettings" should {
    "read from JSON" in {
      Json.fromJson[AccountSettings](testAccountSettingsJson) mustBe JsSuccess(testAccountSettings)
    }

    "write to JSON" in {
      Json.toJson[AccountSettings](testAccountSettings) mustBe testAccountSettingsJson
    }
  }

  val testDashboardDisplay =
    DashboardDisplay(
      Some("user"),
      Some("#124AAO"),
      Some("test/link")
    )

  val testDashboardDisplayJson =
    Json.parse(
      """
        |{
        |   "displayName" : "user",
        |   "displayNameColour" : "#124AAO",
        |   "displayImageURL" : "test/link"
        |}
      """.stripMargin)

  "DashboardDisplay" should {
    "read from JSON" in {
      Json.fromJson[DashboardDisplay](testDashboardDisplayJson) mustBe JsSuccess(testDashboardDisplay)
    }

    "write to JSON" in {
      Json.toJson[DashboardDisplay](testDashboardDisplay) mustBe testDashboardDisplayJson
    }

    "convert to toAccountSettings" in {
      val result = DashboardDisplay.toAccountSettings("user-1234567890", testDashboardDisplay)
      result.userId mustBe "user-1234567890"
      result.settings mustBe Map("displayName" -> "user", "displayNameColour" -> "#124AAO", "displayImageURL" -> "test/link")
    }
  }
}
