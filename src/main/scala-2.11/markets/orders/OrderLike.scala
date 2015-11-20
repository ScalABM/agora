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
package markets.orders

import akka.actor.ActorRef
import markets.ContractLike
import markets.tradables.Tradable


trait OrderLike extends ContractLike {

  def issuer: ActorRef

  def price: Long

  def quantity: Long

  def tradable: Tradable

  require(price >= 0, "Price must be non-negative.")

  require(quantity > 0, "Quantity must be strictly positive.")

}

