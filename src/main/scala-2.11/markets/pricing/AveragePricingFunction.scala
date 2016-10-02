package markets.pricing
import markets.orders.{Order, Price}


class AveragePricingFunction extends PricingFunction {

  def apply(order1: Order with Price, order2: Order with Price): Long = {
    val price = (order1.price + order2.price) / 2  // this could lead to overflow!
    assert(isIndividuallyRational(price, order1, order2), "Price must be individually rational.")
    price
  }


}
