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

import common.{InvalidOldPassword, MissingBasicDetailsException, PasswordUpdated, UpdatedPasswordResponse}
import connectors.AccountsMicroserviceConnector
import enums.{HttpResponse, Registration}
import helpers.other.Fixtures
import models.accounts.{BasicDetails, Enrolments, Settings}
import models.deversity.{OrgDetails, TeacherDetails}
import models.feed.FeedItem
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MockAccountMicroserviceConnector extends BeforeAndAfterEach with MockitoSugar with Fixtures {
  self: PlaySpec =>

  val mockAccountsMicroserviceConnector = mock[AccountsMicroserviceConnector]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAccountsMicroserviceConnector)
  }

  def mockUpdateProfile(updated: Boolean): OngoingStubbing[Future[HttpResponse.Value]] = {
    when(mockAccountsMicroserviceConnector.updateProfile(ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(if(updated) HttpResponse.success else HttpResponse.failed))
  }

  def mockUpdatePassword(updated: Boolean): OngoingStubbing[Future[UpdatedPasswordResponse]] = {
    when(mockAccountsMicroserviceConnector.updatePassword(ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(if(updated) PasswordUpdated else InvalidOldPassword))
  }

  def mockUpdateSettings(updated: Boolean): OngoingStubbing[Future[HttpResponse.Value]] = {
    when(mockAccountsMicroserviceConnector.updateSettings(ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(if(updated) HttpResponse.success else HttpResponse.failed))
  }

  def mockCreateFeedItem: OngoingStubbing[Future[HttpResponse.Value]] = {
    when(mockAccountsMicroserviceConnector.createFeedItem(ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(HttpResponse.success))
  }

  def mockGetFeedItems(populated: Boolean): OngoingStubbing[Future[Option[List[FeedItem]]]] = {
    when(mockAccountsMicroserviceConnector.getFeedItems(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(if(populated) Some(testFeedList) else None))
  }

  def mockGetBasicDetails(fetched: Boolean): OngoingStubbing[Future[BasicDetails]] = {
    when(mockAccountsMicroserviceConnector.getBasicDetails(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(if(fetched) Future(testBasicDetails) else Future.failed(new MissingBasicDetailsException("")))
  }

  def mockGetEnrolments(fetched: Boolean): OngoingStubbing[Future[Option[Enrolments]]] = {
    when(mockAccountsMicroserviceConnector.getEnrolments(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(if(fetched) Some(testEnrolments) else None))
  }

  def mockGetSettings(notDefault: Boolean): OngoingStubbing[Future[Settings]] = {
    when(mockAccountsMicroserviceConnector.getSettings(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(if(notDefault) testSettings else Settings.default))
  }

  def mockGetOrgBasicDetails(fetched: Boolean): OngoingStubbing[Future[Option[OrgDetails]]] = {
    when(mockAccountsMicroserviceConnector.getOrgBasicDetails(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(if(fetched) Some(testOrgDetails) else None))
  }

  def mockGetTeacherList(populated: Boolean): OngoingStubbing[Future[List[TeacherDetails]]] = {
    when(mockAccountsMicroserviceConnector.getTeacherList(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(if(populated) testTeacherDetailsList else List()))
  }

  def mockCreateNewIndividualUser(created: Boolean): OngoingStubbing[Future[Registration.Value]] = {
    when(mockAccountsMicroserviceConnector.createNewIndividualUser(ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(if(created) Registration.success else Registration.failed))
  }

  def mockCreateNewOrgUser(created: Boolean): OngoingStubbing[Future[Registration.Value]] = {
    when(mockAccountsMicroserviceConnector.createNewOrgUser(ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(if(created) Registration.success else Registration.failed))
  }

  def mockCheckUserName(inUse: Boolean): OngoingStubbing[Future[Boolean]] = {
    when(mockAccountsMicroserviceConnector.checkUserName(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future(inUse))
  }

  def mockCheckEmailAddress(inUse: Boolean): OngoingStubbing[Future[Boolean]] = {
    when(mockAccountsMicroserviceConnector.checkEmailAddress(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future(inUse))
  }
}
