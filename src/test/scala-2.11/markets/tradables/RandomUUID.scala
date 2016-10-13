package markets.tradables

import java.util.UUID


/** Trait providing a randomly generated UUID for testing purposes. */
trait RandomUUID {
  this: Tradable =>

  /* Randomly generated UUID. */
  val uuid = UUID.randomUUID()

}
