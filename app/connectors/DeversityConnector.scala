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
package connectors

import com.cjwwdev.auth.models.CurrentUser
import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.http.responses.EvaluateResponse._
import com.cjwwdev.http.responses.WsResponseHelpers
import com.cjwwdev.http.verbs.Http
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.logging.Logging
import com.cjwwdev.security.obfuscation.Obfuscation._
import javax.inject.Inject
import models.accounts.DeversityEnrolment
import models.deversity.{OrgDetails, TeacherDetails}
import play.api.http.Status._
import play.api.mvc.Request

import scala.concurrent.{Future, ExecutionContext => ExC}

class DefaultDeversityConnector @Inject()(val http: Http,
                                          val config: ConfigurationLoader) extends DeversityConnector {
  override val deversity: String = config.getServiceUrl("deversity")
}

trait DeversityConnector extends WsResponseHelpers with Logging {
  val http: Http

  val deversity: String

  def getDeversityEnrolment(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[Option[DeversityEnrolment]] = {
    http.get(s"$deversity/user/${user.id}/enrolment") map {
      case SuccessResponse(resp) => resp.status match {
        case OK         => resp.toDataType[DeversityEnrolment](needsDecrypt = true).fold(Some(_), _ => None)
        case NO_CONTENT => None
      }
      case ErrorResponse(_) => None
    }
  }

  def getTeacherInfo(teacher: String, school: String)(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[Option[TeacherDetails]] = {
    http.get(s"$deversity/user/${user.id}/teacher/${teacher.encrypt}/school/${school.encrypt}/details") map {
      case SuccessResponse(resp) => resp.toDataType[TeacherDetails](needsDecrypt = true).fold(Some(_), _ => None)
      case ErrorResponse(_)      => None
    }
  }

  def getSchoolInfo(school: String)(implicit user: CurrentUser, req: Request[_], ec: ExC): Future[Option[OrgDetails]] = {
    http.get(s"$deversity/user/${user.id}/school/${school.encrypt}/details") map {
      case SuccessResponse(resp) => resp.toDataType[OrgDetails](needsDecrypt = true).fold(Some(_), _ => None)
      case ErrorResponse(_)      => None
    }
  }
}
