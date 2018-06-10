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

package services

import enums.Features
import forms.FeatureState
import javax.inject.Inject

class DefaultFeatureService @Inject()() extends FeatureService

trait FeatureService {

  def getAllBooleanFeatureStates(features: List[Features.Value]): List[FeatureState] = {
    features.map(feature => FeatureState(feature.toString, getBooleanFeatureState(feature)))
  }

  def getBooleanFeatureState(featureName: Features.Value): Boolean = {
    Option(System.getProperty(s"features.$featureName")).fold(false)(_.toBoolean)
  }

  def setBooleanFeatureState(featureName: String, state: Boolean): Boolean = {
    System.setProperty(s"features.$featureName", state.toString)
    if(System.getProperty(s"features.$featureName").toBoolean == state) {
      state
    } else {
      throw new IllegalStateException(s"Could not set state for feature $featureName")
    }
  }
}