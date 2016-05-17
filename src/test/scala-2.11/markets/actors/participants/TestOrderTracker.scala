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
package markets.actors.participants

import akka.actor.Props

import markets.actors.participants.strategies.OrderIssuingStrategy
import markets.orders.{AskOrder, BidOrder, Order}


class TestOrderTracker(askOrderIssuingStrategy: OrderIssuingStrategy[AskOrder],
                       bidOrderIssuingStrategy: OrderIssuingStrategy[BidOrder])
  extends TestOrderIssuer(askOrderIssuingStrategy, bidOrderIssuingStrategy)
  with OrderTracker {

  var outstandingOrders = Set.empty[Order]

  wrappedBecome(orderTrackerBehavior)

}


object TestOrderTracker {

  def props(askOrderIssuingStrategy: OrderIssuingStrategy[AskOrder],
            bidOrderIssuingStrategy: OrderIssuingStrategy[BidOrder]): Props = {
    Props(new TestOrderTracker(askOrderIssuingStrategy, bidOrderIssuingStrategy))
  }

}