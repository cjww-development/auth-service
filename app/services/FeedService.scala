/*
 * Copyright 2018 CJWW Development
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
import com.cjwwdev.config.ConfigurationLoader
import common.ApplicationConfiguration
import connectors.AccountsMicroserviceConnector
import enums.HttpResponse
import javax.inject.Inject
import models.feed.{EventDetail, FeedItem, SourceDetail}
import org.joda.time.DateTime
import play.api.mvc.Request

import scala.concurrent.{ExecutionContext => ExC, Future}

class DefaultFeedService @Inject()(val accountConnector: AccountsMicroserviceConnector,
                                   val config: ConfigurationLoader) extends FeedService {
  override val appName: String = config.get[String]("appName")
}

trait FeedService extends ApplicationConfiguration {
  val accountConnector: AccountsMicroserviceConnector

  val appName: String

  private[services] def buildFeedItem(location : String, desc : String)(implicit user: CurrentUser) : FeedItem = {
    FeedItem(
      user.id,
      SourceDetail(appName, location),
      EventDetail(TITLE, desc),
      DateTime.now
    )
  }

  def basicDetailsFeedEvent(implicit user: CurrentUser, req: Request[_], ec: ExC) : Future[HttpResponse.Value] = {
    val feedEvent = buildFeedItem("edit-profile","You updated your basic details")
    accountConnector.createFeedItem(feedEvent)
  }

  def passwordUpdateFeedEvent(implicit user: CurrentUser, req: Request[_], ec: ExC) : Future[HttpResponse.Value] = {
    val feedEvent = buildFeedItem("edit-profile","You changed your password")
    accountConnector.createFeedItem(feedEvent)
  }

  def accountSettingsFeedEvent(implicit user: CurrentUser, req: Request[_], ec: ExC) : Future[HttpResponse.Value] = {
    val feedEvent = buildFeedItem("edit-profile","You updated your account settings")
    accountConnector.createFeedItem(feedEvent)
  }

  def processRetrievedList(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[List[FeedItem]] = {
    accountConnector.getFeedItems
  }
}
