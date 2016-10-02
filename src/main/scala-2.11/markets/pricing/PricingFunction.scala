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
package markets.pricing


import markets.orders.{Order, Price}


/** Trait defining the interface for a `PricingFunction`. */
trait PricingFunction extends ((Order with Price, Order with Price) => Long) {

  def apply(order1: Order with Price, order2: Order with Price): Long

  protected def isIndividuallyRational(price: Long, order1: Order with Price, order2: Order with Price): Boolean = {
    val lower = math.min(order1.price, order2.price); val upper = math.max(order1.price, order2.price)
    lower <= price && price <= upper
  }

}
