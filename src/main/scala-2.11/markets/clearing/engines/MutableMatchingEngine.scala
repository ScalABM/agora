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

import markets.orders.{AskOrder, BidOrder}

import scala.collection.mutable


/** Base trait for all matching engines with mutable protected order books. */
trait MutableMatchingEngine extends MatchingEngine {

  /** MutableMatchingEngine should have a protected collection of ask orders. */
  protected def _askOrderBook: mutable.Iterable[AskOrder]

  /** MutableMatchingEngine should have a protected collection of bid orders. */
  protected def _bidOrderBook: mutable.Iterable[BidOrder]

}
