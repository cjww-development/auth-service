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

import org.scalatestplus.play.PlaySpec

class FeatureSwitchFormSpec extends PlaySpec {

  "Feature switch form" should {
    "bind fully with one input" in {
      val testFeatures = Map(
        "featureState[DEVERSITY]" -> "true"
      )

      val result = FeatureSwitchForm.form.bind(testFeatures)
      result.errors.isEmpty mustBe true
      result.get            mustBe FeatureModel(featureState = List(
        FeatureState("DEVERSITY", state = true)
      ))
    }

    "bind fully with two inputs" in {
      val testFeatures = Map(
        "featureState[DEVERSITY]" -> "true",
        "featureState[INFO_HUB]"  -> "true"
      )

      val result = FeatureSwitchForm.form.bind(testFeatures)
      result.errors.isEmpty mustBe true
      result.get            mustBe FeatureModel(featureState = List(
        FeatureState("DEVERSITY", state = true),
        FeatureState("INFO_HUB", state = true)
      ))
    }

    "bind fully with three inputs" in {
      val testFeatures = Map(
        "featureState[DEVERSITY]" -> "true",
        "featureState[INFO_HUB]"  -> "true",
        "featureState[DIAG]"      -> "true"
      )

      val result = FeatureSwitchForm.form.bind(testFeatures)
      result.errors.isEmpty mustBe true
      result.get            mustBe FeatureModel(featureState = List(
        FeatureState("DEVERSITY", state = true),
        FeatureState("INFO_HUB", state = true),
        FeatureState("DIAG", state = true)
      ))
    }

    "bind fully with three inputs and a CSRF token" in {
      val testFeatures = Map(
        "csrfToken"               -> "testToken",
        "featureState[DEVERSITY]" -> "true",
        "featureState[INFO_HUB]"  -> "true",
        "featureState[DIAG]"      -> "true"
      )

      val result = FeatureSwitchForm.form.bind(testFeatures)
      result.errors.isEmpty mustBe true
      result.get            mustBe FeatureModel(featureState = List(
        FeatureState("DEVERSITY", state = true),
        FeatureState("INFO_HUB", state = true),
        FeatureState("DIAG", state = true)
      ))
    }
  }
}
