package markets.orders


trait Splittable[T <: Splittable[T]] {
  this: Order =>

  val isSplittable: Boolean = true

  /** Splits the order into two separate orders.
    *
    * @param residualQuantity
    * @return a tuple of orders.
    */
  def split(residualQuantity: Long): (T, T)

}
