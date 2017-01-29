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

package mocks

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import config.WSConfiguration
import connectors.AccountConnector
import models.auth.AuthContext
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.{Args, BeforeAndAfterEach, Status, Suite}
import org.scalatest.mock.MockitoSugar
import play.api.libs.ws.ahc.AhcWSClient
import play.api.mvc.{Action, AnyContent, AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import utils.httpverbs.HttpVerbs
import utils.security.DataSecurity

import scala.concurrent.Future

trait AuthMocks
  extends SessionBuild
    with MockitoSugar
    with MockResponse
    with MockModels
    with ComponentMocks
    with BeforeAndAfterEach
    with Suite
    with WSConfiguration {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val ws = AhcWSClient()

  abstract override def runTest(testName: String, args: Args): Status = super.runTest(testName, args)

  override def beforeEach() : Unit = {
    resetMocks()
  }

  def showWithAuthorisedUser(action: Action[AnyContent],
                             mockAccountConnector: AccountConnector,
                             mockHttp : HttpVerbs,
                             context: AuthContext)(test: Future[Result] => Any) {
    val request = buildRequestWithSession
    val mockResponse = mockWSResponseWithBody(DataSecurity.encryptData[AuthContext](context).get)

    when(mockHttp.getUrl(ArgumentMatchers.any()))
      .thenReturn(Future.successful(mockResponse))

    when(mockAccountConnector.getContext(ArgumentMatchers.any()))
      .thenReturn(Future.successful(Some(context)))

    val result = action.apply(request)
    test(result.run())
  }

  def submitWithAuthorisedUser(action: Action[AnyContent],
                               mockAccountConnector: AccountConnector,
                               request: FakeRequest[AnyContentAsFormUrlEncoded],
                               mockHttp : HttpVerbs,
                               context : AuthContext)(test: Future[Result] => Any) {

    val mockResponse = mockWSResponseWithBody(DataSecurity.encryptData[AuthContext](context).get)

    when(mockHttp.getUrl(ArgumentMatchers.any()))
      .thenReturn(Future.successful(mockResponse))

    when(mockAccountConnector.getContext(ArgumentMatchers.any()))
      .thenReturn(Future.successful(Some(context)))

    val result = action.apply(request)
    test(result)
  }
}
