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

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import play.api.i18n.{Lang, MessagesProvider}

trait MockMessages {

  val mockMessagesProvider: MessagesProvider

  val lang = Lang("en")
  val messages = mockMessagesProvider.messages

  val MOCKED_MESSAGE = "mocked message"

  def mockAllMessages: OngoingStubbing[String] = {
    when(messages.apply(any[String](), any()))
      .thenReturn(MOCKED_MESSAGE)
  }
}
