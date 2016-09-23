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

import markets.MarketsTestKit
import markets.tradables.Security
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

import scala.util.Random


class OrderSpec extends FeatureSpec
  with MarketsTestKit
  with GivenWhenThen
  with Matchers {

  val prng = new Random()

  feature("An Order object must have a strictly positive quantity.") {

    val lower: Long = 1
    val upper: Long = Long.MaxValue

    scenario("Creating an order with non-positive quantity.") {

      val testTradable: Security = Security(uuid())

      When("an order with a non-positive quantity is constructed an exception is thrown.")

      val negativeQuantity = -randomQuantity(lower, upper)
      intercept[IllegalArgumentException](
        TestOrder(uuid(), negativeQuantity, timestamp(), testTradable, uuid())
      )

      val zeroQuantity = 0
      intercept[IllegalArgumentException](
        TestOrder(uuid(), zeroQuantity, timestamp(), testTradable, uuid())
      )

    }

  }
}
