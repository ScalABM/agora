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
package markets.tradables


/** Base class for representing Tradable objects.
  *
  * @param symbol a string representation for the `Tradable` object.
  * @param tick  the minimum increment in which the price of this `Tradable` can change.
  */
class Tradable(val symbol: String, val tick: Long)


object Tradable {

  /** Auxiliary constructor for `Tradable`.
    *
    * @param symbol a string representation for the `Tradable` object.
    * @param tick  the minimum increment in which the price of this `Tradable` can change.
    * @return a `Tradable` object .
    */
  def apply(symbol: String, tick: Long): Tradable = {
    new Tradable(symbol, tick)
  }

  /** Auxiliary constructor for `Tradable`.
    *
    * @param symbol a string representation for the `Tradable` object.
    * @return a `Tradable` object whose `tick` size is 1.
    */
  def apply(symbol: String): Tradable = {
    new Tradable(symbol, 1)
  }

}
