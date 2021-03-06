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

package models

import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.security.obfuscation.{Obfuscation, Obfuscator}
import play.api.libs.json.{Json, OWrites}

case class UserLogin(username : String, password : String)

object UserLogin {
  implicit val userLoginWrites: OWrites[UserLogin] = OWrites[UserLogin] {
    login => Json.obj(
      "username" -> login.username,
      "password" -> login.password.sha512
    )
  }

  implicit val obfuscator: Obfuscator[UserLogin] = new Obfuscator[UserLogin] {
    override def encrypt(value: UserLogin): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }
}
