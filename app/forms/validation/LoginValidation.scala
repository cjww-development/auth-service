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

import play.api.data.Forms.text
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

object LoginValidation {
  def userNameValidation: Mapping[String] = {
    val userNameConstraint: Constraint[String] = Constraint("constraints.userName")({ userName =>
      val errors = userName match {
        case "" => Seq(ValidationError("You have not entered your user name"))
        case _  => Nil
      }
      if(errors.isEmpty) Valid else Invalid(errors)
    })
    text.verifying(userNameConstraint)
  }

  def passwordValidation: Mapping[String] = {
    val passwordConstraint: Constraint[String] = Constraint("constraints.password")({ password =>
      val errors = password match {
        case "" => Seq(ValidationError("You have not entered your password"))
        case _  => Nil
      }
      if(errors.isEmpty) Valid else Invalid(errors)
    })
    text.verifying(passwordConstraint)
  }
}
