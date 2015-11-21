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
package object markets {

  /** Base trait for all messages. */
  trait MessageLike {

    val timestamp: Long

  }


  /** Base trait for representing contracts. */
  trait ContractLike extends MessageLike


  case class OrderAccepted(timestamp: Long) extends MessageLike


  case class OrderRejected(timestamp: Long) extends MessageLike

}
