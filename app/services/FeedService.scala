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

package services

import config.FrontendConfiguration
import connectors.{AccountConnector, FeedEventResponse}
import models.accounts.{EventDetail, FeedItem, SourceDetail}
import org.joda.time.DateTime
import play.api.mvc.Request

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object FeedService extends FeedService {
  val accountConnector = AccountConnector
}

trait FeedService extends FrontendConfiguration {

  val accountConnector : AccountConnector

  private def buildFeedItem(location : String, desc : String)(implicit request: Request[_]) : FeedItem = {
    FeedItem(
      request.session("_id"),
      SourceDetail(APPLICATION_NAME, location),
      EventDetail(TITLE, desc),
      DateTime.now
    )
  }

  def basicDetailsFeedEvent(implicit request: Request[_]) : Future[FeedEventResponse] = {
    val feedEvent = buildFeedItem("edit-profile","You updated your basic details")
    accountConnector.createFeedItem(feedEvent)
  }

  def passwordUpdateFeedEvent(implicit request: Request[_]) : Future[FeedEventResponse] = {
    val feedEvent = buildFeedItem("edit-profile","You changed your password")
    accountConnector.createFeedItem(feedEvent)
  }

  def accountSettingsFeedEvent(implicit request: Request[_]) : Future[FeedEventResponse] = {
    val feedEvent = buildFeedItem("edit-profile","You updated your account settings")
    accountConnector.createFeedItem(feedEvent)
  }

  def processRetrievedList(implicit request: Request[_]) : Future[Option[List[FeedItem]]] = {
    accountConnector.getFeedItems(request.session("_id")) map {
      feed =>
        feed.isDefined match {
          case false => None
          case true => feed.get.isEmpty match {
            case true => None
            case false => feed
          }
        }
    }
  }
}
