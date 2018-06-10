
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

package controllers.test

import common.helpers.FrontendController
import enums.Features._
import forms.{FeatureModel, FeatureSwitchForm}
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.FeatureService
import views.html.test.FeatureSwitchView

class DefaultFeatureController @Inject()(val controllerComponents: ControllerComponents,
                                         val featureService: FeatureService) extends FeatureController {
  override def deversityEnabled: Boolean = featureService.getBooleanFeatureState(DEVERSITY)
}

trait FeatureController extends FrontendController {

  val featureService: FeatureService

  def show(): Action[AnyContent] = Action { implicit request =>
    val featureList = featureService.getAllBooleanFeatureStates(allFeatures)
    Ok(FeatureSwitchView(FeatureSwitchForm.form.fill(FeatureModel(featureList))))
  }

  def submit(): Action[AnyContent] = Action { implicit request =>
    FeatureSwitchForm.form.bindFromRequest.fold(
      _     => BadRequest("BAD REQ"),
      valid => {
        valid.featureState.map(feature => featureService.setBooleanFeatureState(feature.name, feature.state))
        Redirect(routes.FeatureController.show())
      }
    )
  }
}
