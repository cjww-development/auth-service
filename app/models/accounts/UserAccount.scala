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

import java.util.UUID

import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.json._

case class UserAccount(_id : Option[String],
                       firstName : String,
                       lastName : String,
                       userName : String,
                       email : String,
                       password : String,
                       settings : Option[Map[String, String]] = None,
                       details : Option[Map[String, DateTime]] = None) {

  def sessionMap : Map[String, String] =
    Map(
      "cookieID" -> s"session-${UUID.randomUUID().toString}",
      "_id" -> _id.get,
      "firstName" -> firstName,
      "lastName" -> lastName)
}

object UserAccount {

  implicit val dateTimeRead: Reads[DateTime] =
    (__ \ "$date").read[Long].map { dateTime =>
      new DateTime(dateTime, DateTimeZone.UTC)
    }

  implicit val dateTimeWrite: Writes[DateTime] = new Writes[DateTime] {
    def writes(dateTime: DateTime): JsValue = Json.obj(
      "$date" -> dateTime.getMillis
    )
  }

  implicit val format = Json.format[UserAccount]
}

case class UserProfile(firstName : String, lastName : String, userName : String, email : String)

object UserProfile {
  implicit val format = Json.format[UserProfile]

  def fromAccount(account: UserAccount) : UserProfile = {
    UserProfile(
      account.firstName,
      account.lastName,
      account.userName,
      account.email
    )
  }
}
