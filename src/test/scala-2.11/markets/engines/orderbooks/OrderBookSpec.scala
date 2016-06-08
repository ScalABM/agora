package markets.engines.orderbooks

import markets.MarketsTestKit
import markets.orders.{AskOrder, BidOrder}
import markets.tradables.Tradable
import org.scalatest.{FeatureSpec, Matchers}

import scala.util.Random


abstract class OrderBookSpec(name: String) extends FeatureSpec
  with Matchers
  with MarketsTestKit {
  
  def askOrderBook(): OrderBook[AskOrder, Iterable[AskOrder]]

  def bidOrderBook(): OrderBook[BidOrder, Iterable[BidOrder]]

  def invalidTradable: Tradable

  def validTradable: Tradable

  def prng: Random

  feature(s"A $name should be able to add valid orders.") {

    scenario(s"Adding a valid order to an $name[AskOrder].") {
      val order = randomAskOrder(tradable=validTradable)
      val orderBook = askOrderBook()
      orderBook.add(order)
      orderBook.peek() should be(Some(order))
    }

    scenario(s"Adding a valid order to an $name[BidOrder]") {
      val order = randomBidOrder(tradable=validTradable)
      val orderBook = bidOrderBook()
      orderBook.add(order)
      orderBook.peek() should be(Some(order))
    }

  }


  feature(s"A $name should not add invalid orders.") {

    scenario(s"Adding an invalid order to an $name[AskOrder].") {
      val invalidOrder = randomAskOrder(tradable=invalidTradable)
      intercept[IllegalArgumentException](askOrderBook().add(invalidOrder))
    }

    scenario(s"Adding an invalid order to an $name[BidOrder]") {
      val invalidOrder = randomBidOrder(tradable=invalidTradable)
      intercept[IllegalArgumentException](bidOrderBook().add(invalidOrder))
    }

  }

  feature(s"A $name should be able to peek at the head of its underlying collection.") {

    scenario(s"Peeking at the head of an empty $name[AskOrder].") {
      val orderBook = askOrderBook()
      orderBook.peek() should be(None)
    }

    scenario(s"Peeking at the head of an empty $name[BidOrder].") {
      val orderBook = bidOrderBook()
      orderBook.peek() should be(None)
    }

  }

  feature(s"A $name should be able to pop off the head of its underlying collection.") {

    scenario(s"Popping the head from an empty $name[AskOrder].") {
      val orderBook = askOrderBook()
      val poppedOrder = orderBook.pop()
      poppedOrder should be(None)
    }

    scenario(s"Popping the head from an empty $name[BidOrder].") {
      val orderBook = bidOrderBook()
      val poppedOrder = orderBook.pop()
      poppedOrder should be(None)
    }

    scenario(s"Popping the head from a non-empty $name[AskOrder].") {
      val order = randomAskOrder(tradable=validTradable)
      val orderBook = askOrderBook()
      orderBook.add(order)
      val poppedOrder = orderBook.pop()
      poppedOrder should be(Some(order))
      orderBook.peek() should be(None)
    }

    scenario(s"Popping the head from a non-empty $name[BidOrder].") {
      val order = randomBidOrder(tradable=validTradable)
      val orderBook = bidOrderBook()
      orderBook.add(order)
      val poppedOrder = orderBook.pop()
      poppedOrder should be(Some(order))
      orderBook.peek() should be(None)
    }

  }

  feature(s"A $name should be able to pop valid orders.") {

    scenario(s"Popping a valid order from a non-empty $name[AskOrder].") {
      val order = randomAskOrder(tradable=validTradable)
      val orderBook = askOrderBook()
      orderBook.add(order)
      val poppedOrder = orderBook.pop(order)
      poppedOrder should be(Some(order))
      orderBook.peek() should be(None)
    }

    scenario(s"Popping a valid order from a non-empty $name[BidOrder]") {
      val order = randomBidOrder(tradable=validTradable)
      val orderBook = bidOrderBook()
      orderBook.add(order)
      val poppedOrder = orderBook.pop(order)
      poppedOrder should be(Some(order))
      orderBook.peek() should be(None)
    }

    scenario(s"Popping a valid order that is not contained in $name[AskOrder].") {
      val order = randomAskOrder(tradable=validTradable)
      val orderBook = askOrderBook()
      val poppedOrder = orderBook.pop(order)
      poppedOrder should be(None)
    }

    scenario(s"Popping a valid order that is not contained in $name[BidOrder]") {
      val order = randomBidOrder(tradable=validTradable)
      val orderBook = bidOrderBook()
      val poppedOrder = orderBook.pop(order)
      poppedOrder should be(None)
    }

  }

  feature(s"A $name should be able to remove valid orders.") {

    scenario(s"Removing a valid order from an $name[AskOrder].") {
      val order = randomAskOrder(tradable=validTradable)
      val orderBook = askOrderBook()
      orderBook.add(order)
      orderBook.remove(order)
      orderBook.peek() should be(None)
    }

    scenario(s"Removing a valid order from an $name[BidOrder]") {
      val order = randomBidOrder(tradable=validTradable)
      val orderBook = bidOrderBook()
      orderBook.add(order)
      orderBook.remove(order)
      orderBook.peek() should be(None)
    }

  }

  feature(s"A $name should not remove invalid orders.") {

    scenario(s"Removing an invalid order from an $name[AskOrder].") {
      val invalidOrder = randomAskOrder(tradable=invalidTradable)
      intercept[IllegalArgumentException](askOrderBook().remove(invalidOrder))
    }

    scenario(s"Removing an invalid order from a $name[BidOrder]") {
      val invalidOrder = randomBidOrder(tradable=invalidTradable)
      intercept[IllegalArgumentException](bidOrderBook().remove(invalidOrder))
    }

  }

}
