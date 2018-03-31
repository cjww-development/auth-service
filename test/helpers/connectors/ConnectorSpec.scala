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

import connectors.{stringReads, stringWrites}
import helpers.other.{Fixtures, FutureAsserts, MockHttp, MockRequest}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
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
    with GuiceOneAppPerSuite {

  implicit lazy val request: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest().withSession("cookieId" -> generateTestSystemId(SESSION))

  private val contextIdWriter: OWrites[String] = new OWrites[String] {
    override def writes(o: String) = Json.obj(
      "contextId" -> Json.toJsFieldJsValueWrapper(o)(stringWrites)
    )
  }

  private val contextIdReads: Reads[String] = new Reads[String] {
    override def reads(json: JsValue) = JsSuccess(json.\("contextId").as[String](stringReads))
  }

  implicit val contextIdFormat: OFormat[String] = OFormat(contextIdReads, contextIdWriter)

  case class TestModel(str: String, int: Int)
  implicit val format: OFormat[TestModel] = Json.format[TestModel]
}
