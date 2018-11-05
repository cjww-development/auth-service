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
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class TeacherDetails(userId: String, title: String, lastName: String, room: String, status: String)

object TeacherDetails {
  implicit val standardFormat: OFormat[TeacherDetails] = (
    (__ \ "userId").format[String] and
    (__ \ "title").format[String] and
    (__ \ "lastName").format[String] and
    (__ \ "room").format[String] and
    (__ \ "status").format[String]
  )(TeacherDetails.apply, unlift(TeacherDetails.unapply))

  implicit val listDeObfuscator: DeObfuscator[List[TeacherDetails]] = new DeObfuscator[List[TeacherDetails]] {
    override def decrypt(value: String): Either[List[TeacherDetails], DecryptionError] = {
      DeObfuscation.deObfuscate[List[TeacherDetails]](value)
    }
  }

  implicit val deObfuscator: DeObfuscator[TeacherDetails] = new DeObfuscator[TeacherDetails] {
    override def decrypt(value: String): Either[TeacherDetails, DecryptionError] = {
      DeObfuscation.deObfuscate[TeacherDetails](value)
    }
  }
}
