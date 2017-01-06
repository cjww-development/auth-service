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
package controllers.user

import javax.inject.Inject

import connectors.{AccountConnector, SessionStoreConnector}
import controllers.traits.user.EditProfileCtrl
import play.api.Configuration
import play.api.i18n.MessagesApi
import services.{EditProfileService, FeedService}

class EditProfileController @Inject()(val messagesApi: MessagesApi, configuration: Configuration) extends EditProfileCtrl {
  val sessionStoreConnector = SessionStoreConnector
  val accountConnector = AccountConnector
  val feedEventService = FeedService
  val editProfileService = EditProfileService
}
