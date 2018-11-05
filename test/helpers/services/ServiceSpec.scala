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

package helpers.services

import com.cjwwdev.http.headers.HeaderPackage
import com.cjwwdev.implicits.ImplicitDataSecurity._
import helpers.connectors.{MockAuthMicroserviceConnector, MockSessionStoreConnector}
import helpers.other.{Fixtures, FutureAsserts}
import org.scalatestplus.play.PlaySpec
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

trait ServiceSpec
  extends PlaySpec
    with FutureAsserts
    with Fixtures
    with MockAuthMicroserviceConnector
    with MockSessionStoreConnector {

  implicit lazy val request: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest()
      .withHeaders("cjww-headers" -> HeaderPackage("testAppId", Some(generateTestSystemId(SESSION))).encrypt)

}
