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
package markets.twosided.auctions.periodic

import markets.Fill
import markets.twosided.auctions.PostedPriceAuction
import markets.tradables.orders.ask.AskOrder
import markets.tradables.orders.bid.BidOrder


/** Trait defining the interface for a `PeriodicDoubleAuction`. */
trait PeriodicDoubleAuction[A <: AskOrder, B <: BidOrder] extends PostedPriceAuction[A, B] {

  def fill(): Iterable[Fill]

}
