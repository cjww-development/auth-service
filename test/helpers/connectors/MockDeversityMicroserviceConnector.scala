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

package helpers.connectors

import connectors.DeversityConnector
import helpers.other.Fixtures
import models.accounts.DeversityEnrolment
import models.deversity.{OrgDetails, TeacherDetails}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

import scala.concurrent.Future

trait MockDeversityMicroserviceConnector extends BeforeAndAfterEach with MockitoSugar with Fixtures {
  self: PlaySpec =>

  val mockDeversityMicroserviceConnector = mock[DeversityConnector]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDeversityMicroserviceConnector)
  }

  def mockGetTeacherDeversityEnrolment(fetched: Boolean): OngoingStubbing[Future[Option[DeversityEnrolment]]] = {
    when(mockDeversityMicroserviceConnector.getDeversityEnrolment(any(), any(), any()))
      .thenReturn(Future.successful(if(fetched) Some(testTeacherEnrolment) else None))
  }

  def mockGetStudentDeversityEnrolment(fetched: Boolean): OngoingStubbing[Future[Option[DeversityEnrolment]]] = {
    when(mockDeversityMicroserviceConnector.getDeversityEnrolment(any(), any(), any()))
      .thenReturn(Future.successful(if(fetched) Some(testStudentEnrolment) else None))
  }

  def mockGetTeacherInfo(fetched: Boolean): OngoingStubbing[Future[Option[TeacherDetails]]] = {
    when(mockDeversityMicroserviceConnector.getTeacherInfo(any(), any())(any(), any(), any()))
      .thenReturn(Future.successful(if(fetched) Some(testTeacherDetails) else None))
  }

  def mockGetSchoolInfo(fetched: Boolean): OngoingStubbing[Future[Option[OrgDetails]]] = {
    when(mockDeversityMicroserviceConnector.getSchoolInfo(any())(any(), any(), any()))
      .thenReturn(Future.successful(if(fetched) Some(testOrgDetails) else None))
  }
}
