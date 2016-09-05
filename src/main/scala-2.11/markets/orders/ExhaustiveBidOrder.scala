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


trait ExhaustiveBidOrder extends BidOrder {

  /** A boolean function that defines the set of acceptable `AskOrder` instances.
    *
    * @return a boolean function.
    * @note a `MatchingEngine` will use this `predicate` to `filter` its `askOrderBook`.
    */
  def predicate: AskOrder => Boolean

  /** A binary operator used to select a single `AskOrder` from a collection of `AskOrder` instances.
    *
    * @return a binary operator.
    * @note a `MatchingEngine` will uses this `operator` to `reduce` the filtered `askOrderBook`.
    */
  def operator: (AskOrder, AskOrder) => AskOrder

}
