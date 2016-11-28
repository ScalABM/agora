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
package org.economicsl.agora.markets.tradables.orders

import org.economicsl.agora.markets.tradables._

import scala.collection.immutable


/** Mixin trait defining a Price-Quantity schedule.
  *
  * A price-quantity schedule is a step-wise specification of offers to buy or sell various quantities at discrete
  * price points.
  */
trait PriceQuantitySchedule {
  this: Order =>

  def schedule: immutable.Iterable[(Price, Long)]

}


trait SingleUnit extends PriceQuantitySchedule with LimitPrice with Quantity {
  this: Order =>

  val quantity = 1

  val schedule = immutable.Iterable((limit, quantity))

}


trait SinglePricePoint extends PriceQuantitySchedule with LimitPrice with MultiUnit {
  this: Order=>

  val schedule = immutable.Iterable((limit, quantity))

}