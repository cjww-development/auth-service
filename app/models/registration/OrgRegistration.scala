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

package models.registration

import com.cjwwdev.security.encryption.SHA512
import play.api.libs.json._

case class OrgRegistration(orgName: String,
                           initials: String,
                           orgUserName: String,
                           location: String,
                           orgEmail: String,
                           password: String,
                           confirmPassword: String)

object OrgRegistration {
  implicit val orgRegisterWrites: OWrites[OrgRegistration] = OWrites[OrgRegistration] {
    orgReg => Json.obj(
      "orgName"     -> orgReg.orgName,
      "initials"    -> orgReg.initials,
      "orgUserName" -> orgReg.orgUserName,
      "location"    -> orgReg.location,
      "orgEmail"    -> orgReg.orgEmail,
      "password"    -> SHA512.encrypt(orgReg.password)
    )
  }

  val orgRegisterReads: Reads[OrgRegistration] = Json.reads[OrgRegistration]

  implicit val standardFormat: OFormat[OrgRegistration] = OFormat(orgRegisterReads, orgRegisterWrites)
}


