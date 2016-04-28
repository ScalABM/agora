/*
Copyright 2016 David R. Pugh

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package markets.actors

import java.util.UUID


/** Mixin trait defining behaviors for a class handling messages that are passed between actors. */
trait MessageHandler {

  /** Generates an identifier that uniquely identifies individual messages.
    *
    * @return a randomly generated UUID instance.
    */
  protected def uuid(): UUID = {
    UUID.randomUUID()
  }

  /** Generates a timestamp for a message that will be sent between actors.
    *
    * @return a long integer representing a timestamp for some message.
    */
  protected def timestamp(): Long = {
    System.currentTimeMillis()
  }

}
