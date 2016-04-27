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
package markets.engines.orderbooks.immutable

import markets.engines.orderbooks.OrderBooks
import markets.orders.{AskOrder, BidOrder}

import scala.collection.immutable


/** Mixin trait providing immutable order books.
  *
  * @tparam A the type of orders stored in the order book.
  * @tparam B the type of underlying immutable collection used to store the orders.
  */
trait ImmutableOrderBooks[A <: immutable.Iterable[AskOrder], B <: immutable.Iterable[BidOrder]]
  extends OrderBooks[A, B]

