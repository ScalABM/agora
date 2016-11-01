package org.economicsl.agora.markets.tradables


/** Concrete implementation of the `Tradable` interface for testing purposes. */
class TestTradable extends Tradable with RandomUUID


object TestTradable {

  def apply(): TestTradable = new TestTradable()

}
