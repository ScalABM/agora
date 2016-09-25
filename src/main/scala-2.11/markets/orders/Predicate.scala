package markets.orders

import markets.tradables.Tradable


/** A mixin trait that uses a boolean function to express preferences over a particular `Tradable`. */
trait Predicate[T <: Tradable] {
  this: Tradable =>

  /** Boolean function used to determine whether some `Tradable` is acceptable.
    *
    * @return a boolean function that returns `true` if the `Tradable` is acceptable and `false` otherwise.
    */
  def isAcceptable: T => Boolean

}
