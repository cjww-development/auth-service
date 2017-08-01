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

import com.cjwwdev.regex.RegexPack
import play.api.data.Forms.text
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

trait CommonValidation extends RegexPack {
  def userNameValidation: Mapping[String] = {
    val userNameConstraint: Constraint[String] = Constraint("constraints.userName")({ userName =>
      val errors = userName match {
        case userNameRegex() => Nil
        case _               => Seq(ValidationError("You have not entered a valid user name"))
      }
      if(errors.isEmpty) Valid else Invalid(errors)
    })
    text.verifying(userNameConstraint)
  }

  def emailAddressValidation: Mapping[String] = {
    val emailAddressConstraint: Constraint[String] = Constraint("constraints.email")({ email =>
      val errors = email match {
        case emailRegex() => Nil
        case _            => Seq(ValidationError("You have not entered a valid email address"))
      }
      if(errors.isEmpty) Valid else Invalid(errors)
    })
    text.verifying(emailAddressConstraint)
  }

  def passwordValidation: Mapping[String] = {
    val passwordConstraint: Constraint[String] = Constraint("constraints.password")({ password =>
      val errors = password match {
        case passwordRegex()  => Nil
        case ""               => Seq(ValidationError("You have not entered your password"))
        case _                => Seq(ValidationError("You have not entered a valid password"))
      }
      if(errors.isEmpty) Valid else Invalid(errors)
    })
    text.verifying(passwordConstraint)
  }

  def confirmPasswordValidation: Mapping[String] = {
    val confirmPasswordConstraint: Constraint[String] = Constraint("constraints.confirmPassword")({
      text =>
        val errors = text match {
          case passwordRegex()  => Nil
          case ""               => Seq(ValidationError("You have not confirmed your password"))
          case _                => Seq(ValidationError("You have not entered a valid password"))
        }
        if(errors.isEmpty) Valid else Invalid(errors)
    })
    text.verifying(confirmPasswordConstraint)
  }
}
