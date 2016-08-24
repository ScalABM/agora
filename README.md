[![Travis Status](https://travis-ci.org/ScalABM/markets-sandbox.svg?branch=develop)](https://travis-ci.org/ScalABM/markets-sandbox)
[![Appveyor status](https://ci.appveyor.com/api/projects/status/w1g7a4mgighvhnwu/branch/develop?svg=true)](https://ci.appveyor.com/project/davidrpugh/markets-sandbox)
[![Coveralls Status](https://coveralls.io/repos/ScalABM/markets-sandbox/badge.svg?branch=develop&service=github)](https://coveralls.io/github/ScalABM/markets-sandbox?branch=develop)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/bb51f04bbe894b98b36603f49c310e8a)](https://www.codacy.com/app/davidrpugh/markets-sandbox)

# markets-sandbox

A sandbox for building and testing scalable implementations of various market micro-structures.

# Installing

The ScalABM markets-sandbox is being developed using [Java 8](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) and [Scala 2.11](http://www.scala-lang.org/) and built using the [Scala Build Tool (SBT)](http://www.scala-sbt.org/). If you want to build the ScalABM markets-sandbox from source you will also need to install [Git](https://git-scm.com/).

## Java 8

To see what version (if any!) of Java you already have installed on your system, open a terminal (or command prompt on Windows) and run...

`java -version`

...if the result is something like...

`java version "1.8.0_$BUILD_NUMBER`

...then you are good to go and can proceed to installing Scala and SBT. Note that the `$BUILD_NUMBER` will depend on the exact build of Java 8 you have installed.  If you are running older versions of the JDK (or a JDK is not installed on your machine), then you can down install the Java 8 JDK from either [Oracle](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) or the [OpenJDK 8 project](http://openjdk.java.net/projects/jdk8/).

### Oracle JDK 8
Pre-packaged installers for Oracle's JDK 8 are [available](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) for all major operating systems. If you are new to Java development (and/or are *not* using a Linux-based OS!) then I would suggest that you use one of Oracle's pre-packaged installers.

### OpenJDK 8
The OpenJDK 8 project is an open-source reference implementation of the Oracle Java SE 8 Platform Specification. Installing the OpenJDK on Linux systems is a [piece of cake](http://openjdk.java.net/install/).  For example, Debian or Ubuntu users just need to open a terminal and run...

`sudo apt-get install openjdk-8-jdk`

...installing OpenJDK on Mac OSX can be done but requires a bit more work.  While I am sure it is possible to install the OpenJDK on Windows, I don't have any idea how to go about doing it!

### Testing your Java install
To verify your Java install, open a terminal (or command prompt on Windows) and run...

`java -version`

...and the result should be something like...

`java version "1.8.0_$BUILD_NUMBER`

...where the `$BUILD_NUMBER` will depend on the exact build of Java 8 you have installed

## Scala and SBT
Once Java 8 is installed, need to install Scala and SBT. Installers exist for all major operating systems for both [Scala](http://www.scala-lang.org/download/) and [SBT](http://www.scala-sbt.org/download.html).

An alternative solution is to install [Activator](https://www.lightbend.com/activator/download) from LightBend which includes both Scala and SBT (as well as the [Play Framework](https://www.playframework.com/)).

### Testing your Scala and SBT install
To verify your Scala install, open a terminal (or command prompt on Windows) and run...

`scala -version`

...and the result should be something like...

`Scala code runner version 2.11.8 -- Copyright 2002-2016, LAMP/EPFL`

To verify your SBT install, open a terminal (or command prompt on Windows) and run...

`sbt sbtVersion`

...and the result should be something like...

```
[info] Loading global plugins from C:\Users\pughdr\.sbt\0.13\plugins
[info] Loading project definition from C:\Users\pughdr\Research\scalabm\markets-sandbox\project
[info] Set current project to markets-sandbox (in build file:/C:/Users/pughdr/Research/scalabm/markets-sandbox/)
[info] 0.13.11
```

## Git
In order to install the ScalABM markets-sandbox source code, you will need to install [Git](https://git-scm.com/downloads).

### Testing your Git install
To verify your Git install, open a terminal (or command prompt on Windows) and run...

`git --version`

...and the result should be something like...

`git version 2.9.2`

## ScalABM markets-sandbox source
To install the ScalABM markets-sandbox source code you can either [clone the repo](https://help.github.com/articles/cloning-a-repository/) or, assuming you already have a GitHub account, you can first [fork the repo](https://help.github.com/articles/fork-a-repo/) and then clone it.

# Building the ScalABM markets-sandbox
To build the ScalABM markets-sandbox simply open a terminal (or command prompt on Windows), change into the `markets-sandbox` directory and run...

`sbt clean compile`

...which should generate output similar to...

```
[info] Loading global plugins from C:\Users\pughdr\.sbt\0.13\plugins
[info] Loading project definition from C:\Users\pughdr\Research\scalabm\markets-sandbox\project
[info] Set current project to markets-sandbox (in build file:/C:/Users/pughdr/Research/scalabm/markets-sandbox/)
[success] Total time: 0 s, completed Aug 5, 2016 11:47:25 AM
[info] Updating {file:/C:/Users/pughdr/Research/scalabm/markets-sandbox/}markets-sandbox-core...
[info] Resolving org.scala-lang#scalap;2.11.8 ...
[info] Done updating.
[info] Compiling 24 Scala sources to C:\Users\pughdr\Research\scalabm\markets-sandbox\target\scala-2.11\classes...
[success] Total time: 12 s, completed Aug 5, 2016 11:47:37 AM
```

# Running unit tests
The ScalABM markets-sandbox has an extensive suite of unit tests.  To run the ScalABM markets-sandbox unit test suite simply open a terminal (or command prompt on Windows), change into the `markets-sandbox` directory and run...

`sbt clean test`

..to run unit tests and generate unit test coverage statistics run ...

`sbt clean coverage test`

...followed by...

`sbt coverageReport`

...to generate the report.  The html and xml versions of the code coverage reports can be found in the `markets-sandbox/target/` directory. Code coverage reports for both the master and develop branches of the ScalABM markets-sandbox repo are automatically generated by Travis as part of our continuous integration (CI) process and are available online ([master](), [develop]()).

# Running performance tests
In addition to unit tests, the ScalABM markets-sandbox has an extensive suite of performance tests. Performance testing JVM applications is inherently difficult. To gain some appreciation of the difficulties involved, check out the [Scala Meter](https://scalameter.github.io/) [documentation](http://scalameter.github.io/home/gettingstarted/0.7/).

To run the entire suite of ScalABM markets-sandbox performance tests simply open a terminal (or command prompt on Windows), change into the `markets-sandbox` directory and run...

`sbt performance:test`

...note that running the entire suite of performance tests can take several hours. If you are interested in the results of a particular performance test, the you can run...

`sbt "performance:test-only $CLASSPATH"`

...where the `$CLASSPATH` should be a path to the class containing the performance test that you would like to run.  For example, to run the performance test for the Continuous Double Auction matching engine, you would run...

`sbt "performance:test-only markets.engines.CDAMatchingEngineMicroBenchmark"`

...**don't forget to include the quotation marks!** The html version of the performance testing reports can be found in the `markets-sandbox/target/` directory. Performance testing reports for the master branch of the ScalABM markets-sandbox repo are automatically generated by Travis as part of our continuous integration (CI) process and are available online.
