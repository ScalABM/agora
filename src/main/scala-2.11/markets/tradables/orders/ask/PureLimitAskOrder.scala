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
package markets.tradables.orders.ask

import java.util.UUID

import markets.tradables.Tradable
import markets.tradables.orders.NoAdditionalCriteria
import markets.tradables.orders.bid.BidOrder


/** Trait defining the interface for a `PureLimitAskOrder`. */
trait PureLimitAskOrder extends LimitAskOrder with NoAdditionalCriteria[BidOrder]


/** Companion object for the `PureLimitAskOrder` trait.
  *
  * Provides a constructor for the default implementation of the `PureLimitAskOrder` trait.
  */
object PureLimitAskOrder {

  /** Creates an instance of a `PureLimitAskOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `PureLimitAskOrder`.
    * @param limit the minimum price at which the `PureLimitAskOrder` can be executed.
    * @param quantity the number of units of the `tradable` for which the `PureLimitAskOrder` was issued.
    * @param timestamp the time at which the `PureLimitAskOrder` was issued.
    * @param tradable the `Tradable` for which the `PureLimitAskOrder` was issued.
    * @param uuid the `UUID` of the `PureLimitAskOrder`.
    * @return an instance of a `PureLimitAskOrder`.
    */
  def apply(issuer: UUID, limit: Long, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID): PureLimitAskOrder = {
    DefaultPureLimitAskOrder(issuer, limit, quantity, timestamp, tradable, uuid)
  }

  private[this] case class DefaultPureLimitAskOrder(issuer: UUID, limit: Long, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID)
    extends PureLimitAskOrder {

    override val additionalCriteria: Option[(BidOrder) => Boolean] = super.additionalCriteria

  }

}
