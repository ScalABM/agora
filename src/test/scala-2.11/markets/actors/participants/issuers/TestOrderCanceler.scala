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
package markets.actors.participants.issuers

import akka.actor.Props

import markets.strategies.{OrderCancellationStrategy, OrderIssuingStrategy}
import markets.orders.{AskOrder, BidOrder, Order}


class TestOrderCanceler(askOrderIssuingStrategy: OrderIssuingStrategy[AskOrder],
                        bidOrderIssuingStrategy: OrderIssuingStrategy[BidOrder],
                        val orderCancellationStrategy: OrderCancellationStrategy)
  extends TestOrderIssuer(askOrderIssuingStrategy, bidOrderIssuingStrategy)
  with OrderCanceler
  with OrderTracker {

  var outstandingOrders = Set.empty[Order]

  wrappedBecome(orderCancelerBehavior)

}


object TestOrderCanceler {

  def props(askOrderIssuingStrategy: OrderIssuingStrategy[AskOrder],
            bidOrderIssuingStrategy: OrderIssuingStrategy[BidOrder],
            cancellationStrategy: OrderCancellationStrategy): Props = {
    Props(new TestOrderCanceler(askOrderIssuingStrategy, bidOrderIssuingStrategy, cancellationStrategy))
  }

}