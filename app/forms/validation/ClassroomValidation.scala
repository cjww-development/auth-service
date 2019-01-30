/*
 * Copyright 2019 CJWW Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms.validation

import play.api.data.Forms.text
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.i18n.{Lang, MessagesApi}

object ClassroomValidation extends CommonValidation {
  def classNameValidation(implicit messages: MessagesApi, lang: Lang): Mapping[String] = {
    val classNameConstraint: Constraint[String] = Constraint("constraints.className")({ className =>
      val errors = className match {
        case ""                  => Seq(ValidationError(messages("pages.manage-classrooms.create-classroom.error.no-entry")))
        case x if x.length >= 20 => Seq(ValidationError(messages("pages.manage-classrooms.create-classroom.error.too-long")))
        case _                   => Nil
      }
      if(errors.isEmpty) Valid else Invalid(errors)
    })
    text.verifying(classNameConstraint)
  }
}
