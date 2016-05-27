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


/** Performance benchmark comparing the various default dispatchers...*/
object AkkaDefaultDispatcherBenchmark extends Bench.OnlineRegressionReport {

  override def measurer = new Measurer.IgnoringGC with Measurer.PeriodicReinstantiation[Double]

  // 65536
  val inputData: Gen[Int] = Gen.exponential("Number of order issuers")(1, 4096, 2)

  /* Global configuration flags common to all JVM instances. */
  val jvmGlobalConfig = List("-Xmx2G", "-XX:+PrintCommandLineFlags")

  performance of "fork-join-executor" in {

    measure method "main" config (
      exec.minWarmupRuns -> 1,
      exec.maxWarmupRuns -> 5,
      exec.benchRuns -> 200,  // number of benchruns
      exec.independentSamples -> 10,  // number of JVM intances (default is 9)
      exec.reinstantiation.frequency -> 1,  // reallocates the initial data (default is 12)
      exec.reinstantiation.fullGC -> true,
      exec.jvmflags -> (jvmGlobalConfig ::: forkJoinExecutorConfig())
      ) in {
      using(inputData) in { numberOrderIssuers =>
        val args = Array(numberOrderIssuers.toString)
        AkkaDefaultDispatcherBenchmarkSimulation.main(args)
      }
    }

    measure method "main" config (
      exec.minWarmupRuns -> 1,
      exec.maxWarmupRuns -> 5,
      exec.benchRuns -> 200,  // number of benchruns
      exec.independentSamples -> 10,  // number of JVM intances (default is 9)
      exec.reinstantiation.frequency -> 1,  // reallocates the initial data (default is 12)
      exec.reinstantiation.fullGC -> true,
      exec.jvmflags -> (jvmGlobalConfig ::: forkJoinExecutorConfig(parallelismMin = 1, parallelismMax = 1))
      ) in {
      using(inputData) in { numberOrderIssuers =>
        val args = Array(numberOrderIssuers.toString)
        AkkaDefaultDispatcherBenchmarkSimulation.main(args)
      }
    }

    measure method "main" config (
      exec.minWarmupRuns -> 1,
      exec.maxWarmupRuns -> 5,
      exec.benchRuns -> 200,  // number of benchruns
      exec.independentSamples -> 10,  // number of JVM intances (default is 9)
      exec.reinstantiation.frequency -> 1,  // reallocates the initial data (default is 12)
      exec.reinstantiation.fullGC -> true,
      exec.jvmflags -> (jvmGlobalConfig ::: forkJoinExecutorConfig(parallelismMin = 10, parallelismMax = 10))
      ) in {
      using(inputData) in { numberOrderIssuers =>
        val args = Array(numberOrderIssuers.toString)
        AkkaDefaultDispatcherBenchmarkSimulation.main(args)
      }
    }

    measure method "main" config (
      exec.minWarmupRuns -> 1,
      exec.maxWarmupRuns -> 5,
      exec.benchRuns -> 200,  // number of benchruns
      exec.independentSamples -> 10,  // number of JVM intances (default is 9)
      exec.reinstantiation.frequency -> 1,  // reallocates the initial data (default is 12)
      exec.reinstantiation.fullGC -> true,
      exec.jvmflags -> (jvmGlobalConfig ::: forkJoinExecutorConfig(parallelismMin = 100, parallelismMax = 100))
      ) in {
      using(inputData) in { numberOrderIssuers =>
        val args = Array(numberOrderIssuers.toString)
        AkkaDefaultDispatcherBenchmarkSimulation.main(args)
      }
    }

    performance of "thread-pool-executor" in {

      measure method "main" config(
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 5,
        exec.benchRuns -> 200, // number of benchruns
        exec.independentSamples -> 10, // number of JVM intances (default is 9)
        exec.reinstantiation.frequency -> 1, // reallocates the initial data (default is 12)
        exec.reinstantiation.fullGC -> true,
        exec.jvmflags -> (jvmGlobalConfig ::: threadPoolExecutorConfig())
        ) in {
        using(inputData) in { numberOrderIssuers =>
          val args = Array(numberOrderIssuers.toString)
          AkkaDefaultDispatcherBenchmarkSimulation.main(args)
        }
      }

      measure method "main" config(
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 5,
        exec.benchRuns -> 200, // number of benchruns
        exec.independentSamples -> 10, // number of JVM intances (default is 9)
        exec.reinstantiation.frequency -> 1, // reallocates the initial data (default is 12)
        exec.reinstantiation.fullGC -> true,
        exec.jvmflags -> (jvmGlobalConfig ::: threadPoolExecutorConfig(fixedPoolSize = Some(1)))
        ) in {
        using(inputData) in { numberOrderIssuers =>
          val args = Array(numberOrderIssuers.toString)
          AkkaDefaultDispatcherBenchmarkSimulation.main(args)
        }
      }

      measure method "main" config(
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 5,
        exec.benchRuns -> 200, // number of benchruns
        exec.independentSamples -> 10, // number of JVM intances (default is 9)
        exec.reinstantiation.frequency -> 1, // reallocates the initial data (default is 12)
        exec.reinstantiation.fullGC -> true,
        exec.jvmflags -> (jvmGlobalConfig ::: threadPoolExecutorConfig(fixedPoolSize = Some(10)))
        ) in {
        using(inputData) in { numberOrderIssuers =>
          val args = Array(numberOrderIssuers.toString)
          AkkaDefaultDispatcherBenchmarkSimulation.main(args)
        }
      }

      measure method "main" config(
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 5,
        exec.benchRuns -> 200, // number of benchruns
        exec.independentSamples -> 10, // number of JVM intances (default is 9)
        exec.reinstantiation.frequency -> 1, // reallocates the initial data (default is 12)
        exec.reinstantiation.fullGC -> true,
        exec.jvmflags -> (jvmGlobalConfig ::: threadPoolExecutorConfig(fixedPoolSize = Some(100)))
        ) in {
        using(inputData) in { numberOrderIssuers =>
          val args = Array(numberOrderIssuers.toString)
          AkkaDefaultDispatcherBenchmarkSimulation.main(args)
        }
      }
    }

  }

  def forkJoinExecutorConfig(parallelismMin: Int = 8,
                             parallelismFactor:Double = 3.0,
                             parallelismMax: Int = 64,
                             taskPeekingMode: String = "FIFO"):List[String] = {
    val path = s"-Dakka.actor.default-dispatcher."
    List(
      path + s"executor=fork-join-executor",
      path + s"fork-join-executor.parallelism-min=$parallelismMin",
      path + s"fork-join-executor.parallelism-factor=$parallelismFactor",
      path + s"fork-join-executor.parallelism-max=$parallelismMax",
      path + s"fork-join-executor.task-peeking-mode=$taskPeekingMode"
    )
  }

  def threadPoolExecutorConfig(fixedPoolSize: Option[Int] = None): List[String] = {
    val path = s"-Dakka.actor.default-dispatcher."
    val baseConfig = List(path + s"executor=thread-pool-executor")

    fixedPoolSize match {
      case Some(poolSize) =>
        baseConfig ::: List(path + s"thread-pool-executor.fixed-pool-size=$poolSize")
      case None =>
        baseConfig ::: List(path + s"thread-pool-executor.fixed-pool-size=off")
    }
  }

}
