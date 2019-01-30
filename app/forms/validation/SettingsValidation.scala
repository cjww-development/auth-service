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

import com.cjwwdev.regex.RegexPack
import play.api.data.Forms._
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

class InvalidSettingsException(msg: String) extends Exception(msg)

object SettingsValidation extends RegexPack {
  def displayNameValidation: Mapping[String] = {
    val displayNameConstraint: Constraint[String] = Constraint("constraint.displayName")({ displayName =>
      val errors = displayName match {
        case "full" | "short" | "user" => Nil
        case _ =>
          throw new InvalidSettingsException(s"An invalid display name option was detected : ## $displayName ##")
          Seq(ValidationError(""))
      }
      if(errors.isEmpty) Valid else Invalid(errors)
    })
    text.verifying(displayNameConstraint)
  }

  def displayNameColourValidation: Mapping[String] = {
    val displayNameColourConstraint: Constraint[String] = Constraint("constraint.displayNameColour")({ displayNameColour =>
      val errors = displayNameColour match {
        case hexadecimalColourRegex() => Nil
        case _                        => Seq(ValidationError("Enter a valid hexadecimal colour"))
      }
      if(errors.isEmpty) Valid else Invalid(errors)
    })
    text.verifying(displayNameColourConstraint)
  }

  def displayImageUrlValidation: Mapping[String] = {
    val displayImageURLConstraint: Constraint[String] = Constraint("constraint.displayImageURL")({ displayImageURL =>
      val errors = displayImageURL match {
        case `defaultUrl` => Nil
        case urlRegex()   => Nil
        case ""           => Nil
        case _            => Seq(ValidationError("Enter a valid URL for your background image"))
      }
      if(errors.isEmpty) Valid else Invalid(errors)
    })
    text.verifying(displayImageURLConstraint)
  }
}
