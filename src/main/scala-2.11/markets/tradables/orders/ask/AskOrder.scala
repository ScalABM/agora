/*
Copyright 2016 ScalABM

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
package markets.tradables.orders.ask

import markets.tradables.orders.Order
import markets.tradables.Quantity


/** Trait defining an order to sell a `Tradable` object. */
trait AskOrder extends Order with Quantity


object AskOrder {

  implicit val ordering: Ordering[AskOrder] = new DefaultOrdering

  val priority: Ordering[AskOrder] = ordering.reverse

  /** Class implementing an ordering over various `AskOrder` types. */
  private class DefaultOrdering extends Ordering[AskOrder] {

    def compare(askOrder1: AskOrder, askOrder2: AskOrder): Int = (askOrder1, askOrder2) match {
      case (_: MarketAskOrder, _: LimitAskOrder) => -1
      case (limitOrder1: LimitAskOrder, limitOrder2: LimitAskOrder) =>
        LimitAskOrder.ordering.compare(limitOrder1, limitOrder2)
      case (_: LimitAskOrder, _: MarketAskOrder) => 1
      case (marketOrder1: MarketAskOrder, marketOrder2: MarketAskOrder) =>
        MarketAskOrder.ordering.compare(marketOrder1, marketOrder2)
    }

  }

}
