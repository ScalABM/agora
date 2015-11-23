package markets.settlement.strategies

import akka.actor.Actor.Receive
import akka.event.LoggingReceive

import markets.orders.filled.FilledOrder


class LoggingSettlementStrategy extends SettlementStrategy {

  def settle: Receive = LoggingReceive {
    case filledOrder: FilledOrder => ???
  }

}
