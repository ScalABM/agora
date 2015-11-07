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
package markets.orders


/** Trait representing a limit order for a particular Tradable.
  *
  * The `LimitOrderLike` trait should be mixed-in with both `AskOrderLike` and `BidOrderLike` traits to create
  * instance of `LimitAskOrder` and `LimitBidOrder` classes.
  *
  */
trait LimitOrderLike {
  this: OrderLike =>

  /** Limit price for a Tradable. */
  val limitPrice: Long

}
