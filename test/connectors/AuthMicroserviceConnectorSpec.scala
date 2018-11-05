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

import com.cjwwdev.http.exceptions.ForbiddenException
import com.cjwwdev.implicits.ImplicitDataSecurity._
import helpers.connectors.ConnectorSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthMicroserviceConnectorSpec extends ConnectorSpec {

  val testConnector = new AuthMicroserviceConnector {
    override val http = mockHttp
  }

  "getUser" should {
    "return a CurrentUser" when {
      "a user has been found" in {
        mockHttpGet(response = Future(fakeHttpResponse(OK, testCurrentUser.encrypt)))

        awaitAndAssert(testConnector.getUser(testUserLogin)) {
          _ mustBe Some(testCurrentUser)
        }
      }
    }

    "throw a ForbiddenException" when {
      "no user could be found" in {
        mockHttpGet(response = Future.failed(new ForbiddenException("")))

        awaitAndAssert(testConnector.getUser(testUserLogin)) {
          _ mustBe None
        }
      }
    }
  }
}
