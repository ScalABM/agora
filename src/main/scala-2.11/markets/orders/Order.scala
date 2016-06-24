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

import java.util.UUID

import markets.Contract
import markets.tradables.Tradable


trait Order extends Contract {

  val counterparty: Option[UUID] = None

  def price: Long

  def quantity: Long

  def tradable: Tradable

  require(price >= 0, "Price must be non-negative")
  require(price % tradable.tick == 0, "Price must multiple tradable's tick size.")
  require(quantity > 0, "Quantity must be strictly positive.")

}


object Order {

  /** Instances of `Order` can be ordered based on their respective `price` fields. */
  def priceOrdering[A <: Order]: Ordering[A] = Ordering.by(order => order.price)

}

