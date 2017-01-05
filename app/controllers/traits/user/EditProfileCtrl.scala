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
package controllers.traits.user

import config.Authenticated
import connectors._
import controllers.user.routes
import forms.{DisplayNameForm, NewPasswordForm, UserProfileForm}
import models.accounts._
import play.api.mvc.{Action, AnyContent}
import services.{EditProfileService, FeedService}
import utils.application.FrontendController
import views.html.user.EditProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait EditProfileCtrl extends FrontendController with EditProfileService {

  val sessionStoreConnector : SessionStoreConnector
  val accountConnector : AccountConnector
  val feedEventService : FeedService
  val editProfileService : EditProfileService

  def show : Action[AnyContent] = Authenticated.async {
    implicit request =>
      sessionStoreConnector.getDataElement[UserAccount]("userInfo") map {
        account =>
          Ok(
            EditProfile(
              account,
              UserProfileForm.form.fill(UserProfile.fromAccount(account.get)),
              NewPasswordForm.form.fill(NewPasswords("","","")),
              DisplayNameForm.form.fill(DisplayName(getDisplayOption(account), getDisplayNameColour(account)))
            )
          )
      }
  }

  def updateProfile() : Action[AnyContent] = Authenticated.async {
    implicit request =>
      UserProfileForm.form.bindFromRequest.fold(
        errors => {
          sessionStoreConnector.getDataElement[UserAccount]("userInfo") map {
            account =>
              BadRequest(
                EditProfile(
                  account,
                  errors,
                  NewPasswordForm.form.fill(NewPasswords("","","")),
                  DisplayNameForm.form.fill(DisplayName(getDisplayOption(account), getDisplayNameColour(account)))
                )
              )
          }
        },
        valid => {
          accountConnector.updateProfile(valid) flatMap {
            case OK =>
              for {
                _ <- feedEventService.basicDetailsFeedEvent
                _ <- editProfileService.updateSession("userInfo")
              } yield {
                Redirect(routes.EditProfileController.show())
              }
          }
        }
      )
  }

  def updatePassword() : Action[AnyContent] = Authenticated.async {
    implicit request =>
      NewPasswordForm.form.bindFromRequest.fold(
        errors => {
          sessionStoreConnector.getDataElement[UserAccount]("userInfo") map {
            account =>
              BadRequest(
                EditProfile(
                  account,
                  UserProfileForm.form.fill(UserProfile.fromAccount(account.get)),
                  errors,
                  DisplayNameForm.form.fill(DisplayName(getDisplayOption(account), getDisplayNameColour(account)))
                )
              )
          }
        },
        valid => {
          sessionStoreConnector.getDataElement[UserAccount]("userInfo") flatMap {
            account =>
              accountConnector.updatePassword(PasswordSet.create(account.get._id.get, valid.encrypt)) map {
                case PasswordUpdated => feedEventService.passwordUpdateFeedEvent
                  Redirect(s"${routes.EditProfileController.show().url}#password")
                case InvalidOldPassword =>
                  BadRequest(
                    EditProfile(
                      account,
                      UserProfileForm.form.fill(UserProfile.fromAccount(account.get)),
                      NewPasswordForm.form.fill(valid).withError("oldPassword", "Your old password is incorrect"),
                      DisplayNameForm.form.fill(DisplayName(getDisplayOption(account), getDisplayNameColour(account)))
                    )
                  )
              }
          }
        }
      )
  }

  def updateSettings() : Action[AnyContent] = Authenticated.async {
    implicit request =>
      DisplayNameForm.form.bindFromRequest.fold(
        errors => {
          sessionStoreConnector.getDataElement[UserAccount]("userInfo") map {
            account =>
              BadRequest(
                EditProfile(
                  account,
                  UserProfileForm.form.fill(UserProfile.fromAccount(account.get)),
                  NewPasswordForm.form.fill(NewPasswords("","","")),
                  errors
                )
              )
          }
        },
        valid => {
          sessionStoreConnector.getDataElement[UserAccount]("userInfo") flatMap {
            account =>
              accountConnector.updateSettings(DisplayName.toAccountSettings(account.get._id.get, valid)) flatMap {
                case UpdatedSettingsSuccess =>
                  feedEventService.accountSettingsFeedEvent
                  editProfileService.updateSession("userInfo") map {
                    _ => Redirect(routes.EditProfileController.show())
                  }
                case UpdatedSettingsFailed => Future.successful(InternalServerError)
              }
          }
        }
      )
  }
}
