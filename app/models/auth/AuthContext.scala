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

import java.util.UUID

import play.api.libs.json.Json

case class AuthContext(_id : String,
                       user: User,
                       basicDetailsUri : String,
                       enrolmentsUri : String,
                       settingsUri : String)

case class User(userId : String,
                firstName : String,
                lastName : String)

object AuthContext {
  implicit val formatUser = Json.format[User]
  implicit val format = Json.format[AuthContext]
}

case class AuthContextDetails(contextId : String,
                              firstName : String,
                              lastName : String) {

  def sessionMap : Map[String, String] =
    Map(
      "cookieId" -> s"session-${UUID.randomUUID()}",
      "contextId" -> contextId,
      "firstName" -> firstName,
      "lastName" -> lastName)
}

object AuthContextDetails {
  implicit val format = Json.format[AuthContextDetails]
}
