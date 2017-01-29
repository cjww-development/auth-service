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

package models.auth

import models.SessionUpdateSet
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class AuthContextSpec extends PlaySpec {

  val testModel =
    AuthContext(
      "context-1234567890",
      User(
        "user-766543",
        "testFirstName",
        "testLastName"
      ),
      "testLink",
      "testLink",
      "testLink"
    )

  val testJson =
    Json.parse("""
                 |{
                 |   "_id" : "context-1234567890",
                 |   "user" : {
                 |      "userId" : "user-766543",
                 |      "firstName" : "testFirstName",
                 |      "lastName" : "testLastName"
                 |   },
                 |   "basicDetailsUri" : "testLink",
                 |   "enrolmentsUri" : "testLink",
                 |   "settingsUri" : "testLink"
                 |}
               """.stripMargin)

  "AuthContext" should {
    "read from JSON" in {
      Json.fromJson[AuthContext](testJson) mustBe JsSuccess(testModel)
    }

    "write to JSON" in {
      Json.toJson[AuthContext](testModel) mustBe testJson
    }
  }

  val testContextDetails =
    AuthContextDetails(
      "context-1234567890",
      "testFirstName",
      "testLastName"
    )

  val testJsonContextDetails = Json.parse(
    """
      |{
      |   "contextId" : "context-1234567890",
      |   "firstName" : "testFirstName",
      |   "lastName" : "testLastName"
      |}
    """.stripMargin)

  "AuthContextDetails" should {
    "read from JSON" in {
      Json.fromJson[AuthContextDetails](testJsonContextDetails) mustBe JsSuccess(testContextDetails)
    }

    "write to JSON" in {
      Json.toJson[AuthContextDetails](testContextDetails) mustBe testJsonContextDetails
    }

    "be converted into a map" in {
      val result = testContextDetails.sessionMap

      result.get("contextId") mustBe Some("context-1234567890")
      result.get("firstName") mustBe Some("testFirstName")
      result.get("lastName") mustBe Some("testLastName")
    }
  }
}
