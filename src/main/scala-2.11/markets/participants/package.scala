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
package markets


package object participants {

  private[participants] trait Reminder

  private[paricipants] trait SubmitOrder extends Reminder

  private[participants] trait SubmitLimitOrder extends SubmitOrder

  private[participants] trait SubmitMarketOrder extends SubmitOrder

  private[participants] object SubmitOrderCancellation extends Reminder

  private[participants] object SubmitLimitAskOrder extends SubmitLimitOrder

  private[participants] object SubmitLimitBidOrder extends SubmitLimitOrder

  private[participants] object SubmitMarketAskOrder extends SubmitMarketOrder

  private[participants] object SubmitMarketBidOrder extends SubmitMarketOrder

}
