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
  * ==The `PriorityOrderBook` API==
  * A sorted order book maintains some underlying collection of `prioritisedOrders`. These
  * `prioritisedOrders` should contain either ask orders or bid orders for a specific `Tradable`.
  *
  * - Implementations of `add` and `remove` methods should be `O(log n)` operations.
  * - A `PriorityOrderBook` should be able to view the highest priority order as well as remove
  * and return the highest priority order. The view operation should be constant (i.e., `O(1)`)
  * time; removing the highest priority order should be `O(log n)` time.
  *
  * ==Thread safety==
  * Default implementations of `OrderBook` in the `markets-sandbox` are __not__ thread-safe and
  * are optimized for single-threaded performance. Check out implementations of order books
  * embedded in Akka actors in the `AkkABM` library for users interested in thread safe
  * implementations.
  */
package object orderbooks
