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
package markets.clearing.engines

import java.util.UUID

import markets.clearing.strategies.BestLimitPriceFormationStrategy
import markets.orders.orderings.PriceOrdering
import markets.orders.{AskOrder, BidOrder, Order}

import scala.collection.immutable


class CDAMatchingEngine(val askOrdering: PriceOrdering[AskOrder],
                        val bidOrdering: PriceOrdering[BidOrder],
                        initialPrice: Long)
  extends CDAMatchingEngineLike
  with BestLimitPriceFormationStrategy {

  protected var orderBook = immutable.Set.empty[Order]

  protected var mostRecentPrice = initialPrice

}


object CDAMatchingEngine {

  def apply(askOrdering: PriceOrdering[AskOrder],
            bidOrdering: PriceOrdering[BidOrder],
            initialPrice: Long): CDAMatchingEngine = {
    new CDAMatchingEngine(askOrdering, bidOrdering, initialPrice)
  }

}


