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


package utils.validation

import models.accounts.{NewPasswords, OrgRegister, UserRegister}
import play.api.data.Forms._
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

object RegisterValidation {

  def initialsChecker: Mapping[String] = {
    val initialsConstraint: Constraint[String] = Constraint("constraints.initials")({
      initials =>
        val error = initials match {
          case "" => Seq(ValidationError("You have not entered any initials"))
          case _ => if(initials.length > 5) Seq(ValidationError("Please enter no more than 5 letters")) else Nil
        }
        if(error.isEmpty) Valid else Invalid(error)
    })
    text().verifying(initialsConstraint)
  }

  def locationChecker: Mapping[String] = {
    val locationConstraint: Constraint[String] = Constraint("constraints.location")({
      location =>
        val error = location match {
          case "" => Seq(ValidationError("You have not entered your schools location"))
          case _  => Nil
        }
        if(error.isEmpty) Valid else Invalid(error)
    })
    text().verifying(locationConstraint)
  }

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
    val userNameConstraint: Constraint[String] = Constraint("constraints.userName")({
      text =>
        val error = text match {
          case "" => Seq(ValidationError("You need to enter a user name"))
          case _ => Nil
        }
        if (error.isEmpty) Valid else Invalid(error)
    })
    text().verifying(userNameConstraint)
  }

  def userNameEntered : Mapping[String] = {
    val userNameConstraint : Constraint[String] = Constraint("constraints.userName")({
      text =>
        val error = text match {
          case "" => Seq(ValidationError("You have not entered your user name"))
          case _ => Nil
        }
        if(error.isEmpty) Valid else Invalid(error)
    })
    text.verifying(userNameConstraint)
  }

  def emailChecker : Mapping[String] = {
    val validEmail = """[A-Za-z0-9\-_.]{1,126}@[A-Za-z0-9\-_.]{1,126}""".r
    val emailConstraint : Constraint[String] = Constraint("constraints.orgEmail")({
      text =>
        val error = text match {
          case validEmail() => Nil
          case "" => Seq(ValidationError("Please enter a valid email address"))
          case _ => if(text.length > 254) Seq(ValidationError("This is not a valid email address")) else Nil
        }
        if(error.isEmpty) Valid else Invalid(error)
    })
    text().verifying(emailConstraint)
  }

  def profileEmailChecker : Mapping[String] = {
    val validEmail = """[A-Za-z0-9\-_.]{1,126}@[A-Za-z0-9\-_.]{1,126}""".r
    val emailConstraint : Constraint[String] = Constraint("constraints.email")({
      text =>
        val error = text match {
          case validEmail() => Nil
          case "" => Seq(ValidationError("Please enter a valid email address"))
          case _ => if(text.length > 254) Seq(ValidationError("This is not a valid email address")) else Nil
        }
        if(error.isEmpty) Valid else Invalid(error)
    })
    text().verifying(emailConstraint)
  }

  def oldPasswordCheck: Mapping[String] = {
    val passwordCheckConstraint: Constraint[String] = Constraint("constraints.oldPassword")({
      text =>
        val error = text match {
          case "" => Seq(ValidationError("You have not entered your old password"))
          case _ => Nil
        }
        if(error.isEmpty) Valid else Invalid(error)
    })
    text().verifying(passwordCheckConstraint)
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

  def orgXPasswordCheck : Constraint[OrgRegister] = {
    Constraint("constraints.password")({
      orForm : OrgRegister =>
        if(orForm.password == orForm.confirmPassword) {
          Valid
        } else if(orForm.password.isEmpty) {
          Invalid(Seq(ValidationError("You have not entered a password")))
        } else if(orForm.confirmPassword.isEmpty) {
          Invalid(Seq(ValidationError("You have not confirmed your password")))
        } else {
          Invalid(Seq(ValidationError("The passwords you have entered do not match")))
        }
    })
  }

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

  def profileXPasswordCheck : Constraint[NewPasswords] = {
    Constraint("constraints.password")({
      pForm : NewPasswords =>
        if(pForm.newPassword == pForm.confirmPassword) {
          Valid
        } else if(pForm.newPassword.isEmpty) {
          Invalid(Seq(ValidationError("You have not entered a password")))
        } else if(pForm.confirmPassword.isEmpty) {
          Invalid(Seq(ValidationError("You have not confirmed your password")))
        } else {
          Invalid(Seq(ValidationError("The passwords you have entered do not match")))
        }
    })
  }
}
