/*
 * Copyright 2018 CJWW Development
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

package models.accounts

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Json.JsValueWrapper

case class Settings(displayName: String,
                    displayNameColour: String,
                    displayImageURL: String)

object Settings {
  implicit val settingsRead: Reads[Settings] = new Reads[Settings] {
    override def reads(json: JsValue): JsResult[Settings] = JsSuccess(
      Settings(
        displayName       = (json \ "displayName").asOpt[String].getOrElse(default.displayName),
        displayNameColour = (json \ "displayNameColour").asOpt[String].getOrElse(default.displayNameColour),
        displayImageURL   = (json \ "displayImageURL").asOpt[String].getOrElse(default.displayImageURL)
      )
    )
  }

  implicit val settingsWrite: OWrites[Settings] = new OWrites[Settings] {
    override def writes(o: Settings): JsObject = {
      def buildImageUrl: (String, JsValueWrapper) = {
        o.displayImageURL match {
          case "" => "displayImageURL" -> default.displayImageURL
          case _  => "displayImageURL" -> o.displayImageURL
        }
      }
      Json.obj(
        "displayName"       -> o.displayName,
        "displayNameColour" -> o.displayNameColour,
        buildImageUrl
      )
    }
  }

  implicit val standardFormat: OFormat[Settings] = OFormat(settingsRead, settingsWrite)

  val default = Settings("full", "#FFFFFF", "/account-services/assets/images/background.jpg")
}
