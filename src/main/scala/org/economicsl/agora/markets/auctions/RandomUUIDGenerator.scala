package org.economicsl.agora.markets.auctions

import java.util.UUID


/** Mixin trait that provides a method for generating random UUID instances. */
trait RandomUUIDGenerator {

  def nextUUID(): UUID = UUID.randomUUID()

}
