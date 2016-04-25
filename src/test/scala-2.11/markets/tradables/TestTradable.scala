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
package markets.tradables


/** Class representing a stub implementation of the Tradable trait for testing.
  *
  * @param symbol
  * @param tick
  */
case class TestTradable(symbol: String, tick: Long) extends Tradable


object TestTradable {

  /** Auxiliary constructor for TestTradable.
    *
    * @param symbol
    * @return a TestTradable object whose tick size is 1.
    */
  def apply(symbol: String): TestTradable = {
    new TestTradable(symbol, 1)
  }

}

