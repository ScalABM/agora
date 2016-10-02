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
package markets.tradables.orders

import markets.tradables.Tradable


/** A binary operator that can be used to choose between two `Tradable` instances.
  *
  * @tparam T the type of `Tradable` over which the binary operator is defined.
  */
trait Operator[T <: Tradable] {
  this: Tradable =>

  /** Binary operator used to compare two Tradable instances.
    *
    * @return a binary operator `op` with signature `op: (tradable1: T, tradable2: T) => T`.
    * @note the binary operator `op` should return `tradable1` if `tradable1` is preferred to `tradable2`; otherwise the
    *       operator should return `tradable2`. If the binary operator is non-associative, then this could cause the
    *       result of a `MatchingFunction` to be non-deterministic.
    */
  def operator: (T, T) => T

}
