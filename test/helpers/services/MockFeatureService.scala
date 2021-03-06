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

package helpers.services

import com.cjwwdev.featuremanagement.models.Feature
import com.cjwwdev.featuremanagement.services.FeatureService
import helpers.other.Fixtures
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

trait MockFeatureService extends BeforeAndAfterEach with MockitoSugar with Fixtures {
  self: PlaySpec =>

  val mockFeatureService = mock[FeatureService]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockFeatureService)
  }

  def mockGetAllBooleanFeatureStates(): OngoingStubbing[List[Feature]] = {
    when(mockFeatureService.getAllStates(ArgumentMatchers.any()))
      .thenReturn(List(
        Feature(feature = "test1", state = false),
        Feature(feature = "test2", state = true)
      ))
  }

  def mockGetFeatureState(state: Feature): OngoingStubbing[Feature] = {
    when(mockFeatureService.getState(ArgumentMatchers.any()))
      .thenReturn(state)
  }

  def mockSetFeatureState(state: Boolean): OngoingStubbing[Boolean] = {
    when(mockFeatureService.setState(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(state)
  }
}
