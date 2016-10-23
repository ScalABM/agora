package markets.tradables.orders.ask

import markets.tradables.{House, LimitPrice, RandomUUID}
import markets.tradables.orders.{PriceCriteria, RandomIssuer, Timestamp}
import markets.tradables.orders.bid.HousingPreference


trait HouseListing extends AskOrder with LimitPrice with PriceCriteria[HousingPreference]


object HouseListing {

  def appply(limit: Long, tradable: House): HouseListing = {
    DefaultHouseListing(limit, tradable)
  }

  private[this] case class DefaultHouseListing(limit: Long, tradable: House)
    extends HouseListing with RandomIssuer with Timestamp with RandomUUID {

    val priceCriteria: (HousingPreference) => Boolean = {
      preference => this.limit >= preference.limit
    }

    /** Boolean function used to determine whether some `HousingPreference` is acceptable.
      *
      * @return a boolean function that returns `true` if the `HousingPrefernce` is acceptable and `false` otherwise.
      */
    val isAcceptable: (HousingPreference) => Boolean = {
      preference => priceCriteria(preference)
    }

    val quantity: Long = 1

  }

}
