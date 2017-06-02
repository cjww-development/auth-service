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

package forms

import models.registration.OrgRegistration
import play.api.data.Form
import play.api.data.Forms._
import utils.validation.RegisterValidation._

object OrgRegisterForm {
  val orgRegisterForm = Form(
    mapping(
      "orgName"           -> userNameChecker,
      "initials"          -> initialsChecker,
      "orgUserName"       -> userNameChecker,
      "location"          -> locationChecker,
      "orgEmail"          -> emailChecker,
      "password"          -> passwordCheck,
      "confirmPassword"   -> confirmPasswordCheck
    )(OrgRegistration.apply)(OrgRegistration.unapply).verifying(orgXPasswordCheck)
  )
}
