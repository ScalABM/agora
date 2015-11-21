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
package markets.settlement

import akka.actor.Actor

import markets.orders.filled.FilledOrderLike


class BilateralSettlementMechanism extends Actor with SettlementMechanismLike {

  def receive: Receive = {
    case fill: FilledOrderLike =>
      context.actorOf(ContractHandler.props(fill))
  }

}