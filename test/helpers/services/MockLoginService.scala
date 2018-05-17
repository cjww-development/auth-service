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

package helpers.services

import helpers.other.Fixtures
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.mvc.Session
import services.LoginService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MockLoginService extends BeforeAndAfterEach with MockitoSugar with Fixtures {
  self: PlaySpec =>

  val mockLoginService: LoginService = mock[LoginService]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockLoginService)
  }

  def mockProcessIndividualLoginAttempt(success: Boolean): OngoingStubbing[Future[Option[Session]]] = {
    when(mockLoginService.processLoginAttempt(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(if(success) Future(Some(Session(data = individualSession))) else Future(None))
  }

  def mockProcessOrgLoginAttempt(success: Boolean): OngoingStubbing[Future[Option[Session]]] = {
    when(mockLoginService.processLoginAttempt(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(if(success) Future(Some(Session(data = orgSession))) else Future(None))
  }
}
