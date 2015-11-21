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
package markets.orders.filled

import akka.actor.ActorRef

import markets.tradables.Tradable


case class PartialFilledOrder(counterparty: Option[ActorRef],
                              issuer: ActorRef,
                              price: Long,
                              quantity: Long,
                              timestamp: Long,
                              tradable: Tradable) extends FilledOrderLike


object PartialFilledOrder {

  def apply(counterparty: ActorRef,
            issuer: ActorRef,
            price: Long,
            quantity: Long,
            timestamp: Long,
            tradable: Tradable): PartialFilledOrder = {
    new PartialFilledOrder(Some(counterparty), issuer, price, quantity, timestamp, tradable)
  }

}
