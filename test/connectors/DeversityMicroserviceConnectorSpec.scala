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

package connectors

import com.cjwwdev.http.exceptions.NotFoundException
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.security.encryption.DataSecurity
import enums.HttpResponse
import helpers.connectors.ConnectorSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DeversityMicroserviceConnectorSpec extends ConnectorSpec {

  val testConnector = new DeversityMicroserviceConnector {
    override val http = mockHttp
  }

  "getDeversityEnrolment" should {
    "return some DeversityEnrolment" when {
      "the response has an Ok status" in {
        mockHttpGet(response = Future(fakeHttpResponse(OK, testTeacherEnrolment.encryptType)))

        awaitAndAssert(testConnector.getDeversityEnrolment) {
          _ mustBe Some(testTeacherEnrolment)
        }
      }
    }

    "return None" when {
      "the response has a NoContent status" in {
        mockHttpGet(response = Future(fakeHttpResponse(NO_CONTENT)))

        awaitAndAssert(testConnector.getDeversityEnrolment) {
          _ mustBe None
        }
      }

      "the response has NotFound status" in {
        mockHttpGet(response = Future.failed(new NotFoundException("")))

        awaitAndAssert(testConnector.getDeversityEnrolment) {
          _ mustBe None
        }
      }
    }
  }

  "getTeacherInfo" should {
    "return some TeacherInfo" in {
      mockHttpGet(response = Future(fakeHttpResponse(OK, testTeacherDetails.encryptType)))

      awaitAndAssert(testConnector.getTeacherInfo("testTeacher", "testSchool")) {
        _ mustBe Some(testTeacherDetails)
      }
    }

    "return None" in {
      mockHttpGet(response = Future.failed(new NotFoundException("")))

      awaitAndAssert(testConnector.getTeacherInfo("testTeacher", "testSchool")) {
        _ mustBe None
      }
    }
  }

  "getSchoolInfo" should {
    "return some OrgDetails" in {
      mockHttpGet(response = Future(fakeHttpResponse(OK, testOrgDetails.encryptType)))

      awaitAndAssert(testConnector.getSchoolInfo("testSchool")) {
        _ mustBe Some(testOrgDetails)
      }
    }

    "return None" in {
      mockHttpGet(response = Future.failed(new NotFoundException("")))

      awaitAndAssert(testConnector.getSchoolInfo("testSchool")) {
        _ mustBe None
      }
    }
  }

  "getRegistrationCode" should {
    "return some OrgDetails" in {
      mockHttpGet(response = Future(fakeHttpResponse(OK, testRegistrationCode.encryptType)))

      awaitAndAssert(testConnector.getRegistrationCode) {
        _ mustBe testRegistrationCode
      }
    }
  }

  "generateRegistrationCode" should {
    "return a success" when {
      "a code has been generated" in {
        mockHttpHead(response = Future(fakeHttpResponse(OK)))

        awaitAndAssert(testConnector.generateRegistrationCode) {
          _ mustBe HttpResponse.success
        }
      }
    }
  }

  "createClassroom" should {
    "return the name of the created classroom" in {
      mockHttpPostString(response = Future(fakeHttpResponse(OK)))

      awaitAndAssert(testConnector.createClassroom("testClassRoom")) {
        _ mustBe "testClassRoom"
      }
    }
  }

  "getClassrooms" should {
    "return a seq of class rooms" in {
      mockHttpGet(response = Future(fakeHttpResponse(OK, DataSecurity.encryptType(testClassSeq))))

      awaitAndAssert(testConnector.getClassrooms) {
        _ mustBe testClassSeq
      }
    }
  }
}
