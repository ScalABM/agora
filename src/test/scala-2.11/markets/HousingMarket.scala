package markets

import markets.matching.FilterPreferredMatchingFunction


class TestHousingMarket {

  val matchingFunction = new FilterPreferredMatchingFunction[HousingPreferences, HouseListings]()

  val auctionMechanism = {

  }
}
