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

import java.util.UUID

import markets.tradables.Tradable
import markets.tradables.orders.NoAdditionalCriteria
import markets.tradables.orders.ask.AskOrder


/** Trait defining the interface for a `PureLimitBidOrder`. */
trait PureLimitBidOrder extends LimitBidOrder with NoAdditionalCriteria[AskOrder]


/** Companion object for the `PureLimitBidOrder` trait.
  *
  * Provides a constructor for the default implementation of the `PureLimitBidOrder` trait.
  */
object PureLimitBidOrder {

  /** Creates an instance of a `PureLimitBidOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `PureLimitBidOrder`.
    * @param limit the minimum price at which the `PureLimitBidOrder` can be executed.
    * @param quantity the number of units of the `tradable` for which the `PureLimitBidOrder` was issued.
    * @param timestamp the time at which the `PureLimitBidOrder` was issued.
    * @param tradable the `Tradable` for which the `PureLimitBidOrder` was issued.
    * @param uuid the `UUID` of the `PureLimitBidOrder`.
    * @return an instance of a `PureLimitBidOrder`.
    */
  def apply(issuer: UUID, limit: Long, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID): PureLimitBidOrder = {
    DefaultPureLimitBidOrder(issuer, limit, quantity, timestamp, tradable, uuid)
  }

  private[this] case class DefaultPureLimitBidOrder(issuer: UUID, limit: Long, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID)
    extends PureLimitBidOrder {

    override val additionalCriteria: Option[(AskOrder) => Boolean] = super.additionalCriteria

  }

}
