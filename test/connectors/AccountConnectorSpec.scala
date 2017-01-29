// Copyright (C) 2011-2012 the original author or authors.
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

package connectors

import config.FrontendConfiguration
import mocks.CJWWSpec
import models.accounts._
import org.joda.time.DateTime
import utils.security.DataSecurity
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers
import play.api.test.Helpers._

import scala.concurrent.Future

class AccountConnectorSpec extends CJWWSpec {

  val successResp = mockWSResponseWithBody(DataSecurity.encryptData(testContext).get)
  val okResp = mockWSResponse(OK)
  val insResp = mockWSResponse(INTERNAL_SERVER_ERROR)
  val conflictResp = mockWSResponse(CONFLICT)

  val successListResp = mockWSResponseWithBody(testEncFeedList)
  val failedListResp = mockWSResponseWithBody("INVALID_PAYLOAD")

  val mockConfig : FrontendConfiguration = mock[FrontendConfiguration]

  val testConnector = new AccountConnector(mockHttpVerbs, mockConfig)

  "getContext" should {
    "return some context" in {
      when(mockHttpVerbs.getUrl(ArgumentMatchers.any()))
        .thenReturn(Future.successful(successResp))

      val result = await(testConnector.getContext("context-1234567890"))
      result mustBe Some(testContext)
    }
  }

  "updateProfile" should {
    "return an OK" in {

      val testUserProfile =
        UserProfile(
          "testFirstName",
          "testLastName",
          "testUserName",
          "test@email.com"
        )

      when(mockHttpVerbs.post(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(okResp))

      val result = await(testConnector.updateProfile(testUserProfile))
      result mustBe OK
    }
  }

  "updatePassword" should {

    val testPasswordSet = PasswordSet("user-1234567890", "testPass", "testPassNew")

    "return an UpdatedSettingsSuccess" in {
      when(mockHttpVerbs.post(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(okResp))

      val result = await(testConnector.updatePassword(testPasswordSet))
      result mustBe PasswordUpdated
    }

    "return an UpdatedSettingsFailed" in {
      when(mockHttpVerbs.post(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(conflictResp))

      val result = await(testConnector.updatePassword(testPasswordSet))
      result mustBe InvalidOldPassword
    }
  }

  "updateSettings" should {

    val testAccountSettings =
      AccountSettings(
        "user-1234567890",
        Map(
          "displayName" -> "user",
          "displayNameColour" -> "#124AAO",
          "displayImageURL" -> "test/link"
        )
      )

    "return an UpdatedSettingsSuccess" in {
      when(mockHttpVerbs.post(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(okResp))

      val result = await(testConnector.updateSettings(testAccountSettings))
      result mustBe UpdatedSettingsSuccess
    }

    "return an UpdatedSettingsFailed" in {
      when(mockHttpVerbs.post(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(insResp))

      val result = await(testConnector.updateSettings(testAccountSettings))
      result mustBe UpdatedSettingsFailed
    }
  }

  "createFeedItem" should {

    val testFeedItem =
      FeedItem(
        "user-1234567890",
        SourceDetail(
          "testService",
          "testLocation"
        ),
        EventDetail(
          "testTitle",
          "testDescription"
        ),
        DateTime.now()
      )

    "return a FeedEventSuccessResponse" in {
      when(mockHttpVerbs.post(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(okResp))

      val result = await(testConnector.createFeedItem(testFeedItem))
      result mustBe FeedEventSuccessResponse
    }

    "return a FeedEventSuccessFailed" in {
      when(mockHttpVerbs.post(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(insResp))

      val result = await(testConnector.createFeedItem(testFeedItem))
      result mustBe FeedEventFailedResponse
    }
  }

  "getFeedItems" should {
    "return none if the list can't be decrypted" in {
      when(mockHttpVerbs.get[String](ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(failedListResp))

      val result = await(testConnector.getFeedItems("user-1234567890"))
      result mustBe None
    }

    "return some list of feed items if the body can be decrypted" in {
      when(mockHttpVerbs.get[String](ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(successListResp))

      val result = await(testConnector.getFeedItems("user-1234567890"))
      result.getClass mustBe classOf[Some[List[FeedItem]]]
    }
  }

  "getBasicDetails" should {

    val successBasicDetails = mockWSResponseWithBody(DataSecurity.encryptData[BasicDetails](testBasicDetails).get)
    val failedBasicDetails = mockWSResponseWithBody("INVALID_PAYLOAD")

    "return some basic details" in {
      when(mockHttpVerbs.getUrl(ArgumentMatchers.any()))
        .thenReturn(Future.successful(successBasicDetails))

      val result = await(testConnector.getBasicDetails)
      result mustBe Some(testBasicDetails)
    }

    "return none" in {
      when(mockHttpVerbs.getUrl(ArgumentMatchers.any()))
        .thenReturn(Future.successful(failedBasicDetails))

      val result = await(testConnector.getBasicDetails)
      result mustBe None
    }
  }

  "getEnrolments" should {

    val successEnrolments = mockWSResponseWithBody(DataSecurity.encryptData[Enrolments](testEnrolments).get)
    val failedEnrolments = mockWSResponseWithBody("INVALID_PAYLOAD")

    "return some enrolments" in {
      when(mockHttpVerbs.getUrl(ArgumentMatchers.any()))
        .thenReturn(Future.successful(successEnrolments))

      val result = await(testConnector.getEnrolments)
      result mustBe Some(testEnrolments)
    }

    "return none" in {
      when(mockHttpVerbs.getUrl(ArgumentMatchers.any()))
        .thenReturn(Future.successful(failedEnrolments))

      val result = await(testConnector.getEnrolments)
      result mustBe None
    }
  }

  "getSettings" should {
    val successSettings = mockWSResponseWithBody(DataSecurity.encryptData[Settings](testSettings).get)
    val failedSettings = mockWSResponseWithBody("INVALID_PAYLOAD")

    "return some settings" in {
      when(mockHttpVerbs.getUrl(ArgumentMatchers.any()))
        .thenReturn(Future.successful(successSettings))

      val result = await(testConnector.getSettings)
      result mustBe Some(testSettings)
    }

    "return none" in {
      when(mockHttpVerbs.getUrl(ArgumentMatchers.any()))
        .thenReturn(Future.successful(failedSettings))

      val result = await(testConnector.getSettings)
      result mustBe None
    }
  }
}
