package markets.clearing.engines

import markets.orders.{BidOrderLike, AskOrderLike}

import scala.collection.immutable


class ContinuousDoubleAuction(var askOrderBook: immutable.Iterable[AskOrderLike],
                              var bidOrderBook: immutable.Iterable[BidOrderLike]) extends ContinuousDoubleAuctionLike


