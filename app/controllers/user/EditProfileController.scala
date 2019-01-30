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

package controllers.user

import com.cjwwdev.auth.connectors.AuthConnector
import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.featuremanagement.services.FeatureService
import common.helpers.AuthController
import common.{FeatureManagement, RedirectUrls}
import connectors.AccountsConnector
import enums.HttpResponse
import forms.{NewPasswordForm, SettingsForm, UserProfileForm}
import javax.inject.Inject
import models.accounts.{BasicDetails, UserProfile}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.{EditProfileService, FeedService}
import views.html.user.EditProfile

import scala.concurrent.ExecutionContext

class DefaultEditProfileController @Inject()(val accountsConnector: AccountsConnector,
                                             val feedService : FeedService,
                                             val controllerComponents: ControllerComponents,
                                             val authConnector: AuthConnector,
                                             val config: ConfigurationLoader,
                                             val editProfileService: EditProfileService,
                                             val featureService: FeatureService,
                                             implicit val ec: ExecutionContext) extends EditProfileController with RedirectUrls

trait EditProfileController extends AuthController with FeatureManagement {
  val accountsConnector: AccountsConnector
  val feedService: FeedService
  val editProfileService: EditProfileService

  implicit def basicDetailsToUserProfile(basicDetails: BasicDetails): UserProfile = UserProfile(
    firstName = basicDetails.firstName,
    lastName  = basicDetails.lastName,
    userName  = basicDetails.userName,
    email     = basicDetails.email
  )

  def show : Action[AnyContent] = isAuthorised { implicit req => implicit user =>
    editProfileService.getDetailsAndSettings map { case (details, settings) =>
      val profileForm  = UserProfileForm.form.fill(details)
      val passwordForm = NewPasswordForm.form
      val settingsForm = SettingsForm.form.fill(settings)

      Ok(EditProfile(profileForm, passwordForm, settingsForm, details))
    }
  }

  def updateProfile() : Action[AnyContent] = isAuthorised { implicit req => implicit user =>
    UserProfileForm.form.bindFromRequest.fold(
      errors => editProfileService.getDetailsAndSettings map { case (details, settings) =>
        BadRequest(EditProfile(errors, NewPasswordForm.form, SettingsForm.form.fill(settings), details))
      },
      valid => editProfileService.updateProfile(valid) map {
        _ => Redirect(routes.EditProfileController.show())
          .withSession(req.session. +("firstName" -> valid.firstName) + ("lastName" -> valid.lastName))
      }
    )
  }

  def updatePassword() : Action[AnyContent] = isAuthorised { implicit req => implicit user =>
    NewPasswordForm.form.bindFromRequest.fold(
      errors => editProfileService.getDetailsAndSettings map { case (details, settings) =>
        BadRequest(EditProfile(UserProfileForm.form.fill(details), errors, SettingsForm.form.fill(settings), details))
      },
      valid => editProfileService.updatePassword(valid) map {
        case Left((details, settings)) => BadRequest(EditProfile(
          UserProfileForm.form.fill(details),
          NewPasswordForm.form.fill(valid).withError("oldPassword", "Your old password is incorrect"),
          SettingsForm.form.fill(settings),
          details
        ))
        case Right(_) => Redirect(s"${routes.EditProfileController.show().url}#password")
      }
    )
  }

  def updateSettings() : Action[AnyContent] = isAuthorised { implicit req => implicit user =>
    SettingsForm.form.bindFromRequest.fold(
      errors => editProfileService.getBasicDetails map { details =>
        BadRequest(EditProfile(
          UserProfileForm.form.fill(details),
          NewPasswordForm.form,
          errors,
          details
        ))
      },
      valid => editProfileService.updateSettings(valid) map {
        case HttpResponse.success => Redirect(routes.EditProfileController.show())
        case HttpResponse.failed  => InternalServerError
      }
    )
  }
}
