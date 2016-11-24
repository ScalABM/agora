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
package org.economicsl.agora.markets.auctions.orderbooks

import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}
import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.OrderGenerator
import org.scalatest.{FeatureSpec, Matchers}



trait OrderBookSpec[O <: Order with Persistent, OB <: OrderBook[O]] extends FeatureSpec with Matchers with OrderGenerator {

  def orderBookFactory(tradable: Tradable): OB

}
