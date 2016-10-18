package markets.tradables.orders.bid

import markets.tradables.{LimitPrice, RandomUUID, Tradable}
import markets.tradables.orders.ask.HouseListing
import markets.tradables.orders.{Predicate, Preference, RandomIssuer, Timestamp}


trait HousingPreference extends BidOrder with LimitPrice with Predicate[HouseListing] with Preference[HouseListing] {

}


object HousingPreference {

  def apply(limit: Long, nonPriceCriteria: HouseListing => Boolean): HousingPreference = {
    DefaultHousingPreference(limit, nonPriceCriteria)
  }

  private case class DefaultHousingPreference(limit: Long, nonPriceCriteria: HouseListing => Boolean)
    extends HousingPreference with RandomIssuer with Timestamp with RandomUUID {

    /* An acceptable `HouseListing` must satisfy price and non-price criteria. */
    val isAcceptable: HouseListing => Boolean = listing => (listing.limit <= limit) && nonPriceCriteria(listing)

    /* Order of HouseListing from low to high based on price. */
    val ordering: Ordering[HouseListing] = Ordering.by(listing => listing.limit)

    /* Only bid for one house at a time! */
    val quantity: Long = 1

    val tradable: House = House()

  }

}