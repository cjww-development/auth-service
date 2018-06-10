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
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec

class
FeatureServiceSpec extends PlaySpec with BeforeAndAfterEach {

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    System.clearProperty(s"features.${Features.DEVERSITY}")
  }

  val testService = new FeatureService {}

  "getAllBooleanFeatureStates" should {
    "return a map of string -> boolean" in {
      System.setProperty(s"features.${Features.DEVERSITY}", "true")

      val result = testService.getAllBooleanFeatureStates(Features.allFeatures)
      result mustBe List(FeatureState(Features.DEVERSITY.toString, state = true))
    }
  }

  "getFeatureState" should {
    "get the state" when {
      "the feature is off and return false" in {
        System.setProperty(s"features.${Features.DEVERSITY}", "false")

        val result = testService.getBooleanFeatureState(Features.DEVERSITY)
        assert(!result)
      }

      "the feature is on and return true" in {
        System.setProperty(s"features.${Features.DEVERSITY}", "true")

        val result = testService.getBooleanFeatureState(Features.DEVERSITY)
        assert(result)
      }

      "the feature is not set and return false" in {
        val result = testService.getBooleanFeatureState(Features.DEVERSITY)
        assert(!result)
      }
    }
  }

  "setFeatureState" should {
    "set the test feature state to true" in {
      val result = testService.setBooleanFeatureState(Features.DEVERSITY, state = true)
      assert(result)
    }

    "set the test feature state to false" in {
      val result = testService.setBooleanFeatureState(Features.DEVERSITY, state = false)
      assert(!result)
    }
  }
}