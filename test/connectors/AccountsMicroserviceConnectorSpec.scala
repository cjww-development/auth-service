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

import enums.HttpResponse
import helpers.connectors.ConnectorSpec

import scala.concurrent.ExecutionContext.Implicits.global

class AccountsMicroserviceConnectorSpec extends ConnectorSpec {

  val testConnector = new AccountsMicroserviceConnector {
    override val http = mockHttp
  }

  "updateProfile" should {
    "return a Http success" when {
      "the update returns an Ok response" in {
        mockHttpPatch(response = fakeHttpResponse(OK))

        awaitAndAssert(testConnector.updateProfile(testUserProfile)) {
          _ mustBe HttpResponse.success
        }
      }
    }

    "return a Http failed" when {
      "the update doesn't return an Ok response" in {
        mockHttpPatch(response = fakeHttpResponse(INTERNAL_SERVER_ERROR))

        awaitAndAssert(testConnector.updateProfile(testUserProfile)) {
          _ mustBe HttpResponse.failed
        }
      }
    }
  }
}
