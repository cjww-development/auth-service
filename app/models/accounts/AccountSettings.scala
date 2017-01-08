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

import play.api.libs.json.Json

case class AccountSettings(userId : String, settings : Map[String, String])

object AccountSettings {
  implicit val format = Json.format[AccountSettings]
}

case class DashboardDisplay(displayName : Option[String],
                            displayNameColour : Option[String],
                            displayImageURL : Option[String])

object DashboardDisplay {
  implicit val format = Json.format[DashboardDisplay]

  def toAccountSettings(userId : String, displayNameOption: DashboardDisplay) : AccountSettings = {
    AccountSettings(
      userId,
      Map(
        "displayName" -> displayNameOption.displayName.getOrElse("full"),
        "displayNameColour" -> displayNameOption.displayNameColour.getOrElse("#FFFFFF"),
        "displayImageURL" -> displayNameOption.displayImageURL.getOrElse("/account-services/assets/images/background.jpg")
      )
    )
  }
}
