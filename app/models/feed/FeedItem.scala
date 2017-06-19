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

package models.feed

import com.cjwwdev.json.JsonFormats
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class FeedItem(userId : String,
                    sourceDetail: SourceDetail,
                    eventDetail: EventDetail,
                    generated : DateTime)

object FeedItem extends JsonFormats[FeedItem] {
  override implicit val standardFormat: OFormat[FeedItem] = (
    (__ \ "userId").format[String] and
    (__ \ "sourceDetail").format[SourceDetail](SourceDetail.standardFormat) and
    (__ \ "eventDetail").format[EventDetail](EventDetail.standardFormat) and
    (__ \ "generated").format[DateTime](dateTimeRead)(dateTimeWrite)
  )(FeedItem.apply, unlift(FeedItem.unapply))
}