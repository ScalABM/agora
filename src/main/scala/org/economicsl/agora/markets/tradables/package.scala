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
package org.economicsl.agora.markets

import scala.language.implicitConversions


/** Package contains classes and traits for modeling objects that can be traded via market mechanisms. */
package object tradables {

  trait Price[T] extends AnyVal {

    def value: T

  }


  /** Value class representing a numeric price. */
  case class ContinuousPrice(value: Double) extends Price[Double]


  /** Companion object for the `Price` value class. */
  object ContinuousPrice {

    /** Default ordering for `Price` instances is low to high based on the underlying value. */
    implicit val ordering: Ordering[ContinuousPrice] = Ordering.by(price => price.value)

    /** Implicit conversion used by the compiler to construct boiler plate code for >, <, >=, <=, operators. */
    implicit def mkOrderingOps(lhs: ContinuousPrice) = ordering.mkOrderingOps(lhs)

    val MaxValue = ContinuousPrice(Double.PositiveInfinity)

    val MinValue = ContinuousPrice(0.0)

  }


  case class DiscretePrice(value: Long) extends Price[Long]


  object DiscretePrice {

    /** Default ordering for `Price` instances is low to high based on the underlying value. */
    implicit val ordering: Ordering[DiscretePrice] = Ordering.by(price => price.value)

    /** Implicit conversion used by the compiler to construct boiler plate code for >, <, >=, <=, operators. */
    implicit def mkOrderingOps(lhs: DiscretePrice) = ordering.mkOrderingOps(lhs)

    val MaxValue = DiscretePrice(Long.MaxValue)

    val MinValue = DiscretePrice(0)

  }


  /** Default ordering for `Price` instances is low to high based on the underlying value. */
  object PriceOrdering extends Ordering[Price[T]] {

    /** Instances of `Price` are compared using their underlying values.
      *
      * @param p1 some `Price` instance.
      * @param p2 another `Price` instance.
      * @return -1 if `p1` is less than `p2`, 0 if `p1` equals `p2`, 1 otherwise.
      */
    def compare(p1: Price, p2: Price): Int = p1.value compare p2.value

  }

}
