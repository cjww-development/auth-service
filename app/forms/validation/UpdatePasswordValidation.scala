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

package forms.validation

import models.accounts.PasswordSet
import play.api.data.Forms._
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

object UpdatePasswordValidation extends CommonValidation {
  def oldPasswordValidation: Mapping[String] = {
    val passwordCheckConstraint: Constraint[String] = Constraint("constraints.oldPassword")({ password =>
      val errors = password match {
        case passwordRegex()  => Nil
        case ""               => Seq(ValidationError("You have not entered your old password"))
        case _                => Seq(ValidationError("You have not entered a valid password"))
      }
      if(errors.isEmpty) Valid else Invalid(errors)
    })
    text.verifying(passwordCheckConstraint)
  }

  def profileXPasswordValidation: Constraint[PasswordSet] = Constraint("constraints.password")({ pForm =>
    if(pForm.newPassword == pForm.confirmPassword) {
      Valid
    } else if(pForm.newPassword.isEmpty) {
      Invalid(Seq(ValidationError("You have not entered a password")))
    } else if(pForm.confirmPassword.isEmpty) {
      Invalid(Seq(ValidationError("You have not confirmed your password")))
    } else {
      Invalid(Seq(ValidationError("The passwords you have entered don't match")))
    }
  })
}
