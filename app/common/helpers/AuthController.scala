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

import com.cjwwdev.auth.frontend.AuthorisedAction
import common.ApplicationConfiguration
import org.slf4j.{Logger, LoggerFactory}
import play.api.i18n.Lang
import play.api.mvc.{BaseController, Call, Request}

trait AuthController
  extends BaseController
    with ControllerHelpers
    with UrlParser
    with AuthorisedAction {

  implicit def getLang(implicit request: Request[_]): Lang = supportedLangs.preferred(request.acceptLanguages)

  override def unauthorisedRedirect: Call = Call("GET", s"/account-services$LOGIN_CALLBACK")

  override val logger: Logger = LoggerFactory.getLogger(getClass)
}
