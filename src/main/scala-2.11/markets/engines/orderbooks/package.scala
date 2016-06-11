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
package markets.engines


/** Classes for modeling order books.
  *
  * =`OrderBook` API=
  * An order book has some underlying collection of orders.  These `existingOrders` should contain
  * either ask orders or bid orders for a specific `Tradable` (i.e., physical good or some promise
  * to deliver physical goods).
  *
  * - An `OrderBook` should be able to `add` and `remove` orders from `existingOrders` in constant
  * time (i.e., implementations of these methods should be `O(1)` operations).
  * - An `OrderBook` should be able to filter `existingOrders` and return only those orders that
  * satisfy some predicate. Use cases: Suppose a market participant wanted to chose from orders
  * that satisfied certain criteria? suppose market participant wanted to trade with a specific
  * counterparty?
  *
  * ==The `Sorted` trait==
  * An order book that mixes in the Sorted trait should have some underlying sorted
  * collection of orders. These `existingSortedOrders` should contain either ask orders or bid
  * orders for a specific `Tradable`.
  *
  * - Implementations of `add` and `remove` should be `O(log n)` operations.
  * - An order book mixing in the `Sorted` trait should also be able to view the highest priority
  * order as well as remove and the highest priority order. The view operation should be
  * constant (i.e., `O(1)`) time; removing the highest priority order should be `O(log n)` time.
  * - Implementation of `existingSortedOrders` needs to all for multiple orders with the same
  * priority (i.e., could have multiple orders with the same price, could have two orders with the
  * same price arrive with the same timestamp from different market participants, etc).
  *
  * ==The `Bounded` trait==
  * An order book that mixes in the `Bounded` trait should have a `depth` field that specifies
  * the maximum number of orders that the order book can contain.  Implementations of `add`
  * methods will need to be modified accordingly.  When full, an order book implementing the
  * `Sorted` trait should probably `remove` the lowest price order(s) to make room for the new
  * order.
  *
  * @todo What to do when an `OrderBook` is full? Should order be rejected? Should the most "stale"
  *       order in the order book be removed? Surely this decision should be left up to the user.
  *       One solution would be to adopt the following signature for adding orders to an order
  *       book: `add(order: AskOrder): Try[Unit]`. This would allow the caller to respond
  *       differently when successfully adding an order versus failing to add an order (perhaps
  *       because the order book has mixed in the `Bounded` trait).
  *
  * ==Immutable vs Mutable==
  * All immutable order books should be guaranteed thread safe; mutable order books are not
  * guaranteed thread safe but can be safely used within Akka actors.  Should have tests
  * documenting any performance differences between immutable and mutable order books.
  *
  * Default implementations of order books should be immutable.
  */
package object orderbooks
