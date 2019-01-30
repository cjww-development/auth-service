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

package models.accounts

import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.security.obfuscation.{Obfuscation, Obfuscator}
import play.api.libs.json._

case class PasswordSet(oldPassword : String, newPassword : String, confirmPassword : String)

object PasswordSet {
  implicit val passwordSetWrite: OWrites[PasswordSet] = OWrites[PasswordSet] {
    set => Json.obj(
      "previousPassword" -> set.oldPassword.sha512,
      "newPassword"      -> set.newPassword.sha512
    )
  }

  implicit val passwordSetReads: Reads[PasswordSet] = Json.reads[PasswordSet]

  implicit val standardFormat: OFormat[PasswordSet] = OFormat(passwordSetReads, passwordSetWrite)

  implicit val obfuscator: Obfuscator[PasswordSet] = new Obfuscator[PasswordSet] {
    override def encrypt(value: PasswordSet): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }
}
