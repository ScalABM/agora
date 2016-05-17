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
package markets.actors.settlement

import markets.Fill
import markets.actors.Filled


/** Stub implementation of the `SettlementMechanismActor` trait for testing purposes.
  *
  * @note A `TestSettlementMechanismActor` simply receives `Fill` messages and then notifies both
  *       the ask order issuer and the bid order issuer that their respective orders have been
  *       filled using a `Filled` message.
  */
class TestSettlementMechanismActor extends SettlementMechanismActor {

  override def receive: Receive = {
    case Fill(ask, bid, _, _, residualAsk, residualBid, _, _) =>
      ask.issuer tell(Filled(ask, residualAsk), self)
      bid.issuer tell(Filled(bid, residualBid), self)
    case message =>
      super.receive(message)
  }

}
