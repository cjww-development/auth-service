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

import com.cjwwdev.security.deobfuscation.{DeObfuscation, DeObfuscator, DecryptionError}
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class DeversityEnrolment(schoolDevId: String,
                              role: String,
                              title: Option[String],
                              room: Option[String],
                              teacher: Option[String])

object DeversityEnrolment {
  implicit val standardFormat: OFormat[DeversityEnrolment] = (
    (__ \ "schoolDevId").format[String] and
    (__ \ "role").format[String] and
    (__ \ "title").formatNullable[String] and
    (__ \ "room").formatNullable[String] and
    (__ \ "teacher").formatNullable[String]
  )(DeversityEnrolment.apply, unlift(DeversityEnrolment.unapply))

  implicit val deObfuscator: DeObfuscator[DeversityEnrolment] = new DeObfuscator[DeversityEnrolment] {
    override def decrypt(value: String): Either[DeversityEnrolment, DecryptionError] = {
      DeObfuscation.deObfuscate[DeversityEnrolment](value)
    }
  }
}
