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

import models.registration.OrgRegistration
import play.api.data.Forms.text
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

object OrgRegistrationValidation extends CommonValidation {

  private val initialsRegex = """^[A-Z]{1,5}$""".r
  private val locationRegex = """^\w[A-Za-z-]{0,49}$""".r
  private val orgNameRegex  = """^\w[A-Za-z-]{0,49}$""".r

  def orgNameValidation: Mapping[String] = {
    val orgNameConstraint: Constraint[String] = Constraint("constraints.orgName")({ orgName =>
      val errors = orgName match {
        case orgNameRegex() => Nil
        case _              => Seq(ValidationError("You have not entered a valid organisation name"))
      }
      if(errors.isEmpty) Valid else Invalid(errors)
    })
    text.verifying(orgNameConstraint)
  }

  def initialsValidation: Mapping[String] = {
    val initialsConstraint: Constraint[String] = Constraint("constraints.initials")({ initials =>
      val errors = initials.toUpperCase match {
        case initialsRegex() => Nil
        case _               => Seq(ValidationError("Enter five characters or less for your initials"))
      }
      if(errors.isEmpty) Valid else Invalid(errors)
    })
    text.verifying(initialsConstraint)
  }

  def locationValidation: Mapping[String] = {
    val locationConstraint: Constraint[String] = Constraint("constraints.location")({ location =>
      val errors = location match {
        case locationRegex() => Nil
        case _               => Seq(ValidationError("You have not entered a valid location"))
      }
      if(errors.isEmpty) Valid else Invalid(errors)
    })
    text.verifying(locationConstraint)
  }

  def orgXPasswordValidation: Constraint[OrgRegistration] = Constraint("constraints.password")({ orForm =>
    if(orForm.password == orForm.confirmPassword) {
      Valid
    } else if(orForm.password.isEmpty) {
      Invalid(Seq(ValidationError("You have not entered a password")))
    } else if(orForm.confirmPassword.isEmpty) {
      Invalid(Seq(ValidationError("You have not confirmed your password")))
    } else {
      Invalid(Seq(ValidationError("The passwords you have entered don't match")))
    }
  })
}
