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
package markets

import com.typesafe.config.ConfigFactory
import org.scalameter.api._


/** Performance benchmark for a MarketActor. */
object MarketActorBenchmark extends Bench.OnlineRegressionReport {

  val config = ConfigFactory.load("marketActorBenchmark.conf")

  /* Input data should a sequence of integers from 1 to 2 * number of available cores. */
  val inputData: Gen[Int] = Gen.range("Number of logical cores")(1, 12, 1)

  performance of "MarketActorBenchmarkSimulation" in {
    measure method "main" config (
      exec.jvmflags -> List("-Xms2G", "-Xmx3G", "-XX:+PrintCommandLineFlags")
      ) in {
      using(inputData) in {
        maxParallelism =>
          MarketActorBenchmarkSimulation.main(Array(maxParallelism.toString))
      }
    }
  }

}
