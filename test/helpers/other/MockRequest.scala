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

package helpers.other

import akka.util.ByteString
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSCookie, WSResponse}

import scala.xml.Elem

trait MockRequest {
  def fakeHttpResponse(statusCode: Int, bodyContents: String = ""): WSResponse = new WSResponse {
    override def cookie(name: String): Option[WSCookie] = ???
    override def underlying[T]: T                       = ???
    override def body: String                           = bodyContents
    override def bodyAsBytes: ByteString                = ???
    override def cookies: Seq[WSCookie]                 = ???
    override def allHeaders: Map[String, Seq[String]]   = ???
    override def xml: Elem                              = ???
    override def statusText: String                     = ???
    override def json: JsValue                          = ???
    override def header(key: String): Option[String]    = ???
    override def status: Int                            = statusCode
  }
}
