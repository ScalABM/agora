/*
Copyright 2015 David R. Pugh, J. Doyne Farmer, and Dan F. Tang

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
package markets.tickers

import markets.Fill


/** Class representing a Tick.
  *
  * @param askPrice
  * @param bidPrice
  * @param price
  * @param quantity
  */
case class Tick(askPrice: Long,
                bidPrice: Long,
                price: Long,
                quantity: Long,
                timestamp: Long)


object Tick {

  /** Generates a Tick from a Fill.
    *
    * @param fill
    * @return a new Tick instance generated using information contained in the `fill`.
    */
  def fromFill(fill: Fill): Tick = {
    Tick(fill.askOrder.price, fill.bidOrder.price, fill.price, fill.quantity, fill.timestamp)
  }
}
