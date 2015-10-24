[![Build Status](https://travis-ci.org/ScalABM/markets-sandbox.svg?branch=develop)](https://travis-ci.org/ScalABM/markets-sandbox)
[![Coverage Status](https://coveralls.io/repos/ScalABM/markets-sandbox/badge.svg?branch=develop&service=github)](https://coveralls.io/github/ScalABM/markets-sandbox?branch=develop)

# markets-sandbox
A sandbox for building and testing scalable implementations of various market micro-structures.

## Some ideas for an API for scalable markets...

The Markets API explicitly defines various non-equilibrium processes by which market prices and quantities are determined.

### Requirements
The Markets API needs to be sufficiently flexible in order to handle markets for relatively homogeneous goods (firm non-labor inputs, firm outputs, final consumption goods, standard financial products etc.) as well as markets for relatively heterogeneous goods (i.e., labor, housing, non-standard financial products, etc).

Here is my (likely incomplete) list..

* Receive buy and sell orders from other actors.
* Accept (reject) only valid (invalid) buy and sell orders.
* Handle queuing of accepted buy and sell orders as necessary.
* Order execution including price formation and, if necessary, quantity determination.
* Processing and settlement of executed orders once those orders have been filled.
* Record keeping of orders received, orders executed, transactions processed, etc.

**Problem:** too many requirements for a single market actor to satisfy. **Solution:** model the market actor as a collection of actors. Specifically, suppose that each `MarketLike` actor is composed of two additional actors: a `ClearingMechanismLike` actor that receives buy and sell orders and generates filled orders, and then a `SettlementMechanismLike` actor that processes the resulting filled orders and creates actual transactions.

![Hierarchical market actors](./marketlike-actor.jpg)

### `MarketLike` actor
The `MarketLike` actor should directly receive buy and sell orders for a particular `Tradable`, filter out any invalid orders, and then forward along all valid orders to a `ClearingMechanismLike` actor for further processing.

### `ClearingMechanismLike` actor
A ClearingMechanismLike actor should handle order execution (including price formation and quantity determination as well as any necessary queuing of buy and sell orders), generate filled orders, and send the filled orders to some SettlementMechanismLike actor for further processing. Note that each MarketLike actor should have a unique clearing mechanism.

#### Order execution
Order execution entails price formation and quantity determination. Market price formation requires clearing the market. It is important to be clear about the definition of the term "market clearing." Oxford Dictionary of Economics defines "market clearing" as follows:

1. The process of moving to a position where the quantity supplied is equal to the quantity demanded.
2. The assumption that economic forces always ensure the equality of supply and demand.

In most all mainstream macroeconomic models (i.e., RBC, DSGE, etc) it is assumed that economic forces instantaneously adjust to ensure the equality of supply and demand in all markets.

In our API, however, a key component of a `ClearingMechanismLike` actor is a `MatchingEngineLike` behavioral trait which explicitly defines a dynamic process by which orders are executed, prices are formed, and quantities are determined. Note that a `MatchingEngineLike` behavioral trait is similar to an auction mechanism in many respects. [Friedman (2007)](http://www.sciencedirect.com/science/article/pii/S0167268106002757) lists four major types of two-sided auction mechanisms commonly implemented in real world markets.

* Posted offer (PO): PO allows one side (say sellers) to commit to particular prices that are publicly posted and then allows the other side to choose quantities. PO is the dominant clearing mechanism used in the modern retail sector.

* Bilateral negotiation (BLN): BLN requires each buyer to search for a seller (and vice versa); the pair then tries to negotiate a price and (if unsuccessful) resumes search. BLN clearing mechanisms were prevalent in preindustrial retail trade, and continue to be widely used in modern business-to-business (B2B) contracting. Some retail Internet sites also use BLN clearing mechanisms.

* Continuous double auction (CDA): CDA allows traders to make offers to buy and to sell and allows traders to accept offers at any time during a trading period. Variants of CDA markets prevail in modern financial exchanges such as the New York Stock Exchange (NYSE), NASDAQ, and the Chicago Board of Trade and are featured options on many B2B Internet sites.

* Call auction (CA): The CA requires participants to make simultaneous offers to buy or sell, and the offers are cleared once each trading period at a uniform price.

Each of these auction mechanisms would correspond to a particular implementation of an `MatchingEngineLike` behavior.

TODO: similarly classify the various types of single-sided auction mechanisms commonly implemented
in real world markets.

#### Order queuing
Order queuing involves storing and possibly sorting received buy and sell orders according to some `OrderQueuingStrategy`. Different order queuing strategies will be distinguished from one another by...

1. type of collection used for storing buy and sell orders,
2. the sorting algorithm applied to the collections.

For example, some `OrderQueuingStrategy` behaviors might only require that unfilled buy and sell orders are stored in some collection (the sorting of buy and sell orders within their respective collections being irrelevant). Other `OrderQueuingStrategy` behaviors might have complicated `OrderBookLike` rules for sorting the stored buy and sell orders.

### Settlement mechanisms
Fundamental objective of a `SettlementMechanismLike` actor is to convert filled orders into settled transactions. Rough sketch of a process by which filled orders are converted into settled transaction is as follows.

1. Receive filled orders from some ClearingMechanismLike actor(s).
2. Send request for the desired quantity of the specified Tradable to the seller. 
3. Send request for some desired quantity of the specified means of payment (which will be some other Tradable) to the buyer.
4. Handle response from the seller (requires handling the case in which seller has insufficient quantity of the specified Tradable).
5. Handle response from the buyer (requires handling the case in which buyer has insufficient quantity of the specified means of payment).
6. Generate a settled transaction.

The following two types of settlement mechanisms should cover most all possible use cases.

* `BilateralSettlement`: with `BilateralSettlement`, buy and sell counterparties settle directly with one another.
* `CentralCounterpartySettlement`: With `CentralCounterparty` settlement, a central counterparty (CCP) actor inserts itself as a both a buy and sell counterparty to all filled orders that it receives from some clearing mechanism. After inserting itself as a counterparty, the CCP actor then settles the filled orders using bilateral settlement mechanism. Unlike clearing mechanisms, which are unique to a particular market, settlement mechanisms could be shared across markets.

### Use cases for `MarketLike` actors
In this section I sketch out some specific use cases for the Markets API.

#### Retail goods market
Retail goods markets are markets for final consumption goods (typically purchased by households). `RetailMarketLike` behavior would extend generic `MarketLike` behavior with:

* Some `ClearingMechanismLike` clearing mechanism using a `PostedOfferLike` matching engine,
* A `BilateralSettlement` settlement mechanism.

#### Wholesale goods market
Wholesale goods markets are markets for intermediate goods (typically purchased by firms and then used in the production of retail goods). WholesaleMarketLike behavior would extend MarketLike behavior with:

* Some `ClearingMechanismLike` clearing mechanism using a `BilateralNegotiationLike` matching engine,
* A `BilateralSettlement` settlement mechanism.

#### Labor market
Labor can be a very heterogenous commodity (which makes labor markets tricky). `LaborMarketLike` behavior would extend MarketLike behavior with:

* Some `ClearingMechanismLike` clearing mechanism using either a `BilateralNegotiationLike` or `PostedOfferLike` matching engine,
* A `BilateralSettlement` settlement mechanism.

#### Housing market
Note similarity of `HousingMarketLike` to `RetailMarketLike`. `HousingMarketLike` behavior would extend `MarketLike` behavior with:

* Some `ClearingMechanismLike` clearing mechanism using a `PostedOfferLike` matching engine,
* A `BilateralSettlement` settlement mechanism.

#### Securities market
`SecuritymarketLike` markets would include markets for stocks, bonds, currencies, etc. Could even create a `SecuritiesExchangeLike` actor which would route orders for various securities to the appropriate `SecuritiesMarketLike` actor. `SecuritiesMarketLike` behavior would extend `MarketLike` behavior with:

* Clearing mechanism with `ContinuousDoubleAuctionLike` matching engine and `OrderBookLike` order queuing strategy,
* `CentralCounterpartySettlement` settlement mechanism.

#### Unsecured interbank lending market
See Perry Mehrling for more details on unsecured interbank lending markets. `InterbankMarketLike` behavior would extend MarketLike behavior with:

* Some `ClearingMechanismLike` clearing mechanism using either a `BilateralNegotiationLike`,
* A `BilateralSettlement` settlement mechanism.

#### Secured interbank lending (repo) market
See Perry Mehrling for more details on secured interbank lending (repo) markets. `RepoMarketLike` behavior would extend MarketLike behavior with:

* Some `ClearingMechanismLike` clearing mechanism using either a `BilateralNegotiationLike`,
* A `BilateralSettlement` settlement mechanism.

