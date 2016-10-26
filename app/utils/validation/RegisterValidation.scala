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

package utils.validation

import models.UserRegister
import play.api.data.Forms._
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

object RegisterValidation {

  def firstNameChecker : Mapping[String] = {
    val validfirstname = """(^\\w[A-Za-z]{0,29}\\b)""".r
    val firstNameConstraint: Constraint[String] = Constraint("constraints.firstName")({
      text =>
        val error = text match {
          case validfirstname() => Nil
          case "" => Seq(ValidationError("You have not entered your first name"))
          case _ => if (text.length > 30) Seq(ValidationError("The first name you have entered is too long")) else Nil
        }
        if(error.isEmpty) Valid else Invalid(error)
    })
    text().verifying(firstNameConstraint)
  }

  def lastNameChecker : Mapping[String] = {
    val validLastName = """(^\\w[A-Za-z]{0,19}\\b)""".r
    val lastNameConstraint: Constraint[String] = Constraint("constraints.lastName")({
      text =>
        val error = text match {
          case validLastName() => Nil
          case "" => Seq(ValidationError("You have not entered your last name"))
          case _ => if (text.length > 30) Seq(ValidationError("The last name you have entered is too long")) else Nil
        }
        if(error.isEmpty) Valid else Invalid(error)
    })
    text().verifying(lastNameConstraint)
  }

  def userNameChecker : Mapping[String] = {
    val validUserName = """(^\\w[A-Za-z]{0,19}\\b)""".r
    val userNameConstraint: Constraint[String] = Constraint("constraints.userName")({
      text =>
        //TODO : Validate user name
        val inUse = false
        val error = (text, inUse) match {
          case (validUserName(), false) => Nil
          case ("", _) => Seq(ValidationError("You have not entered your user name"))
          case (_, _) => if (text.length > 20) Seq(ValidationError("The user name you have entered is too long")) else Nil
        }
        if (error.isEmpty) Valid else Invalid(error)

    })
    text().verifying(userNameConstraint)
  }

  def emailChecker : Mapping[String] = {
    val validEmail = """[A-Za-z0-9\-_.]{1,126}@[A-Za-z0-9\-_.]{1,126}""".r
    val emailConstraint : Constraint[String] = Constraint("constraints.email")({
      text =>
        //TODO : Validate email address
        val inUse = false
        val error = (text, inUse) match {
          case (validEmail(), false) => Nil
          case ("", _) => Seq(ValidationError("Please enter a valid email address"))
          case (_,_) => if(text.length > 254) Seq(ValidationError("This is not a valid email address")) else Nil
        }
        if(error.isEmpty) Valid else Invalid(error)
    })
    text().verifying(emailConstraint)
  }

  def passwordCheck: Mapping[String] = {
    val passwordCheckConstraint: Constraint[String] = Constraint("constraints.password")({
      text =>
        val error = text match {
          case "" => Seq(ValidationError("You have not entered a password"))
          case _ => Nil
        }
        if(error.isEmpty) Valid else Invalid(error)
    })
    text().verifying(passwordCheckConstraint)
  }

  def confirmPasswordCheck: Mapping[String] = {
    val confirmPasswordCheckConstraint: Constraint[String] = Constraint("constraints.confirmPassword")({
      text =>
        val error = text match {
          case "" => Seq(ValidationError("You have not confirmed your password"))
          case _ => Nil
        }
        if(error.isEmpty) Valid else Invalid(error)
    })
    text().verifying(confirmPasswordCheckConstraint)
  }

//  def orgXPasswordCheck : Constraint[OrgRegister] = {
//    Constraint("constraints.password")({
//      orForm : OrgRegister =>
//        if(orForm.password == orForm.confirmPassword) {
//          Valid
//        } else if(orForm.password.isEmpty) {
//          Invalid(Seq(ValidationError(Messages("cjww.auth.password.error.notfound"))))
//        } else if(orForm.confirmPassword.isEmpty) {
//          Invalid(Seq(ValidationError(Messages("cjww.auth.confirmpassword.error.notfound"))))
//        } else {
//          Invalid(Seq(ValidationError(Messages("cjww.auth.password.error.nomatch"))))
//        }
//    })
//  }

  def xPasswordCheck : Constraint[UserRegister] = {
    Constraint("constraints.password")({
      urForm : UserRegister =>
        if(urForm.password == urForm.confirmPassword) {
          Valid
        } else if(urForm.password.isEmpty) {
          Invalid(Seq(ValidationError("You have not entered a password")))
        } else if(urForm.confirmPassword.isEmpty) {
          Invalid(Seq(ValidationError("You have not confirmed your password")))
        } else {
          Invalid(Seq(ValidationError("The passwords you have entered do not match")))
        }
    })
  }
}
