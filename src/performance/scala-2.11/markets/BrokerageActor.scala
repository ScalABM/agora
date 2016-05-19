package markets

import akka.actor.{Actor, PoisonPill, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}

import markets.actors.participants.TestOrderIssuer
import markets.actors.participants.strategies.OrderIssuingStrategy
import markets.orders.{AskOrder, BidOrder}


class BrokerageActor(askOrderIssuingStrategy: OrderIssuingStrategy[AskOrder],
                     bidOrderIssuingStrategy: OrderIssuingStrategy[BidOrder],
                     numberOrderIssuers: Int) extends Actor {

  val orderIssuers = for (i <- 1 to numberOrderIssuers) yield {
    context.actorOf(TestOrderIssuer.props(askOrderIssuingStrategy, bidOrderIssuingStrategy))
  }
  orderIssuers.foreach( orderIssuer => context watch orderIssuer)

  var router = Router(RoundRobinRoutingLogic(), orderIssuers.map(orderIssuer => ActorRefRoutee(orderIssuer)))

  def receive = {
    case Terminated(a) =>
      router = router.removeRoutee(a)
      if (router.routees.isEmpty) {
        self ! PoisonPill
      }
    case message =>
      router.route(message, sender())
  }
}


object BrokerageActor {

  def props(askOrderIssuingStrategy: OrderIssuingStrategy[AskOrder],
            bidOrderIssuingStrategy: OrderIssuingStrategy[BidOrder],
            numberOrderIssuers: Int): Props = {
    Props(new BrokerageActor(askOrderIssuingStrategy, bidOrderIssuingStrategy, numberOrderIssuers))
  }
}