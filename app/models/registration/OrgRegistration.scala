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

package models.registration

import com.cjwwdev.security.encryption.SHA512
import play.api.libs.json.{JsObject, Json, OWrites}

case class OrgRegistration(orgName: String,
                           initials: String,
                           orgUserName: String,
                           location: String,
                           orgEmail: String,
                           password: String,
                           confirmPassword: String)

object OrgRegistration {
  implicit val orgRegisterWrites: OWrites[OrgRegistration] = new OWrites[OrgRegistration] {
    override def writes(o: OrgRegistration): JsObject = Json.obj(
      "orgName"     -> o.orgName,
      "initials"    -> o.initials,
      "orgUserName" -> o.orgUserName,
      "location"    -> o.location,
      "orgEmail"    -> o.orgEmail,
      "password"    -> SHA512.encrypt(o.password)
    )
  }
}


