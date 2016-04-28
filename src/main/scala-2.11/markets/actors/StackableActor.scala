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

import akka.actor.{Actor, ActorLogging}

import java.util.UUID


trait StackableActor extends Actor with ActorLogging {

  def receive: Receive = {
    case message if wrappedReceive.isDefinedAt(message) =>
      wrappedReceive(message)
    case message =>
      unhandled(message)
  }

  /* Method used to timestamp all sent messages. */
  protected def timestamp(): Long = {
    System.currentTimeMillis()
  }

  /* Method used to specify a unique id for each sent message. */
  protected def uuid(): UUID = {
    UUID.randomUUID()
  }

  protected def wrappedBecome(receive: Receive): Unit = {
    wrappedReceive = receive
  }

  private[this] var wrappedReceive: Receive = {
    case message => unhandled(message)
  }

}
