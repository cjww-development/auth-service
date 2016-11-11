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

package utils.application

import javax.inject._

import play.api._
import play.api.http.DefaultHttpErrorHandler
import play.api.http.Status._
import play.api.i18n.MessagesApi
import play.api.mvc.Results._
import play.api.mvc._
import play.api.routing.Router

import scala.concurrent._

@Singleton
class ErrorHandler @Inject()
  (env : Environment,
   configuration: Configuration,
   sourceMapper: OptionalSourceMapper,
   router: Provider[Router], messagesApi: MessagesApi) extends DefaultHttpErrorHandler(env, configuration, sourceMapper, router) with RequestBuilder {


  override def onClientError(request: RequestHeader, statusCode: Int, message: String) : Future[Result] = {
    Logger.error(s"[ErrorHandler] - [onClientError] - Url : ${request.uri}, Status code : $statusCode, Message : $message")
    implicit val req = buildNewRequest[String](request, "")
    statusCode match {
      case NOT_FOUND => Future.successful(NotFound(views.html.errors.NotFoundPage()))
      case _ => Future.successful(Results.Status(statusCode)(views.html.error_template("There was a problem, please try again later!")))
    }
  }
  override def onServerError(request: RequestHeader, exception: Throwable) : Future[Result] = {
    Logger.error(s"[ErrorHandler] - [onServerError] - exception : $exception")
    implicit val req = buildNewRequest[String](request, "s")
    Future.successful(InternalServerError(views.html.errors.ServerError()))
  }
}
