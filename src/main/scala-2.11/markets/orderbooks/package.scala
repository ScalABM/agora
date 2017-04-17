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


/** A package for representing collections of `OrderLike` objects.
  *
  * Basic orderbook should be unordered. Some markets might not have any need for maintaining an ordering. However
  * many orderbooks will need to maintain an ordering therefore need to support a sorted orderbook.
  *
  * Need to have different orderbooks for buy and sell orders. Buy and sell orders have different orderings.
  */
package object orderbooks
