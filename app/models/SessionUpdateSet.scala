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

import com.cjwwdev.security.obfuscation.{Obfuscation, Obfuscator}
import play.api.libs.json._

case class SessionUpdateSet(key : String, data : String)

object SessionUpdateSet {
  implicit val sessionUpdateSetWrites: OWrites[SessionUpdateSet] = OWrites[SessionUpdateSet] {
    set => Json.obj(set.key -> set.data)
  }

  implicit val sessionUpdateSetReads: Reads[SessionUpdateSet] = Json.reads[SessionUpdateSet]

  implicit val standardFormat: OFormat[SessionUpdateSet] = OFormat(sessionUpdateSetReads, sessionUpdateSetWrites)

  implicit val obfuscator: Obfuscator[SessionUpdateSet] = new Obfuscator[SessionUpdateSet] {
    override def encrypt(value: SessionUpdateSet): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }
}
