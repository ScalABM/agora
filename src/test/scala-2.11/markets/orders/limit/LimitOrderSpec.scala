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
package markets.orders.limit


import markets.MarketsTestKit
import markets.tradables.Tradable
import org.scalatest.{BeforeAndAfterAll, FeatureSpec, GivenWhenThen, Matchers}

import scala.util.Random


class LimitOrderSpec extends FeatureSpec
  with MarketsTestKit
  with GivenWhenThen
  with Matchers
  with BeforeAndAfterAll {

  val prng: Random = new Random()

  val tradable: Tradable = Tradable("AAPL")

  feature("A LimitOrder object must have a strictly positive price.") {

    scenario("Creating an order with negative price.") {

      When("an order with a negative price is constructed an exception is thrown.")

      val negativePrice = -randomLimitPrice(prng)
      intercept[IllegalArgumentException](
        TestLimitOrder(uuid(), negativePrice, randomQuantity(prng), timestamp(), tradable, uuid())
      )

    }

  }

}
