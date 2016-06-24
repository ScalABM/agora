/*
Copyright 2016 David R. Pugh

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package markets


/** Classes for modeling auction mechanisms.
  *
  * In our API, however, a key component of a `ClearingMechanismActor` is a `MatchingEngineLike`
  * module. A `MatchingEngineLike` module handles any necessary queuing of buy and sell orders,
  * order execution (including price formation and quantity determination), and generates filled
  * orders. Note that a `MatchingEngineLike` module is similar to an auction mechanism in many
  * respects. Four major types of two-sided auction mechanisms commonly implemented in real world
  * markets.
  *
  * - Posted offer (PO): PO allows one side (say sellers) to commit to particular prices that are
  * publicly posted and then allows the other side to choose quantities. PO is the dominant
  * clearing mechanism used in the modern retail sector.
  *
  * - Bilateral negotiation (BLN): BLN requires each buyer to search for a seller (and vice versa);
  * the pair then tries to negotiate a price and (if unsuccessful) resumes search. BLN clearing
  * mechanisms were prevalent in pre-industrial retail trade, and continue to be widely used in
  * modern business-to-business (B2B) contracting. Some retail Internet sites also use BLN
  * clearing mechanisms.
  *
  * - Continuous double auction (CDA): CDA allows traders to make offers to buy and to sell and
  * allows traders to accept offers at any time during a trading period. Variants of CDA markets
  * prevail in modern financial markets.exchanges such as the New York Stock Exchange (NYSE), 
  * NASDAQ, and the Chicago Board of Trade and are featured options on many B2B Internet sites.
  *
  * - Call auction (CA): The CA requires markets.participants to make simultaneous offers to buy
  * or sell, and the offers are cleared once each trading period at a uniform price.
  *
  */
package object auctions
