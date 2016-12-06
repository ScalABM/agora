package org.economicsl.agora.markets.tradables.orders

import org.economicsl.agora.markets.tradables.Price


trait MultivariateExcessDemandFunction extends Order with Persistent {

  def excessDemand: Vector[Price] => Vector[Long]

}
