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
package connectors

import com.cjwwdev.auth.models.CurrentUser
import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.http.responses.WsResponseHelpers
import com.cjwwdev.http.responses.EvaluateResponse._
import com.cjwwdev.http.verbs.Http
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.logging.Logging
import com.cjwwdev.security.obfuscation.Obfuscation._
import common.ApplicationConfiguration
import enums.HttpResponse
import javax.inject.Inject
import models.RegistrationCode
import models.accounts.DeversityEnrolment
import models.deversity.{Classroom, OrgDetails, TeacherDetails}
import play.api.http.Status._
import play.api.mvc.Request

import scala.concurrent.{ExecutionContext => ExC, Future}

class DefaultDeversityMicroserviceConnector @Inject()(val http: Http,
                                                      val configurationLoader: ConfigurationLoader) extends DeversityMicroserviceConnector

trait DeversityMicroserviceConnector extends ApplicationConfiguration with WsResponseHelpers with Logging {
  val http: Http

  def getDeversityEnrolment(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[Option[DeversityEnrolment]] = {
    http.get(s"$deversityMicroservice/user/${user.id}/enrolment") map {
      case SuccessResponse(resp) => resp.status match {
        case OK         => resp.toDataType[DeversityEnrolment](needsDecrypt = true).fold(Some(_), _ => None)
        case NO_CONTENT => None
      }
      case ErrorResponse(_) => None
    }
  }

  def getTeacherInfo(teacher: String, school: String)(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[Option[TeacherDetails]] = {
    http.get(s"$deversityMicroservice/user/${user.id}/teacher/${teacher.encrypt}/school/${school.encrypt}/details") map {
      case SuccessResponse(resp) => resp.toDataType[TeacherDetails](needsDecrypt = true).fold(Some(_), _ => None)
      case ErrorResponse(_)      => None
    }
  }

  def getSchoolInfo(school: String)(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[Option[OrgDetails]] = {
    http.get(s"$deversityMicroservice/user/${user.id}/school/${school.encrypt}/details") map {
      case SuccessResponse(resp) => resp.toDataType[OrgDetails](needsDecrypt = true).fold(Some(_), _ => None)
      case ErrorResponse(_)      => None
    }
  }

  def getRegistrationCode(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[RegistrationCode] = {
    http.get(s"$deversityMicroservice/user/${user.id}/fetch-registration-code") collect {
      case SuccessResponse(resp) => resp.toDataType[RegistrationCode](needsDecrypt = true).left.get
    }
  }

  def generateRegistrationCode(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[HttpResponse.Value] = {
    http.head(s"$deversityMicroservice/user/${user.id}/generate-registration-code") map {
      case SuccessResponse(_) => HttpResponse.success
      case ErrorResponse(_)   => HttpResponse.failed
    }
  }

  def createClassroom(classRoomName: String)(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[String] = {
    http.postString(s"$deversityMicroservice/teacher/${user.id}/create-classroom", classRoomName) map {
      _ => classRoomName
    }
  }

  def getClassrooms(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[Seq[Classroom]] = {
    http.get(s"$deversityMicroservice/teacher/${user.id}/classrooms") map {
      case SuccessResponse(resp) => resp.status match {
        case OK         => resp.toDataType[Seq[Classroom]](needsDecrypt = true).fold(identity, _ => Seq.empty[Classroom])
        case NO_CONTENT => Seq.empty[Classroom]
      }
      case ErrorResponse(_) => Seq.empty[Classroom]
    }
  }

  def getClassroom(classId: String)(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[Classroom] = {
    http.get(s"$deversityMicroservice/teacher/${user.id}/classroom/$classId") collect {
      case SuccessResponse(resp) => resp.toDataType[Classroom](needsDecrypt = true).left.get
    }
  }

  def deleteClassroom(classId: String)(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[HttpResponse.Value] = {
    http.delete(s"$deversityMicroservice/teacher/${user.id}/classroom/$classId") map {
      case SuccessResponse(_) => HttpResponse.success
      case ErrorResponse(_)   => HttpResponse.failed
    }
  }
}
