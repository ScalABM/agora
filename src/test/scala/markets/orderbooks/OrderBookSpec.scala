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
package markets.orderbooks

import java.util.UUID

import markets.OrderGenerator
import markets.tradables.orders.Order
import markets.tradables.{TestTradable, Tradable}

import org.scalatest.{FeatureSpec, Matchers}


trait OrderBookSpec[O1 <: Order, OB1 <: OrderBook[O1, collection.GenMap[UUID, O1]]]
  extends FeatureSpec with Matchers with OrderGenerator {

  def orderBookFactory(tradable: Tradable): OB1

}
