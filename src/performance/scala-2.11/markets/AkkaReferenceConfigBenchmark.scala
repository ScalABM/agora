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

import org.scalameter.api._


/** Scaling experiment testing how performance varies with the number of order issuers. */
object AkkaReferenceConfigBenchmark extends Bench.OnlineRegressionReport {

  override def measurer = new Measurer.IgnoringGC with Measurer.PeriodicReinstantiation[Double]

  val inputData: Gen[Int] = Gen.exponential("Number of order issuers")(1, 1024, 2)

  performance of "AkkaReferenceConfigBenchmarkSimulation" in {
    measure method "main" config (
      exec.minWarmupRuns -> 1,
      exec.maxWarmupRuns -> 5,
      exec.benchRuns -> 200,  // number of benchruns
      exec.independentSamples -> 10,  // number of JVM intances (default is 9)
      exec.reinstantiation.frequency -> 1,  // reallocates the initial data (default is 12)
      exec.reinstantiation.fullGC -> true,
      exec.jvmflags -> List("-Xmx2G", "-XX:+PrintCommandLineFlags")
      ) in {
      using(inputData) in { numberOrderIssuers =>
        val args = Array("4", numberOrderIssuers.toString, "100000", "0.5")
        AkkaReferenceConfigBenchmarkSimulation.main(args)
      }
    }
  }

}
