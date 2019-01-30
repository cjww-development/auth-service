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

package services

import connectors.{AuthMicroserviceConnector, SessionStoreConnector}
import helpers.services.ServiceSpec

import scala.concurrent.ExecutionContext.Implicits.global

class LoginServiceSpec extends ServiceSpec {

  val testService = new LoginService {
    override val authConnector: AuthMicroserviceConnector = mockAuthMicroserviceConnector
    override val sessionStoreConnector: SessionStoreConnector = mockSessionStoreConnector
    override val generateSessionId: String = generateTestSystemId(SESSION)
  }

  "processLoginAttempt" should {
    "return an Org session" when {
      "the credentials were validated as an org credential" in {
        mockGetOrgUser(fetched = true)

        mockCache(cached = true)

        mockUpdateSession(updated = true)

        awaitAndAssert(testService.processLoginAttempt(testUserLogin)) {
          _.get.data mustBe orgSession
        }
      }
    }

    "return an Individual session" when {
      "the credentials were validated as an individual credential" in {
        mockGetIndividualUser(fetched = true)

        mockCache(cached = true)

        mockUpdateSession(updated = true)

        awaitAndAssert(testService.processLoginAttempt(testUserLogin)) {
          _.get.data mustBe individualSession
        }
      }
    }

    "return None" when {
      "the credentials could not be validated" in {
        mockGetIndividualUser(fetched = false)

        awaitAndAssert(testService.processLoginAttempt(testUserLogin)) {
          _ mustBe None
        }
      }
    }
  }
}
