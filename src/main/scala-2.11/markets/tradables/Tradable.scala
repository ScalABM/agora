package markets.tradables

import java.util.UUID


/** Base trait defining the interface for any object whose ownership can be transferred via a `Market`. */
trait Tradable {

  /** A unique identifier used to distinguish a `Tradable` from other `Tradable` objects. */
  def uuid: UUID

}
