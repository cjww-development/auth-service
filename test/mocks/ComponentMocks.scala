// Copyright (C) 2016-2017 the original author or authors.
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

import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.http.verbs.Http
import connectors.SessionStoreConnector
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito.reset
import services.{EditProfileService, FeedService, LoginService}

trait ComponentMocks {
  this: MockitoSugar =>

  val mockSessionStoreConnector : SessionStoreConnector = mock[SessionStoreConnector]
  val mockEditProfileService : EditProfileService = mock[EditProfileService]
  val mockFeedEventService : FeedService = mock[FeedService]
  val mockHttpVerbs : Http = mock[Http]
  val mockLoginService : LoginService = mock[LoginService]
  val mockConfig = mock[ConfigurationLoader]

  def resetMocks() : Unit = {
    reset(mockSessionStoreConnector)
    reset(mockEditProfileService)
    reset(mockFeedEventService)
    reset(mockHttpVerbs)
    reset(mockLoginService)
  }
}
