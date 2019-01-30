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

package services

import com.cjwwdev.auth.models.CurrentUser
import common.responses.{InvalidOldPassword, PasswordUpdated, UpdatedPasswordResponse}
import connectors.AccountsConnector
import enums.HttpResponse
import javax.inject.Inject
import models.accounts.{BasicDetails, PasswordSet, Settings, UserProfile}
import play.api.mvc.Request

import scala.concurrent.{Future, ExecutionContext => ExC}

class DefaultEditProfileService @Inject()(val accountsConnector: AccountsConnector,
                                          val feedService: FeedService) extends EditProfileService

trait EditProfileService {

  val accountsConnector: AccountsConnector
  val feedService: FeedService

  def getDetailsAndSettings(implicit req: Request[_], currentUser: CurrentUser, ec: ExC): Future[(BasicDetails, Settings)] = {
    for {
      Some(details) <- accountsConnector.getBasicDetails
      settings      <- accountsConnector.getSettings
    } yield (details, settings)
  }

  def getBasicDetails(implicit req: Request[_], currentUser: CurrentUser, ec: ExC): Future[BasicDetails] = {
    accountsConnector.getBasicDetails.map(_.get)
  }

  def updateProfile(updatedProfile: UserProfile)(implicit req: Request[_], currentUser: CurrentUser, ec: ExC): Future[HttpResponse.Value] = {
    for {
      res <- accountsConnector.updateProfile(updatedProfile)
      _   <- feedService.basicDetailsFeedEvent
    } yield res
  }

  def updatePassword(newPassword: PasswordSet)(implicit req: Request[_],
                                               currentUser: CurrentUser,
                                               ec: ExC): Future[Either[(BasicDetails, Settings), UpdatedPasswordResponse]] = {
    accountsConnector.updatePassword(newPassword) flatMap {
      case res@PasswordUpdated => feedService.passwordUpdateFeedEvent map(_ => Right(res))
      case InvalidOldPassword  => for {
        Some(details) <- accountsConnector.getBasicDetails
        settings      <- accountsConnector.getSettings
      } yield Left((details, settings))
    }
  }

  def updateSettings(newSettings: Settings)(implicit req: Request[_], currentUser: CurrentUser, ec: ExC): Future[HttpResponse.Value] = {
    accountsConnector.updateSettings(newSettings) flatMap {
      case res@HttpResponse.success => feedService.accountSettingsFeedEvent map(_ => res)
      case res@HttpResponse.failed  => Future.successful(res)
    }
  }
}
