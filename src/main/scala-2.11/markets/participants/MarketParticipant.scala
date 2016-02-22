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
package markets.participants

import akka.actor.ActorRef
import akka.agent.Agent

import markets._
import markets.orders.Order
import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.{immutable, mutable}


/** Base Trait for all market participants. */
trait MarketParticipant extends StackableActor {

  val markets: mutable.Map[Tradable, ActorRef]

  val outstandingOrders: mutable.Set[Order]

  val tickers: mutable.Map[Tradable, Agent[immutable.Seq[Tick]]]

  protected final def submit(order: Order): Unit = {
    outstandingOrders += order  // SIDE EFFECT!
    markets(order.tradable) tell(order, self)
  }

  override def receive: Receive = {
    // handles processing of orders
    case Filled(order, residual, _, _) =>
      outstandingOrders -= order
      residual match {
        case Some(residualOrder) =>
          outstandingOrders += residualOrder
        case None =>  // do nothing!
      }
    case Rejected(order, _, _) =>
      outstandingOrders -= order

    // Handles adding and removing markets
    case Add(market, ticker, _, tradable, _) =>
      markets(tradable) = market
      tickers(tradable) = ticker
    case Remove(_, tradable, _) =>
      markets -= tradable
      tickers -= tradable
    case message =>
      super.receive(message)
  }

}
