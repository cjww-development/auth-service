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

package models.accounts

import com.cjwwdev.json.TimeFormat
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class BasicDetails(firstName : String,
                        lastName : String,
                        userName : String,
                        email : String,
                        createdAt : DateTime)

object BasicDetails extends TimeFormat {
  implicit val standardFormat: OFormat[BasicDetails] = (
    (__ \ "firstName").format[String] and
    (__ \ "lastName").format[String] and
    (__ \ "userName").format[String] and
    (__ \ "email").format[String] and
    (__ \ "createdAt").format[DateTime](dateTimeRead)(dateTimeWrite)
  )(BasicDetails.apply, unlift(BasicDetails.unapply))
}
