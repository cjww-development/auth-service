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
import play.api.libs.json.{JsSuccess, Json}

class SessionUpdateSetSpec extends PlaySpec {

  val testModel = SessionUpdateSet("testKey", "testData")

  val testJson =
    Json.parse("""
                 |{
                 |   "key" : "testKey",
                 |   "data" : "testData"
                 |}
               """.stripMargin)

  "SessionUpdateSet" should {
    "read from JSON" in {
      Json.fromJson[SessionUpdateSet](testJson) mustBe JsSuccess(testModel)
    }

    "write to JSON" in {
      Json.toJson[SessionUpdateSet](testModel) mustBe testJson
    }
  }
}
