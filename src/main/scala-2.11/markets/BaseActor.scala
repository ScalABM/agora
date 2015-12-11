/*
Copyright 2015 David R. Pugh, J. Doyne Farmer, and Dan F. Tang

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
package markets

import akka.actor.{Actor, ActorLogging}

import java.util.UUID


/** Base trait for all actors. */
trait BaseActor extends Actor with ActorLogging {

  def baseActorBehavior: Receive = {
    case message => log.debug(message.toString)
  }

  /** Method used to timestamp all sent messages. */
  def timestamp(): Long = {
    System.currentTimeMillis()
  }

  /** Method used to timestamp all sent messages. */
  def uuid(): UUID = {
    UUID.randomUUID()
  }
}
