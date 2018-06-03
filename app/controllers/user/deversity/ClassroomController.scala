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

package controllers.user.deversity

import com.cjwwdev.auth.connectors.AuthConnector
import common.FrontendController
import forms.CreateClassForm
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.ClassroomService
import views.html.user.deversity.{ManageClassroomView, ManageClassroomsView}

import scala.concurrent.ExecutionContext.Implicits.global

class DefaultClassroomController @Inject()(val authConnector: AuthConnector,
                                           val controllerComponents: ControllerComponents,
                                           val classroomService: ClassroomService) extends ClassroomController

trait ClassroomController extends FrontendController {
  val classroomService: ClassroomService

  private val createClassroomForm = CreateClassForm.form

  def manageClassRooms: Action[AnyContent] = isAuthorised {
   implicit user =>
      implicit request =>
        classroomService.getClassrooms map { classList =>
          Ok(ManageClassroomsView(createClassroomForm, classList))
        }
  }

  def createClassRoom: Action[AnyContent] = isAuthorised {
    implicit user =>
      implicit request =>
        createClassroomForm.bindFromRequest.fold(
          errors => classroomService.getClassrooms map { classList =>
            BadRequest(ManageClassroomsView(errors, classList))
          },
          name   => classroomService.createClassroom(name) map { _ =>
            Redirect(routes.ClassroomController.manageClassRooms())
          }
        )
  }

  def manageClassRoom(classId: String): Action[AnyContent] = isAuthorised {
    implicit user =>
      implicit request =>
        classroomService.getClassroom(classId) map { classRoom =>
          Ok(ManageClassroomView(classRoom))
        }
  }

  def deleteClassRoom(classId: String): Action[AnyContent] = isAuthorised {
    implicit user =>
      implicit request =>
        classroomService.deleteClassroom(classId) map {
          _ => Redirect(routes.ClassroomController.manageClassRooms())
        }
  }
}
