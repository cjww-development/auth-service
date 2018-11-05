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

import com.cjwwdev.views.html.templates.errors.NotFoundView
import common.ApplicationConfiguration
import play.api.i18n.Lang
import play.api.mvc.{BaseController, Request, Result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ControllerHelpers extends ApplicationConfiguration {
  self: BaseController =>

  protected def deversityGuard(result: => Future[Result])(implicit request: Request[_], lang: Lang): Future[Result] = {
    if(deversityEnabled) result else Future(NotFound(NotFoundView()))
  }
}
