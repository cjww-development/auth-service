
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

package forms

import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.{Form, FormError, Forms, Mapping}

case class FeatureState(name: String, state: Boolean)
case class FeatureModel(featureState: List[FeatureState])

object FeatureSwitchForm {

  implicit def featureFormatter: Formatter[List[FeatureState]] = new Formatter[List[FeatureState]] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], List[FeatureState]] = {
      val states = data.toList.filterNot(_._1 == "csrfToken") map { case (feat, state) =>
        val index = feat.replace("featureState[", "").replace("]", "")
        val cleansedState = state match {
          case "on"  => true
          case "off" => false
          case bool  => bool.toBoolean
        }
        FeatureState(index, cleansedState)
      }

      Right(states)
    }

    override def unbind(key: String, value: List[FeatureState]): Map[String, String] = {
      value.map(feat => (feat.name.toString, feat.state.toString)).toMap
    }
  }

  val feature: Mapping[List[FeatureState]] = Forms.of[List[FeatureState]](featureFormatter)

  val form: Form[FeatureModel] = Form(
    mapping(
      "featureState" -> feature
    )(FeatureModel.apply)(FeatureModel.unapply)
  )
}
