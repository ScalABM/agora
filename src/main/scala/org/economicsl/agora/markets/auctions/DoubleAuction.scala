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
package org.economicsl.agora.markets.auctions

import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.{MultiUnit, Quantity, SingleUnit}
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder


sealed trait DoubleAuction[A <: LimitAskOrder with Quantity, B <: LimitBidOrder with Quantity] {

  def place(order: A with Persistent): Unit = reverseAuction.place(order)

  def place(order: B with Persistent): Unit = auction.place(order)

  protected def auction: Auction[A, B with Persistent]

  protected def reverseAuction: ReverseAuction[B, A with Persistent]

}


trait SingleUnitDoubleAuction[A <: LimitAskOrder with SingleUnit, B <: LimitBidOrder with SingleUnit]
  extends DoubleAuction[A, B] {

  protected def auction: SingleUnitAuction[A, B with Persistent]

  protected def reverseAuction: SingleUnitReverseAuction[B, A with Persistent]

}



trait MultiUnitDoubleAuction[A <: LimitAskOrder with MultiUnit, B <: LimitBidOrder with MultiUnit]
  extends DoubleAuction[A, B] {

  protected def auction: MultiUnitAuction[A, B with Persistent]

  protected def reverseAuction: MultiUnitReverseAuction[B, A with Persistent]

}
