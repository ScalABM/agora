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
package markets.tradables


/** A mixin trait that uses a total `Ordering` to express preferences over a particular type of `Tradable`.
  *
  * @note an `ordering` implies a particular `max` function that can be used as a binary `operator` to compare two
  *       `Tradable` instances.
  */
trait Preferences[T <: Tradable] extends Operator[T] {
  this: Tradable =>

  def ordering: Ordering[T]

  def operator: (T, T) => T = ordering.max

}