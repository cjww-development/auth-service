
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

package forms

import com.cjwwdev.featuremanagement.models.Feature
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.{Form, FormError, Forms, Mapping}

case class FeatureModel(featureState: List[Feature])

object FeatureSwitchForm {

  implicit def featureFormatter: Formatter[List[Feature]] = new Formatter[List[Feature]] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], List[Feature]] = {
      val states = data.toList.filterNot(_._1 == "csrfToken") map { case (feat, state) =>
        val index = feat.replace("featureState[", "").replace("]", "")
        val cleansedState = state match {
          case "on"  => true
          case "off" => false
          case bool  => bool.toBoolean
        }
        Feature(index, cleansedState)
      }

      Right(states)
    }

    override def unbind(key: String, value: List[Feature]): Map[String, String] = {
      value.map(feat => (feat.feature, feat.state.toString)).toMap
    }
  }

  val feature: Mapping[List[Feature]] = Forms.of[List[Feature]](featureFormatter)

  val form: Form[FeatureModel] = Form(
    mapping(
      "featureState" -> feature
    )(FeatureModel.apply)(FeatureModel.unapply)
  )
}
