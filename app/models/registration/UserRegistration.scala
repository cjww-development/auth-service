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


package models.registration

import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.security.obfuscation.{Obfuscation, Obfuscator}
import play.api.libs.json._

case class UserRegistration(firstName: String,
                            lastName: String,
                            userName: String,
                            email: String,
                            password: String,
                            confirmPassword: String)

object UserRegistration {
  implicit val userRegisterWrites: OWrites[UserRegistration] = OWrites[UserRegistration] {
    userReg => Json.obj(
      "firstName" -> userReg.firstName,
      "lastName"  -> userReg.lastName,
      "userName"  -> userReg.userName,
      "email"     -> userReg.email,
      "password"  -> userReg.password.sha512
    )
  }

  val userRegisterReads: Reads[UserRegistration] = Json.reads[UserRegistration]

  implicit val standardFormat: OFormat[UserRegistration] = OFormat(userRegisterReads, userRegisterWrites)

  implicit val obfuscator: Obfuscator[UserRegistration] = new Obfuscator[UserRegistration] {
    override def encrypt(value: UserRegistration): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }

}
