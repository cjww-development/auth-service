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


package models.deversity

import com.cjwwdev.security.deobfuscation.{DeObfuscation, DeObfuscator, DecryptionError}
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class OrgDetails(orgName: String, initials: String, location: String)

object OrgDetails {
  implicit val standardFormat: OFormat[OrgDetails] = (
    (__ \ "orgName").format[String] and
    (__ \ "initials").format[String] and
    (__ \ "location").format[String]
  )(OrgDetails.apply, unlift(OrgDetails.unapply))

  implicit val deObfuscator: DeObfuscator[OrgDetails] = new DeObfuscator[OrgDetails] {
    override def decrypt(value: String): Either[OrgDetails, DecryptionError] = {
      DeObfuscation.deObfuscate[OrgDetails](value)
    }
  }
}
