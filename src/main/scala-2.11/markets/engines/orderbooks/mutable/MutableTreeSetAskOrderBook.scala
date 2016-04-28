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
package markets.engines.orderbooks.mutable

import markets.orders.AskOrder

import scala.collection.mutable


/** Class implementing a mutable collection of ask orders using a `TreeSet`.
  *
  * @note Adding and removing orders are `O(log n)` operations where `n` is the size of the
  *       order book.
  */
class MutableTreeSetAskOrderBook(implicit val ordering: Ordering[AskOrder])
  extends GenericMutableAskOrderBook[mutable.TreeSet[AskOrder]] with MutableTreeSetLike[AskOrder] {

  protected val backingStore: mutable.TreeSet[AskOrder] = {
    mutable.TreeSet.empty[AskOrder]
  }

}