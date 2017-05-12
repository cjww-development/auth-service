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


package controllers.user

import javax.inject.Inject

import com.cjwwdev.auth.actions.Actions
import com.cjwwdev.auth.connectors.AuthConnector
import connectors._
import forms.{DisplayNameForm, NewPasswordForm, UserProfileForm}
import models.accounts.{DashboardDisplay, NewPasswords, PasswordSet, UserProfile}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import services.{EditProfileService, FeedService}
import utils.application.FrontendController
import views.html.user.EditProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EditProfileController @Inject()(messagesApi: MessagesApi,
                                      accountConnector: AccountConnector,
                                      feedEventService : FeedService,
                                      authConnect: AuthConnector) extends FrontendController with EditProfileService with Actions {

  val authConnector = authConnect

  def show : Action[AnyContent] = authorisedFor(LOGIN_CALLBACK).async {
    implicit user =>
      implicit request =>
        for {
          Some(basicDetails) <- accountConnector.getBasicDetails
          settings <- accountConnector.getSettings
        } yield {
          Ok(
            EditProfile(
              UserProfileForm.form.fill(UserProfile.fromAccount(basicDetails)),
              NewPasswordForm.form.fill(NewPasswords("","","")),
              DisplayNameForm.form.fill(DashboardDisplay(getDisplayOption(settings),getDisplayNameColour(settings),getDisplayImageURL(settings))),
              basicDetails
            )
          )
        }
  }

  def updateProfile() : Action[AnyContent] = authorisedFor(LOGIN_CALLBACK).async {
    implicit user =>
      implicit request =>
        UserProfileForm.form.bindFromRequest.fold(
          errors => {
            for {
              Some(basicDetails) <- accountConnector.getBasicDetails
              settings <- accountConnector.getSettings
            } yield {
              BadRequest(
                EditProfile(
                  errors,
                  NewPasswordForm.form.fill(NewPasswords("","","")),
                  DisplayNameForm.form.fill(DashboardDisplay(getDisplayOption(settings), getDisplayNameColour(settings), getDisplayImageURL(settings))),
                  basicDetails
                )
              )
            }
          },
          valid => {
            accountConnector.updateProfile(valid) flatMap {
              case OK =>
                for {
                  _ <- feedEventService.basicDetailsFeedEvent
                } yield {
                  Redirect(routes.EditProfileController.show()).withSession(request.session. +("firstName" -> valid.firstName) +("lastName" -> valid.lastName))
                }
            }
          }
        )
  }

  def updatePassword() : Action[AnyContent] = authorisedFor(LOGIN_CALLBACK).async {
    implicit user =>
      implicit request =>
        NewPasswordForm.form.bindFromRequest.fold(
          errors => {
            for {
              Some(basicDetails) <- accountConnector.getBasicDetails
              settings <- accountConnector.getSettings
            } yield {
              BadRequest(
                EditProfile(
                  UserProfileForm.form.fill(UserProfile.fromAccount(basicDetails)),
                  errors,
                  DisplayNameForm.form.fill(DashboardDisplay(getDisplayOption(settings), getDisplayNameColour(settings), getDisplayImageURL(settings))),
                  basicDetails
                )
              )
            }
          },
          valid => {
            accountConnector.updatePassword(PasswordSet.create(user.user.userId, valid.encrypt)) flatMap {
              case PasswordUpdated => feedEventService.passwordUpdateFeedEvent map {
                _ => Redirect(s"${routes.EditProfileController.show().url}#password")
              }
              case InvalidOldPassword =>
                accountConnector.getBasicDetails flatMap {
                  basicDetails =>
                    accountConnector.getSettings map {
                      settings =>
                        BadRequest(
                          EditProfile(
                            UserProfileForm.form.fill(UserProfile.fromAccount(basicDetails.get)),
                            NewPasswordForm.form.fill(valid).withError("oldPassword", "Your old password is incorrect"),
                            DisplayNameForm.form.fill(DashboardDisplay(
                              getDisplayOption(settings),
                              getDisplayNameColour(settings),
                              getDisplayImageURL(settings)
                            )),
                            basicDetails.get
                          )
                        )
                    }
                }
            }
          }
        )
  }

  def updateSettings() : Action[AnyContent] = authorisedFor(LOGIN_CALLBACK).async {
    implicit user =>
      implicit request =>
        DisplayNameForm.form.bindFromRequest.fold(
          errors => {
            for {
              Some(basicDetails) <- accountConnector.getBasicDetails
              settings <- accountConnector.getSettings
            } yield {
              BadRequest(
                EditProfile(
                  UserProfileForm.form.fill(UserProfile.fromAccount(basicDetails)),
                  NewPasswordForm.form.fill(NewPasswords("","","")),
                  errors,
                  basicDetails
                )
              )
            }
          },
          valid => {
            accountConnector.updateSettings(DashboardDisplay.toAccountSettings(user.user.userId, valid)) flatMap {
              case UpdatedSettingsSuccess =>
                feedEventService.accountSettingsFeedEvent map {
                  _ => Redirect(routes.EditProfileController.show())
                }
              case UpdatedSettingsFailed => Future.successful(InternalServerError)
            }
          }
        )
  }
}
