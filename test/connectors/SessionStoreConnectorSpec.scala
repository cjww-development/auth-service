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

import com.cjwwdev.http.exceptions.{ClientErrorException, NotFoundException, ServerErrorException}
import com.cjwwdev.implicits.ImplicitDataSecurity._
import enums.SessionCache
import helpers.connectors.ConnectorSpec
import models.SessionUpdateSet

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SessionStoreConnectorSpec extends ConnectorSpec {

  val testConnector = new SessionStoreConnector {
    override val http = mockHttp
  }

  "cache" should {
    "return a cached" when {
      "the session has been initialised" in {
        mockHttpPostString(response = Future(fakeHttpResponse(OK)))

        awaitAndAssert(testConnector.cache(generateTestSystemId(SESSION))) {
          _ mustBe SessionCache.cached
        }
      }
    }

    "return a cacheFailure" when {
      "there was a problem initialising the session" in {
        mockHttpPostString(response = Future.failed(new ServerErrorException("", INTERNAL_SERVER_ERROR)))

        awaitAndAssert(testConnector.cache(generateTestSystemId(SESSION))) {
          _ mustBe SessionCache.cacheFailure
        }
      }
    }
  }

  "getDataElement" should {
    "return some TestModel" when {
      "data has been found matching the key" in {
        val testModel = TestModel("test", 616)

        mockHttpGet(response = Future(fakeHttpResponse(OK, testModel.encrypt)))

        awaitAndAssert(testConnector.getDataElement[TestModel]("testKey")) {
          _ mustBe Some(testModel)
        }
      }
    }

    "return None" when {
      "no data has been found matching the key" in {
        mockHttpGet(response = Future.failed(new NotFoundException("")))

        awaitAndAssert(testConnector.getDataElement[TestModel]("testKey")) {
          _ mustBe None
        }
      }
    }
  }

  "updateSession" should {
    "return a cacheUpdated" when {
      "the session has been updated" in {
        mockHttpPatch(response = Future(fakeHttpResponse(OK)))

        awaitAndAssert(testConnector.updateSession(SessionUpdateSet("testKey", "testData"), None)) {
          _ mustBe SessionCache.cacheUpdated
        }
      }
    }

    "return a cacheUpdateFailure" when {
      "there was a problem updating the session" in {
        mockHttpPatch(response = Future.failed(new ServerErrorException("", INTERNAL_SERVER_ERROR)))

        awaitAndAssert(testConnector.updateSession(SessionUpdateSet("testKey", "testData"), None)) {
          _ mustBe SessionCache.cacheUpdateFailure
        }
      }
    }
  }

  "destroySession" should {
    "return a cacheDestroyed" when {
      "the response contains an Ok" in {
        mockHttpDelete(response = Future(fakeHttpResponse(OK)))

        awaitAndAssert(testConnector.destroySession) {
          _ mustBe SessionCache.cacheDestroyed
        }
      }

      "the response contains an BadRequest" in {
        mockHttpDelete(response = Future.failed(new ClientErrorException("", BAD_REQUEST)))

        awaitAndAssert(testConnector.destroySession) {
          _ mustBe SessionCache.cacheDestroyed
        }
      }
    }

    "return a cacheDestructionFailure" when {
      "the response contains an InternalServerError" in {
        mockHttpDelete(response = Future.failed(new ServerErrorException("", INTERNAL_SERVER_ERROR)))

        awaitAndAssert(testConnector.destroySession) {
          _ mustBe SessionCache.cacheDestructionFailure
        }
      }
    }
  }
}
