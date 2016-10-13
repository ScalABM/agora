package markets.tradables.orders

trait Timestamp {

  /** Generate a timestamp. */
  def timestamp(): Long = System.currentTimeMillis()

}
