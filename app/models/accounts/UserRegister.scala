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

package models.accounts

import play.api.libs.json.Json
import security.Encryption.sha512

case class UserRegister(firstName: String,
                        lastName: String,
                        userName: String,
                        email: String,
                        password: String,
                        confirmPassword: String) {

  def encryptPasswords : UserRegister = {
    this.copy(password = sha512(password), confirmPassword = sha512(confirmPassword))
  }
}

object UserRegister {
  implicit val format = Json.format[UserRegister]

  def empty : UserRegister = UserRegister("","","","","","")
}