package markets.tradables.orders.ask

import markets.tradables.{LimitPrice, RandomUUID, Tradable}
import markets.tradables.orders.{PriceCriteria, RandomIssuer, Timestamp}
import markets.tradables.orders.bid.HousingPreference


trait HouseListing extends AskOrder with LimitPrice with PriceCriteria[HousingPreference]


object HouseListing {

  def appply(limit: Long): HouseListing = {
    ???
  }

  private[this] case class DefaultHouseListing(limit: Long)
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

    val tradable: House = House()

    val quantity: Long = 1

  }

}
