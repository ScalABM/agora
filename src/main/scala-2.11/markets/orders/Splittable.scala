package markets.orders


trait Splittable[T <: Splittable[T]] {
  this: Order =>

  val isSplittable: Boolean = true

  /** Splits the order into a filled order and a residual order.
    *
    * @param residualQuantity
    * @return a tuple of orders.
    */
  def split(residualQuantity: Long): (T, T)

}
