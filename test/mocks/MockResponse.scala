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

import models.accounts.UserAccount
import models.UserLogin
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import play.api.libs.ws.WSResponse
import security.JsonSecurity

trait MockResponse extends MockitoSugar{

  def mockWSResponse(statusCode : Int, body : String = "") : WSResponse = {
    val m = mock[WSResponse]
    when(m.status).thenReturn(statusCode)
    when(m.body).thenReturn(body)
    m
  }

  def mockWSResponseWithBody(data : String) : WSResponse = {
    val m = mock[WSResponse]
    when(m.body).thenReturn(data)
    m
  }

  val testUserCredentials = UserLogin("testUserName","testPassword")
  val testUserDetails = UserAccount(Some("testID"),"testFirstName","testLastName","testUserName","test@email.com","testPassword")

  val encryptedUserDetails = JsonSecurity.encryptModel[UserAccount](testUserDetails)
}
