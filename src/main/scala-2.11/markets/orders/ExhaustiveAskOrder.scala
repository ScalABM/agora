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
package markets.orders


trait ExhaustiveAskOrder extends AskOrder {

  /** A boolean function that defines the set of acceptable `BidOrder` instances.
    *
    * @return a boolean function.
    * @note a `MatchingEngine` will use this `predicate` to `filter` its `bidOrderBook`.
    */
  def predicate: BidOrder => Boolean

  /** A binary operator used to select a single `BidOrder` from a collection of `BidOrder` instances.
    *
    * @return a binary operator.
    * @note a `MatchingEngine` will uses this `operator` to `reduce` the filtered `bidOrderBook`.
    */
  def operator: (BidOrder, BidOrder) => BidOrder

}