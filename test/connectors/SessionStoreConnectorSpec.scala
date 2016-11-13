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

import mocks.MockResponse
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import utils.httpverbs.HttpVerbs
import play.api.test.Helpers._
import org.mockito.Mockito._
import org.mockito.Matchers
import play.api.libs.json.Json
import security.JsonSecurity

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class SessionStoreConnectorSpec extends PlaySpec with OneAppPerSuite with MockitoSugar with MockResponse {

  case class TestModel(string : String, int : Int)
  implicit val format = Json.format[TestModel]

  val mockHttp = mock[HttpVerbs]

  val successResponse = mockWSResponse(statusCode = CREATED)
  val okResponse = mockWSResponse(statusCode = OK)

  val data = TestModel("string", 1)
  val getDataResponse = mockWSResponse(statusCode = CREATED, body = JsonSecurity.encryptModel[TestModel](data).get)

  class Setup {
    object TestConnector extends SessionStoreConnector {
      val http = mockHttp
    }
  }

  "cache" should {
    "return CREATED" when {
      "posting a session data to mongo" in new Setup {
        when(mockHttp.cache[String](Matchers.any(), Matchers.any(), Matchers.any())(Matchers.any()))
          .thenReturn(Future.successful(successResponse))

        val result = Await.result(TestConnector.cache("sessionID","testData"), 5.seconds)
        result.status mustBe CREATED
      }
    }
  }

  "destroySession" should {
    "return OK" when {
      "getting with session id" in new Setup {
        when(mockHttp.destroySession(Matchers.any(), Matchers.any())(Matchers.any()))
          .thenReturn(Future.successful(okResponse))

        val result = Await.result(TestConnector.destroySession("sessionID"), 5.seconds)
        result.status mustBe OK
      }
    }
  }
}
