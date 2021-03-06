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

package connectors.test

import com.cjwwdev.http.verbs.Http
import helpers.connectors.ConnectorSpec

import scala.concurrent.ExecutionContext.Implicits.global

class TeardownConnectorSpec extends ConnectorSpec {

  val testConnector = new TeardownConnector {
    override val accounts: String = "/test/accounts"
    override val http: Http       = mockHttp
  }

  "deleteTestAccountInstance" should {
    "return an Ok" when {
      "the test account has been torn down (individual)" in {
        mockHttpDelete(response = fakeHttpResponse(OK))

        awaitAndAssert(testConnector.deleteTestAccountInstance("testUserName", "individual")) {
          _.status mustBe OK
        }
      }

      "the test account has been torn down (organisation)" in {
        mockHttpDelete(response = fakeHttpResponse(OK))

        awaitAndAssert(testConnector.deleteTestAccountInstance("testUserName", "organisation")) {
          _.status mustBe OK
        }
      }
    }
  }
}
