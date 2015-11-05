package markets

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{FeatureSpecLike, Matchers, GivenWhenThen}


/** Test specification for a `MarketLike` actor.
  *
  * @note A `MarketLike` actor should directly receive `AskOrderLike` and `BidOrderLike` orders for a particular
  *       `Tradable` (filtering out any invalid orders) and then forward along all valid orders to a
  *       `ClearingMechanismLike` actor for further processing.
  */
class MarketLikeSpec extends TestKit(ActorSystem("MarketLikeSpec"))
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers {

  feature("A MarketLike actor should receive and process OrderLike messages.") {

    When("A MarketLike actor receives a valid OrderLike message...")
    Then("...it should forward it to its clearing mechanism...")
    Then("...it should notify the sender that the order has been accepted.")

    When("A MarketLike actor receives a invalid OrderLike message...")
    Then("...it should notify the sender that the order has been rejected.")
  }

}
