package markets.tradables.orders

import java.util.UUID


/** Trait providing a randomly generated issuer for testing purposes. */
trait RandomIssuer {
  this: Order =>

  /* Randomly generated issuer UUID. */
  val issuer = UUID.randomUUID()

}
