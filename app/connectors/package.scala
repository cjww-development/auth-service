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

import com.cjwwdev.implicits.ImplicitHandlers
import play.api.libs
import play.api.libs.json
import play.api.libs.json.{Format, OFormat, Reads, Writes}

package object connectors extends ImplicitHandlers {
  implicit val stringReads: json.Reads.StringReads.type = Reads.StringReads
  implicit val stringWrites: libs.json.Writes.StringWrites.type = Writes.StringWrites
  implicit val stringFormat: Format[String] = Format(stringReads, stringWrites)
}
