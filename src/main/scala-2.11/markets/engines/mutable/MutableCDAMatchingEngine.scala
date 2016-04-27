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

import markets.engines.mechanisms.CDAMatchingMechanism
import markets.engines.orderbooks.mutable.MutableOrderBooks
import markets.orders.{AskOrder, BidOrder}

import scala.collection.mutable


/** Continuous Double Auction (CDA) Matching Engine. */
trait MutableCDAMatchingEngine[A <: mutable.Iterable[AskOrder], B <: mutable.Iterable[BidOrder]]
  extends CDAMatchingMechanism[A, B]
  with MutableOrderBooks[A, B]
