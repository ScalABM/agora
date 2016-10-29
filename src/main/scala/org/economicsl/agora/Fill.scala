/*
Copyright 2016 ScalABM

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
package org.economicsl.agora

import java.util.UUID

import org.economicsl.agora.tradables.Tradable


/** Used to store information associated with a transaction between a buyer and a seller.
  *
  * @param buyer the buyer's `UUID`.
  * @param seller the seller' `UUID`.
  * @param price the price at which the transaction between the `buyer` and `seller` will be settled.
  * @param quantity the quantity of the `Tradable` that will be exchanged during settlement.
  * @param tradable the `Tradable` that will be exchanged during settlement.
  * @note a `Fill` needs to contain all relevant information required to settle the transaction between the `buyer` and
  *       the `seller`.
  */
class Fill(val buyer: UUID, val seller: UUID, val price: Long, val quantity: Long, val tradable: Tradable)
