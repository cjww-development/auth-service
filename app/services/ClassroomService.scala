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

package services

import javax.inject.Inject

import com.cjwwdev.auth.models.CurrentUser
import connectors.DeversityMicroserviceConnector
import models.deversity.Classroom
import play.api.mvc.Request

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ClassroomServiceImpl @Inject()(val deversityConnector: DeversityMicroserviceConnector) extends ClassroomService

trait ClassroomService {
  val deversityConnector: DeversityMicroserviceConnector

  def createClassroom(classroomName: String)(implicit user: CurrentUser, request: Request[_]): Future[String] = {
    deversityConnector.createClassroom(classroomName)
  }

  def getClassrooms(implicit user: CurrentUser, request: Request[_]): Future[Seq[Classroom]] = {
    deversityConnector.getClassrooms
  }

  def getClassroom(classId: String)(implicit user: CurrentUser, request: Request[_]): Future[Classroom] = {
    deversityConnector.getClassroom(classId)
  }

  def deleteClassroom(classId: String)(implicit user: CurrentUser, request: Request[_]): Future[String] = {
    deversityConnector.deleteClassroom(classId) map(_ => classId)
  }
}
