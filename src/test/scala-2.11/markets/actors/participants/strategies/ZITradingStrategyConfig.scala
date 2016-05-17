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
package markets.actors.participants.strategies

import com.typesafe.config.Config


class ZITradingStrategyConfig(config: Config) {

  val askQuantity: Long = config.getLong("ask-quantity")

  val bidQuantity: Long = config.getLong("bid-quantity")

  val minAskPrice: Double = config.getDouble("minimum-ask-price")

  val maxAskPrice: Double = config.getDouble("maximum-ask-price")

  val minBidPrice: Double = config.getDouble("minimum-bid-price")

  val maxBidPrice: Double = config.getDouble("maximum-bid-price")

  val marketOrderProbability: Double = config.getDouble("market-order-probability")

}


object ZITradingStrategyConfig {

  def apply(config: Config): ZITradingStrategyConfig = {
    new ZITradingStrategyConfig(config)
  }

}