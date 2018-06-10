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

import play.api.mvc.{BaseController, Call, Request, Result}
import com.cjwwdev.views.html.templates.errors.NotFoundView
import com.cjwwdev.frontendUI.builders.NavBarLinkBuilder
import play.api.i18n.Lang

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait ControllerHelpers {
  self: BaseController =>

  def deversityEnabled: Boolean

  protected def deversityGuard(result: => Future[Result])(implicit request: Request[_],
                                                          lang: Lang,
                                                          navBarLinks: Seq[NavBarLinkBuilder],
                                                          navBarRoutes: Map[String, Call]): Future[Result] = {
    if(deversityEnabled) {
      result
    } else {
      Future(NotFound(NotFoundView()))
    }
  }
}
