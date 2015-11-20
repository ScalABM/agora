/*
Copyright 2015 David R. Pugh, J. Doyne Farmer, and Dan F. Tang

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
package markets.settlement

import markets.orders.filled.{TotalFilledOrder, PartialFilledOrder, FilledOrderLike}

import scala.collection.immutable

import akka.actor.{Props, ActorRef, Actor}


/** Central counterparty (CCP) clearing mechanism.
  *
  * @note The key difference between CCP settlement and bilateral settlement is that CCP inserts
  *       itself as the counterparty to both the ask and the bid trading parties before
  *       processing the final transaction. By acting as a counterparty on every transaction the
  *       CCP effectively assumes all counterparty risk.
  */
class CCPSettlementMechanism extends Actor with SettlementMechanismLike with CounterpartyLike {

  /* BilateralClearingMechanism can be used to process novated fills. */
  val bilateralSettlementMechanism: ActorRef = context.actorOf(Props[BilateralSettlementMechanism])

  /** Central counter-party (CCP) settlement mechanism behavior. */
  val settlementMechanismBehavior: Receive = {
    case filledOrder: FilledOrderLike =>
      val novatedFilledOrders = novate(filledOrder)
      novatedFilledOrders foreach(novatedFill => bilateralSettlementMechanism ! novatedFill)
  }

  /** Novate a FillLike between two trading counterparties.
    *
    * @note The substitution of counterparties is typically accomplished through a legal process
    *       called contract novation. Novation discharges the contract between the original
    *       trading counterparties and creates two new, legally binding contracts – one between
    *       each of the original trading counterparties and the central counterparty.
    * @param fill a FillLike between two trading counterparties.
    * @return a list of two FillLikes - one between each of the original trading counterparties
    *         and the central counterparty.
    */
  def novate(fill: FilledOrderLike): immutable.Seq[FilledOrderLike] = fill match {
    case fill: PartialFilledOrder =>
      immutable.Seq(PartialFilledOrder((self, fill.counterParties._2), fill.price, fill.quantity, ???, fill.tradable),
        PartialFilledOrder((fill.counterParties._1, self), fill.price, fill.quantity, ???, fill.tradable))
    case fill: TotalFilledOrder =>
      immutable.Seq(TotalFilledOrder((self, fill.counterParties._2), fill.price, fill.quantity, ???, fill.tradable),
        TotalFilledOrder((fill.counterParties._1, self), fill.price, fill.quantity, ???, fill.tradable))
  }

  def receive: Receive = {
    settlementMechanismBehavior orElse ???
  }

}