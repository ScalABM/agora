package markets

import akka.actor.{Actor, ActorRef, PoisonPill, Props, Terminated}
import akka.routing.{ActorRefRoutee, Router, RoutingLogic}

import scala.collection.immutable


class TestBrokerageActor(routingLogic: RoutingLogic,
                         routees: immutable.IndexedSeq[ActorRefRoutee])
  extends Actor {

  routees.foreach(routee => context watch routee.ref)

  def receive = {
    case Terminated(actorRef) =>
      router = router.removeRoutee(actorRef)
      if (router.routees.isEmpty) {
        self ! PoisonPill
      }
    case message =>
      router.route(message, sender())
  }

  private[this] var router = Router(routingLogic, routees)

}


object TestBrokerageActor {

  def props(routingLogic: RoutingLogic, routees: immutable.IndexedSeq[ActorRefRoutee]): Props = {
    Props(new TestBrokerageActor(routingLogic, routees))
  }

}