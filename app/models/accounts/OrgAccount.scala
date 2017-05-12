// Copyright (C) 2016-2017 the original author or authors.
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

case class OrgAccount(orgId: Option[String],
                      orgName: String,
                      initials: String,
                      orgUserName: String,
                      location: String,
                      orgEmail: String,
                      credentialType: String,
                      password: String,
                      metaData: Option[Map[String, DateTime]],
                      settings: Option[Map[String, String]])

object OrgAccount {
  implicit val dateTimeRead: Reads[DateTime] =
    (__ \ "$date").read[Long].map { dateTime =>
      new DateTime(dateTime, DateTimeZone.UTC)
    }

  implicit val dateTimeWrite: Writes[DateTime] = new Writes[DateTime] {
    def writes(dateTime: DateTime): JsValue = Json.obj(
      "$date" -> dateTime.getMillis
    )
  }

  implicit val format = Json.format[OrgAccount]
}


