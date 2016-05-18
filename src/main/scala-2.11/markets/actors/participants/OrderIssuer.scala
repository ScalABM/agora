/*
Copyright 2016 David R. Pugh

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
package markets.actors.participants


import markets.actors.participants.strategies.OrderIssuingStrategy
import markets.orders.{AskOrder, BidOrder}
import markets.orders.limit.{LimitAskOrder, LimitBidOrder}
import markets.orders.market.{MarketAskOrder, MarketBidOrder}
import markets.tradables.Tradable


trait OrderIssuer extends MarketParticipant {

  def askOrderIssuingStrategy: OrderIssuingStrategy[AskOrder]

  def bidOrderIssuingStrategy: OrderIssuingStrategy[BidOrder]

  override def receive: Receive = {
    askOrderIssuerBehavior orElse bidOrderIssuerBehavior orElse super.receive
  }

  /** Partial function defining how the `OrderIssuer` issues an `AskOrder`. */
  def askOrderIssuerBehavior: Receive = {
    case IssueAskOrder =>
      askOrderIssuingStrategy.investmentStrategy(tickers) match {
        case Some(tradable) =>
          val ticker = tickers(tradable)
          askOrderIssuingStrategy.tradingStrategy(tradable, ticker) match {
            case Some((price, quantity)) =>
              val askOrder = issueAskOrder(price, quantity, tradable)
              markets(tradable) tell(askOrder, self)
            case None =>  // no feasible trading strategy!
          }
        case None =>  // no feasible investment strategy!
      }
  }

  /** Partial function defining how the `OrderIssuer` issues a `BidOrder`. */
  def bidOrderIssuerBehavior: Receive = {
    case IssueBidOrder =>
      bidOrderIssuingStrategy.investmentStrategy(tickers) match {
        case Some(tradable) =>
          val ticker = tickers(tradable)
          bidOrderIssuingStrategy.tradingStrategy(tradable, ticker) match {
            case Some((price, quantity)) =>
              val bidOrder = issueBidOrder(price, quantity, tradable)
              markets(tradable) tell(bidOrder, self)
            case None =>  // no feasible trading strategy!
          }
        case None =>  // no feasible investment strategy!
      }
  }

  /* Create an `AskOrder` given some price, quantity, and tradable. */
  private[this] def issueAskOrder(price: Option[Long], quantity: Long, tradable: Tradable) = {
    price match {
      case Some(limitPrice) =>
        LimitAskOrder(self, limitPrice, quantity, timestamp(), tradable, uuid())
      case None =>
        MarketAskOrder(self, quantity, timestamp(), tradable, uuid())
    }
  }

  /* Create a `BidOrder` given some price, quantity, and tradable. */
  private[this] def issueBidOrder(price: Option[Long], quantity: Long, tradable: Tradable) = {
    price match {
      case Some(limitPrice) =>
        LimitBidOrder(self, limitPrice, quantity, timestamp(), tradable, uuid())
      case None =>
        MarketBidOrder(self, quantity, timestamp(), tradable, uuid())
    }
  }

}
