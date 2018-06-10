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

package common.helpers

import common.ApplicationConfiguration
import play.api.mvc.Request

trait UrlParser extends ApplicationConfiguration with Logging {
  def serviceDirector(implicit request: Request[_]) : String = {
    logger.info(s"[UrlParser] - [serviceDirector] ${request.getQueryString("redirect").getOrElse("Redirecting to service selector")}")
    request.getQueryString("redirect") match {
      case Some("diagnostics") => diagnosticsFrontend
      case Some("deversity")   => deversityFrontend
      case Some("hub")         => hubFrontend
      case _                   => controllers.redirect.routes.RedirectController.chooseService().url
    }
  }
}
