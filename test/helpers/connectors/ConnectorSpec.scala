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

package helpers.connectors

import com.cjwwdev.security.deobfuscation.{DeObfuscation, DeObfuscator, DecryptionError}
import com.cjwwdev.security.obfuscation.{Obfuscation, Obfuscator}
import connectors.{stringReads, stringWrites}
import helpers.other.{Fixtures, FutureAsserts, MockHttp, MockRequest}
import helpers.services.MockFeatureService
import models.RegistrationCode
import models.accounts.DeversityEnrolment
import models.deversity.{Classroom, OrgDetails, TeacherDetails}
import org.scalatestplus.play.PlaySpec
import play.api.http.Status
import play.api.libs.json._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

trait ConnectorSpec
  extends PlaySpec
    with FutureAsserts
    with MockHttp
    with MockRequest
    with Fixtures
    with Status
    with MockFeatureService {

  implicit lazy val request: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest().withSession("cookieId" -> generateTestSystemId(SESSION))

  private val contextIdWriter: OWrites[String] = OWrites[String] {
    str => Json.obj("contextId" -> Json.toJsFieldJsValueWrapper(str)(stringWrites))
  }

  private val contextIdReads: Reads[String] = Reads[String] {
    json => JsSuccess(json.\("contextId").as[String](stringReads))
  }

  implicit val contextIdFormat: OFormat[String] = OFormat(contextIdReads, contextIdWriter)

  case class TestModel(str: String, int: Int)
  implicit val format: OFormat[TestModel] = Json.format[TestModel]

  implicit val obfuscator: Obfuscator[TestModel] = new Obfuscator[TestModel] {
    override def encrypt(value: TestModel): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }

  implicit val deObfuscator: DeObfuscator[TestModel] = new DeObfuscator[TestModel] {
    override def decrypt(value: String): Either[TestModel, DecryptionError] = {
      DeObfuscation.deObfuscate[TestModel](value)
    }
  }

  implicit val enrObfuscator: Obfuscator[DeversityEnrolment] = new Obfuscator[DeversityEnrolment] {
    override def encrypt(value: DeversityEnrolment): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }

  implicit val teacherDetailsObs: Obfuscator[TeacherDetails] = new Obfuscator[TeacherDetails] {
    override def encrypt(value: TeacherDetails): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }

  implicit val orgDetailsObs: Obfuscator[OrgDetails] = new Obfuscator[OrgDetails] {
    override def encrypt(value: OrgDetails): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }

  implicit val regCodeObs: Obfuscator[RegistrationCode] = new Obfuscator[RegistrationCode] {
    override def encrypt(value: RegistrationCode): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }

  implicit val classSeqObs: Obfuscator[Seq[Classroom]] = new Obfuscator[Seq[Classroom]] {
    override def encrypt(value: Seq[Classroom]): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }

  implicit val classObs: Obfuscator[Classroom] = new Obfuscator[Classroom] {
    override def encrypt(value: Classroom): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }
}
