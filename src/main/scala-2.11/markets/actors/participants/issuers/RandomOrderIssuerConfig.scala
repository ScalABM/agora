package markets.actors.participants.issuers


trait RandomOrderIssuerConfig extends OrderIssuerConfig {

  lazy val seed = {
    if (config.getString("seed").equalsIgnoreCase("None")) {
      None
    } else {
      Some(config.getLong("seed"))
    }
  }

}
