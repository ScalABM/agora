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
package markets.orders.orderings.ask

import markets.orders.AskOrder
import markets.orders.orderings.TimePriority


class AskTimeOrdering extends AskOrdering with TimePriority {

  def compare(order1: AskOrder, order2: AskOrder): Int = {
    if (hasTimePriority(order1, order2)) {
      -1
    } else if (order1.timestamp == order2.timestamp) {
      0
    } else {
      1
    }
  }

}