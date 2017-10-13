// Copyright (C) 2016-2017 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package connectors

import javax.inject.{Inject, Singleton}

import com.cjwwdev.auth.models.AuthContext
import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.http.exceptions.NotFoundException
import com.cjwwdev.http.verbs.Http
import com.cjwwdev.security.encryption.DataSecurity
import config.ApplicationConfiguration
import models.accounts.DeversityEnrolment
import models.deversity.{OrgDetails, TeacherDetails}
import play.api.libs.json.JsValue
import play.api.mvc.Request

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class DeversityMicroserviceConnector @Inject()(http: Http, val config: ConfigurationLoader) extends ApplicationConfiguration {
  def getDeversityEnrolment(implicit authContext: AuthContext, request: Request[_]): Future[Option[DeversityEnrolment]] = {
    http.GET[DeversityEnrolment](s"$deversityMicroservice/enrolment/${authContext.user.id}/deversity") map { devEnr =>
      Some(devEnr)
    } recover {
      case _: NotFoundException => None
    }
  }

  def getDeversityEnrolmentForConfirmation(userId: String)(implicit authContext: AuthContext, request: Request[_]): Future[Option[DeversityEnrolment]] = {
    http.GET[DeversityEnrolment](s"") map { devEnr =>
      Some(devEnr)
    } recover {
      case _: NotFoundException => None
    }
  }

  def getPendingEnrolmentCount(implicit authContext: AuthContext, request: Request[_]): Future[JsValue] = {
    http.GET[JsValue](s"$deversityMicroservice/utilities/${authContext.user.id}/pending-deversity-enrolments")
  }

  def getTeacherInfo(teacher: String, school: String)(implicit authContext: AuthContext, request: Request[_]): Future[Option[TeacherDetails]] = {
    val teacherEnc  = DataSecurity.encryptString(teacher)
    val schoolEnc   = DataSecurity.encryptString(school)
    http.GET[TeacherDetails](s"$deversityMicroservice/user/${authContext.user.id}/teacher/$teacherEnc/school/$schoolEnc/details") map {
      teacherDeets => Some(teacherDeets)
    } recover {
      case _: NotFoundException => None
    }
  }

  def getSchoolInfo(school: String)(implicit authContext: AuthContext, request: Request[_]): Future[Option[OrgDetails]] = {
    val schoolEnc = DataSecurity.encryptString(school)
    http.GET[OrgDetails](s"$deversityMicroservice/user/${authContext.user.id}/school/$schoolEnc/details") map {
      orgDeets => Some(orgDeets)
    } recover {
      case _: NotFoundException => None
    }
  }
}
