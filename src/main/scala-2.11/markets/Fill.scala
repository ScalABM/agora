/*
Copyright 2015 David R. Pugh, J. Doyne Farmer, and Dan F. Tang

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
package markets.clearing

import java.util.UUID

import markets.Message
import markets.orders.{BidOrder, AskOrder}


case class Fill(askOrder: AskOrder,
                bidOrder: BidOrder,
                price: Option[Long],
                residualAskOrder: Option[AskOrder],
                residualBidOrder: Option[BidOrder],
                timestamp: Long,
                uuid: UUID) extends Message


object Fill {

  def fromMatching(matching: Matching, timestamp: Long, uuid: UUID): Fill = {
    Fill(matching.askOrder, matching.bidOrder, matching.price, matching.residualAskOrder,
      matching.residualBidOrder, timestamp, uuid)
  }
}
