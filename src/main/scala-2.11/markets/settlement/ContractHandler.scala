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

import akka.actor.{Actor, PoisonPill, Props}

import markets.orders.filled.FilledOrderLike

import scala.util.{Failure, Success, Try}


class ContractHandler(filledOrder: FilledOrderLike) extends Actor {

  /* Primary constructor */
  val seller = filledOrder.counterParties._1
  val buyer = filledOrder.counterParties._2

  // This is really a request for seller to fulfill contractual requirements
  seller ! AssetsRequest(filledOrder.tradable, filledOrder.quantity)

  // This is really a request for the buyer to fulfill contractual requirements
  buyer ! PaymentRequest(filledOrder.price * filledOrder.quantity)

  /** Behavior of a TransactionHandler after receiving the seller's response.
    *
    * @param sellerResponse
    * @return a partial function that handles the buyer's response.
    */
  def awaitingBuyerResponse(sellerResponse: Try[Assets]): Receive = sellerResponse match {
    case Success(assets) => {
      // partial function for handling buyer response given successful seller response
      case Success(payment) =>
        buyer ! assets
        seller ! payment
        self ! PoisonPill
      case Failure(ex) =>
        seller ! assets // refund assets to seller
        self ! PoisonPill
    }
    case Failure(exception) => {
      // partial function for handling buyer response given failed seller response
      case Success(payment) => // refund payment to buyer
        buyer ! payment
        self ! PoisonPill
      case Failure(otherException) => // nothing to refund
        self ! PoisonPill
    }
  }

  /** Behavior of a TransactionHandler after receiving the buyer's response.
    *
    * @param buyerResponse
    * @return partial function that handles the seller's response.
    */
  def awaitingSellerResponse(buyerResponse: Try[Payment]): Receive = buyerResponse match {
    case Success(payment) => {
      // partial function for handling seller response given successful buyer response
      case Success(assets) =>
        buyer ! assets
        seller ! payment
        self ! PoisonPill
      case Failure(exception) =>
        buyer ! payment // refund payment to buyer
        self ! PoisonPill
    }
    case Failure(exception) => {
      // partial function for handling seller response given failed buyer response
      case Success(assets) => // refund assets to seller
        seller ! assets
        self ! PoisonPill
      case Failure(otherException) => // nothing to refund
        self ! PoisonPill
    }
  }

  /** Behavior of a TransactionHandler.
    *
    * @return partial function that handles buyer and seller responses.
    */
  def receive: Receive = {

    case success @ Success(Payment(amount)) =>
      context.become(awaitingSellerResponse(success))
    case failure @ Failure(InsufficientFundsException(msg)) =>
      context.become(awaitingSellerResponse(failure))
    case success @ Success(Assets(tradable, quantity)) =>
      context.become(awaitingBuyerResponse(success))
    case failure @ Failure(InsufficientAssetsException(msg)) =>
      context.become(awaitingBuyerResponse(failure))

  }

}


object ContractHandler {

  def props(filledOrder: FilledOrderLike): Props = {
    Props(new ContractHandler(filledOrder))
  }

}
