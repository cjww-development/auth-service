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

package connectors

import com.cjwwdev.http.verbs.Http
import com.cjwwdev.implicits.ImplicitDataSecurity._
import helpers.connectors.ConnectorSpec

import scala.concurrent.ExecutionContext.Implicits.global

class DeversityMicroserviceConnectorSpec extends ConnectorSpec {

  val testConnector = new DeversityConnector {
    override val deversity: String = "/test/deversity"
    override val http: Http        = mockHttp
  }

  "getDeversityEnrolment" should {
    "return some DeversityEnrolment" when {
      "the response has an Ok status" in {
        mockHttpGet(response = fakeHttpResponse(OK, testTeacherEnrolment.encrypt))

        awaitAndAssert(testConnector.getDeversityEnrolment) {
          _ mustBe Some(testTeacherEnrolment)
        }
      }
    }

    "return None" when {
      "the response has a NoContent status" in {
        mockHttpGet(response = fakeHttpResponse(NO_CONTENT))

        awaitAndAssert(testConnector.getDeversityEnrolment) {
          _ mustBe None
        }
      }

      "the response has NotFound status" in {
        mockHttpGet(response = fakeHttpResponse(NOT_FOUND))

        awaitAndAssert(testConnector.getDeversityEnrolment) {
          _ mustBe None
        }
      }
    }
  }

  "getTeacherInfo" should {
    "return some TeacherInfo" in {
      mockHttpGet(response = fakeHttpResponse(OK, testTeacherDetails.encrypt))

      awaitAndAssert(testConnector.getTeacherInfo("testTeacher", "testSchool")) {
        _ mustBe Some(testTeacherDetails)
      }
    }

    "return None" in {
      mockHttpGet(response = fakeHttpResponse(NOT_FOUND))

      awaitAndAssert(testConnector.getTeacherInfo("testTeacher", "testSchool")) {
        _ mustBe None
      }
    }
  }

  "getSchoolInfo" should {
    "return some OrgDetails" in {
      mockHttpGet(response = fakeHttpResponse(OK, testOrgDetails.encrypt))

      awaitAndAssert(testConnector.getSchoolInfo("testSchool")) {
        _ mustBe Some(testOrgDetails)
      }
    }

    "return None" in {
      mockHttpGet(response = fakeHttpResponse(NOT_FOUND))

      awaitAndAssert(testConnector.getSchoolInfo("testSchool")) {
        _ mustBe None
      }
    }
  }
}
