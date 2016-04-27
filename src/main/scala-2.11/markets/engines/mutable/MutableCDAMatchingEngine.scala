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
package markets.engines.mutable

import markets.auctions.ContinuousDoubleAuction
import markets.orders.{AskOrder, BidOrder, Order}

/** Continuous Double Auction (CDA) Matching Engine. */
class MutableContinuousDoubleAuctionMatchingEngine(askOrdering: Ordering[AskOrder],
                                                   bidOrdering: Ordering[BidOrder],
                                                   initialPrice: Long)
  extends MutableTreeSetMatchingEngine with ContinuousDoubleAuction {






  protected val _askOrderBook = new MutableSortedAskOrderBook()(askOrdering)

  protected val _bidOrderBook = new MutableSortedBidOrderBook()(bidOrdering)

}


object MutableContinuousDoubleAuctionMatchingEngine {

  def apply(askOrdering: Ordering[AskOrder],
            bidOrdering: Ordering[BidOrder],
            initialPrice: Long): MutableContinuousDoubleAuctionMatchingEngine = {
    new MutableContinuousDoubleAuctionMatchingEngine(askOrdering, bidOrdering, initialPrice)
  }

}