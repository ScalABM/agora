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
package org.economicsl.agora.markets.auctions.mutable.continuous

import org.economicsl.agora.markets.auctions.pricing.WeightedAveragePricing
import org.economicsl.agora.markets.tradables.Tradable


/** Class implementing a k-Double Auction as described in [[http://www.sciencedirect.com/science/article/pii/002205318990121X Satterthwaite and Williams (JET, 1989)]].
  *
  * @param k 
  * @param tradable
  */
class KDoubleAuction(k: Double, tradable: Tradable) extends DoubleAuction(WeightedAveragePricing(1-k), WeightedAveragePricing(k), tradable){

  require(0 <= k && k <= 1, "The value of k must be in the unit interval (i.e., [0, 1]).")

}


object KDoubleAuction {

  def apply(k: Double, tradable: Tradable): KDoubleAuction = new KDoubleAuction(k, tradable)

}
