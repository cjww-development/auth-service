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

package services

import com.cjwwdev.auth.models.AuthContext
import com.cjwwdev.config.ConfigurationLoader
import com.google.inject.{Inject, Singleton}
import config.ApplicationConfiguration
import connectors.AccountsMicroserviceConnector
import enums.HttpResponse
import models.feed.{EventDetail, FeedItem, SourceDetail}
import org.joda.time.DateTime
import play.api.mvc.Request

import scala.concurrent.Future

@Singleton
class FeedService @Inject()(accountConnector: AccountsMicroserviceConnector,
                            val config: ConfigurationLoader) extends ApplicationConfiguration {

  private val appName = config.loadedConfig.underlying.getString("appName")

  private[services] def buildFeedItem(location : String, desc : String)(implicit authContext : AuthContext) : FeedItem = {
    FeedItem(
      authContext.user.id,
      SourceDetail(appName, location),
      EventDetail(TITLE, desc),
      DateTime.now
    )
  }

  def basicDetailsFeedEvent(implicit authContext: AuthContext, request: Request[_]) : Future[HttpResponse.Value] = {
    val feedEvent = buildFeedItem("edit-profile","You updated your basic details")
    accountConnector.createFeedItem(feedEvent)
  }

  def passwordUpdateFeedEvent(implicit authContext: AuthContext, request: Request[_]) : Future[HttpResponse.Value] = {
    val feedEvent = buildFeedItem("edit-profile","You changed your password")
    accountConnector.createFeedItem(feedEvent)
  }

  def accountSettingsFeedEvent(implicit authContext: AuthContext, request: Request[_]) : Future[HttpResponse.Value] = {
    val feedEvent = buildFeedItem("edit-profile","You updated your account settings")
    accountConnector.createFeedItem(feedEvent)
  }

  def processRetrievedList(implicit auth: AuthContext, request: Request[_]): Future[Option[List[FeedItem]]] = {
    accountConnector.getFeedItems
  }
}
