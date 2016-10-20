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


/** A mixin trait that uses a boolean function to express preferences over a particular `Tradable`. */
trait Predicate[-T <: Tradable] {
  this: Order =>

  /** Boolean function used to determine whether some `Tradable` is acceptable.
    *
    * @return a boolean function that returns `true` if the `Tradable` is acceptable and `false` otherwise.
    */
  def isAcceptable: T => Boolean

}
