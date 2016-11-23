package org.economicsl.agora.markets.auctions.matching

import org.economicsl.agora.markets.auctions.mutable.orderbooks.{SortedAskOrderBook, SortedBidOrderBook}
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder

import scala.collection.immutable


class FindFirstAcceptableMatches[A <: LimitAskOrder with Persistent, B <: LimitBidOrder with Persistent]
  extends ((SortedAskOrderBook[A], SortedBidOrderBook[B]) => Option[immutable.TreeSet[(A, B)]]){

  def apply(askOrderBook: SortedAskOrderBook[A], bidOrderBook: SortedBidOrderBook[B]): Option[immutable.TreeSet[(A, B)]] = {
    if (askOrderBook.isEmpty || bidOrderBook.isEmpty) {
      None
    } else {
      val matchedOrders = zip(askOrderBook, bidOrderBook)
      val acceptableMatches = matchedOrders.takeWhile(pair => isMutuallyAcceptable(pair))
      if (acceptableMatches.isEmpty) None else Some(acceptableMatches)
    }
  }

  /* todo consider moving this to `Predicate` trait! */
  private[this] def isMutuallyAcceptable(pair: (A, B)): Boolean = {
    val (askOrder, bidOrder) = pair
    askOrder.isAcceptable(bidOrder) && bidOrder.isAcceptable(askOrder)
  }

  /* todo this should be a method of the `OrderBook` class! */
  private[this] def zip(askOrderBook: SortedAskOrderBook[A], bidOrderBook: SortedBidOrderBook[B]): immutable.TreeSet[(A, B)] = {
    var pairs = immutable.TreeSet.empty[(A, B)]
    val (askOrders, bidOrders) = (askOrderBook.iterator, bidOrderBook.iterator)
    while (askOrders.hasNext && bidOrders.hasNext) {
      pairs = pairs + ((askOrders.next(), bidOrders.next()))
    }
    pairs
  }

}


object FindFirstAcceptableMatches {

  def apply[A <: LimitAskOrder with Persistent, B <: LimitBidOrder with Persistent](): FindFirstAcceptableMatches[A, B] = {
    new FindFirstAcceptableMatches()
  }

}