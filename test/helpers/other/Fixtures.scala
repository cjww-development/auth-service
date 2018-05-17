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
package helpers.other

import com.cjwwdev.auth.models.CurrentUser
import models.accounts._
import models.deversity.{Classroom, OrgDetails, TeacherDetails}
import models.feed.{EventDetail, FeedItem, SourceDetail}
import models.registration.OrgRegistration
import models.{RegistrationCode, UserLogin}
import org.joda.time.DateTime
import play.api.libs.json.Json

trait Fixtures extends TestDataGenerator with MockRequest {
  val testOrgDevId = generateTestSystemId(DEVERSITY)

  val testOrgCurrentUser = CurrentUser(
    contextId       = generateTestSystemId(CONTEXT),
    id              = generateTestSystemId(ORG),
    orgDeversityId  = Some(generateTestSystemId(DEVERSITY)),
    credentialType  = "organisation",
    orgName         = Some("testOrgName"),
    firstName       = None,
    lastName        = None,
    role            = None,
    enrolments      = None
  )

  implicit val testCurrentUser = CurrentUser(
    contextId       = generateTestSystemId(CONTEXT),
    id              = generateTestSystemId(USER),
    orgDeversityId  = Some(generateTestSystemId(DEVERSITY)),
    credentialType  = "individual",
    orgName         = None,
    firstName       = Some("testFirstName"),
    lastName        = Some("testLastName"),
    role            = None,
    enrolments      = Some(Json.obj(
      "deversityId" -> generateTestSystemId(DEVERSITY)
    ))
  )

  val testTeacherEnrolment: DeversityEnrolment = {
    DeversityEnrolment(
      schoolDevId     = testOrgDevId,
      role            = "teacher",
      title           = Some("testTitle"),
      room            = Some("testRoom"),
      teacher         = None
    )
  }

  val testStudentEnrolment: DeversityEnrolment = {
    DeversityEnrolment(
      schoolDevId     = testOrgDevId,
      role            = "student",
      title           = None,
      room            = None,
      teacher         = Some(createTestUserName)
    )
  }

  val testBasicDetails = BasicDetails(
    firstName = "testFirstName",
    lastName  = "testLastName",
    userName  = "testUserName",
    email     = "test@email.com",
    createdAt = now
  )

  val testEnrolments = Enrolments(
    deversityId = Some(generateTestSystemId(DEVERSITY)),
    hubId       = None,
    diagId      = None
  )

  val testSettings = Settings(
    displayName       = "full",
    displayNameColour = "#000000",
    displayImageURL   = ""
  )

  val testOrgAccount = OrgRegistration(
    orgName         = "testSchoolName",
    initials        = "TSN",
    orgUserName     = "tSchoolName",
    location        = "testLocation",
    orgEmail        = "test@email.com",
    password        = "testPass",
    confirmPassword = "testPass"
  )

  val testOrgDetails = OrgDetails(
    orgName  = testOrgAccount.orgName,
    initials = testOrgAccount.initials,
    location = testOrgAccount.location
  )

  val testTeacherDetails = TeacherDetails(
    userId   = generateTestSystemId(USER),
    title    = "testTitle",
    lastName = "testLastName",
    room     = "testRoom",
    status   = "pending"
  )

  val testTeacherDetailsList = List(testTeacherDetails)

  val testFeedItem = FeedItem(
    userId = generateTestSystemId(USER),
    sourceDetail = SourceDetail(
      service   = "auth-service",
      location  = "testLocation"
    ),
    eventDetail = EventDetail(
      title       = "testTitle",
      description = "testDesc"
    ),
    generated = now
  )

  val testFeedItem2 = FeedItem(
    "testUserId2",
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

  val testFeedList = List(testFeedItem, testFeedItem2)

  val testFeedArray = Json.obj("feed-array" -> Json.toJson(testFeedList))

  val testUserProfile = UserProfile(
    firstName = "testFirstName",
    lastName  = "testLastName",
    userName  = "tUserName",
    email     = "test@email.com"
  )

  val testRegistrationCode = RegistrationCode(
    identifier  = generateTestSystemId(DEVERSITY),
    code        = "testCode",
    createdAt   = now
  )

  val testClassroom = Classroom(
    classId = generateTestSystemId("class"),
    name    = "testClassroomName"
  )

  val testClassroom2 = Classroom(
    classId = generateTestSystemId("class"),
    name    = "testClassroomName2"
  )

  val testClassSeq = Seq(testClassroom, testClassroom2)

  val testUserLogin = UserLogin(
    username = "testUserName",
    password = "testPassword"
  )

  val individualSession = Map(
    "cookieId"        -> generateTestSystemId(SESSION),
    "firstName"       -> "testFirstName",
    "lastName"        -> "testLastName",
    "credentialType"  -> "individual",
    ""                -> ""
  )

  val orgSession = Map(
    "cookieId"        -> generateTestSystemId(SESSION),
    "orgName"         -> "testOrgName",
    "credentialType"  -> "organisation"
  )
}
