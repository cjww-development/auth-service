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

package models.deversity

import com.cjwwdev.security.deobfuscation.{DeObfuscation, DeObfuscator, DecryptionError}
import play.api.libs.json.{Json, OFormat, Reads}

case class Classroom(classId: String, name: String)

object Classroom {
  implicit val format: OFormat[Classroom] = Json.format[Classroom]

  implicit val classListReader: Reads[List[Classroom]] = Reads[List[Classroom]] {
    Json.fromJson[List[Classroom]]
  }

  implicit val seqDeObfuscator: DeObfuscator[Seq[Classroom]] = new DeObfuscator[Seq[Classroom]] {
    override def decrypt(value: String): Either[Seq[Classroom], DecryptionError] = {
      DeObfuscation.deObfuscate[Seq[Classroom]](value)
    }
  }

  implicit val deObfuscator: DeObfuscator[Classroom] = new DeObfuscator[Classroom] {
    override def decrypt(value: String): Either[Classroom, DecryptionError] = {
      DeObfuscation.deObfuscate[Classroom](value)
    }
  }
}
