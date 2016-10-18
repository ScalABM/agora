package markets

import markets.matching.FilterPreferredMatchingFunction
import markets.tradables.orders.ask.HouseListing
import markets.tradables.orders.bid.HousingPreference


class HousingMarket {

  val matchingFunction = new FilterPreferredMatchingFunction[HousingPreference, HouseListing]()

  val auctionMechanism = {

  }

  val settlementMechanism = ???
  
}
