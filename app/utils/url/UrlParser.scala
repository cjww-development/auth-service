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
package utils.url

import config.FrontendConfiguration
import play.api.Logger
import play.api.mvc.Request

object UrlParser extends UrlParser

trait UrlParser extends FrontendConfiguration {

  def serviceDirector(implicit request: Request[_]) : String = {
    Logger.info(s"[UrlParse] [serviceDirector] - ${request.getQueryString("redirect").getOrElse("Redirecting to service selector")}")
    request.getQueryString("redirect") match {
      case Some("diagnostics") => diagnosticsFrontend
      case Some("deversity") => deversityFrontend
      case Some("hub") => hubFrontend
      case _ => controllers.redirect.routes.RedirectController.chooseService().url
    }
  }
}
