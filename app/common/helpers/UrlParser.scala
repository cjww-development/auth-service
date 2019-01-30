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

package common.helpers

import com.cjwwdev.logging.Logging
import play.api.mvc.Request

trait UrlParser extends Logging {

  protected val diagnosticsFrontend: String
  protected val deversityFrontend: String
  protected val hubFrontend: String

  def serviceDirector(implicit request: Request[_]) : String = {
    logger.info(s"[serviceDirector] - ${request.getQueryString("redirect").getOrElse("Redirecting to service selector")}")
    request.getQueryString("redirect") match {
      case Some("diagnostics") => diagnosticsFrontend
      case Some("deversity")   => deversityFrontend
      case Some("hub")         => hubFrontend
      case _                   => controllers.redirect.routes.RedirectController.chooseService().url
    }
  }
}
