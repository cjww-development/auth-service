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

package helpers.connectors

import connectors.DeversityMicroserviceConnector
import enums.HttpResponse
import helpers.other.Fixtures
import models.RegistrationCode
import models.accounts.DeversityEnrolment
import models.deversity.{Classroom, OrgDetails, TeacherDetails}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MockDeversityMicroserviceConnector extends BeforeAndAfterEach with MockitoSugar with Fixtures {
  self: PlaySpec =>

  val mockDeversityMicroserviceConnector = mock[DeversityMicroserviceConnector]

  private val NO_ENROLMENTS: Int = 0
  private val ENROLMENTS: Int    = 5

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDeversityMicroserviceConnector)
  }

  def mockGetTeacherDeversityEnrolment(fetched: Boolean): OngoingStubbing[Future[Option[DeversityEnrolment]]] = {
    when(mockDeversityMicroserviceConnector.getDeversityEnrolment(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(if(fetched) Some(testTeacherEnrolment) else None))
  }

  def mockGetStudentDeversityEnrolment(fetched: Boolean): OngoingStubbing[Future[Option[DeversityEnrolment]]] = {
    when(mockDeversityMicroserviceConnector.getDeversityEnrolment(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(if(fetched) Some(testStudentEnrolment) else None))
  }

  def mockGetPendingEnrolmentCount(notZero: Boolean): OngoingStubbing[Future[Int]] = {
    when(mockDeversityMicroserviceConnector.getPendingEnrolmentCount(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(if(notZero) ENROLMENTS else NO_ENROLMENTS))
  }

  def mockGetTeacherInfo(fetched: Boolean): OngoingStubbing[Future[Option[TeacherDetails]]] = {
    when(mockDeversityMicroserviceConnector.getTeacherInfo(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(if(fetched) Some(testTeacherDetails) else None))
  }

  def mockGetSchoolInfo(fetched: Boolean): OngoingStubbing[Future[Option[OrgDetails]]] = {
    when(mockDeversityMicroserviceConnector.getSchoolInfo(ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(if(fetched) Some(testOrgDetails) else None))
  }

  def mockGetRegistrationCode: OngoingStubbing[Future[RegistrationCode]] = {
    when(mockDeversityMicroserviceConnector.getRegistrationCode(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(testRegistrationCode))
  }

  def mockGenerateRegistrationCode: OngoingStubbing[Future[HttpResponse.Value]] = {
    when(mockDeversityMicroserviceConnector.generateRegistrationCode(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(HttpResponse.success))
  }

  def mockCreateClassroom: OngoingStubbing[Future[String]] = {
    when(mockDeversityMicroserviceConnector.createClassroom(ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future("testClassRoomName"))
  }

  def mockGetClassrooms(populated: Boolean): OngoingStubbing[Future[Seq[Classroom]]] = {
    when(mockDeversityMicroserviceConnector.getClassrooms(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(if(populated) testClassSeq else Seq()))
  }

  def mockGetClassroom: OngoingStubbing[Future[Classroom]] = {
    when(mockDeversityMicroserviceConnector.getClassroom(ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(testClassroom))
  }

  def mockDeleteClassroom: OngoingStubbing[Future[HttpResponse.Value]] = {
    when(mockDeversityMicroserviceConnector.deleteClassroom(ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(HttpResponse.success))
  }
}
