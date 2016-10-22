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
package markets.tradables.orders.bid

import markets.tradables.RandomUUID
import markets.tradables.orders.ask.HouseListing
import markets.tradables.orders.{Preference, RandomIssuer, Timestamp}


trait HousingPreference extends LimitBidOrder with Preference[HouseListing]


object HousingPreference {

  def apply(limit: Long, nonPriceCriteria: HouseListing => Boolean): HousingPreference = {
    DefaultHousingPreference(limit, nonPriceCriteria)
  }

  private case class DefaultHousingPreference(limit: Long, nonPriceCriteria: HouseListing => Boolean)
    extends HousingPreference with RandomIssuer with Timestamp with RandomUUID {

    /* An acceptable `HouseListing` must satisfy both price and non-price criteria. */
    val isAcceptable: HouseListing => Boolean = listing => (listing.limit <= limit) && nonPriceCriteria(listing)

    /* Order of HouseListing from low to high based on price. */
    val ordering: Ordering[HouseListing] = Ordering.by(listing => listing.limit)

    /* Only bid for one house at a time! */
    val quantity: Long = 1

    val tradable: House = House()

  }

}