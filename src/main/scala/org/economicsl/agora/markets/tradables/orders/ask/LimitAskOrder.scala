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
package org.economicsl.agora.markets.tradables.orders.ask

import org.economicsl.agora.markets.tradables.LimitPrice
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder
import org.economicsl.agora.markets.tradables.orders.{Persistent, PriceCriteria}


/** Trait defining a `LimitAskOrder`. */
trait LimitAskOrder extends AskOrder with LimitPrice with PriceCriteria[BidOrder with Persistent]


/** Companion object for the `LimitAskOrder` trait.
  *
  * Provides various orderings for `LimitAskOrder` instances.
  */
object LimitAskOrder {


  /** By default, instances of `LimitAskOrder` are ordered based on `limit` price from lowest to highest */
  implicit def ordering[A <: LimitAskOrder]: Ordering[A] = LimitPrice.ordering

  /** The highest priority `LimitAskOrder` is the one with the lowest `limit` price. */
  def priority[A <: LimitAskOrder]: Ordering[A] = LimitPrice.ordering.reverse

}