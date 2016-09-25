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
package markets.tradables

import java.util.UUID


/** Class for representing Tradable objects.
  *
  * @param uuid a unique identifier that distinguishes a `Security` from other types of `Tradable` objects.
  * @param tick the minimum increment in which the price of this `Security` can change.
  */
case class Security(uuid: UUID, tick: Long = 1) extends Tradable
