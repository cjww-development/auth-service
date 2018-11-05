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

import com.cjwwdev.security.deobfuscation.{DeObfuscation, DeObfuscator, DecryptionError}
import com.cjwwdev.security.obfuscation.{Obfuscation, Obfuscator}
import play.api.libs.json._

case class Settings(displayName: String,
                    displayNameColour: String,
                    displayImageURL: String)

object Settings {
  implicit val settingsRead: Reads[Settings] = Reads[Settings] {
    json => JsSuccess(Settings(
      displayName       = (json \ "displayName").asOpt[String].getOrElse(default.displayName),
      displayNameColour = (json \ "displayNameColour").asOpt[String].getOrElse(default.displayNameColour),
      displayImageURL   = (json \ "displayImageURL").asOpt[String].getOrElse(default.displayImageURL)
    ))
  }

  implicit val settingsWrite: OWrites[Settings] = OWrites[Settings] {
    settings => Json.obj(
      "displayName" -> settings.displayName,
      "displayNameColour" -> settings.displayNameColour,
      settings.displayImageURL match {
        case "" => "displayImageURL" -> default.displayImageURL
        case _  => "displayImageURL" -> settings.displayImageURL
      }
    )
  }

  implicit val standardFormat: OFormat[Settings] = OFormat(settingsRead, settingsWrite)

  implicit val obfuscator: Obfuscator[Settings] = new Obfuscator[Settings] {
    override def encrypt(value: Settings): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }

  implicit val deObfuscator: DeObfuscator[Settings] = new DeObfuscator[Settings] {
    override def decrypt(value: String): Either[Settings, DecryptionError] = {
      DeObfuscation.deObfuscate[Settings](value)
    }
  }

  val default = Settings("full", "#FFFFFF", "/account-services/assets/images/background.jpg")
}
