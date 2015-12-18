name := "markets-sandbox"

version := "0.1.0-alpha-SNAPSHOT"

organization := "com.github.ScalABM"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.1",
  "com.typesafe.akka" %% "akka-agent" % "2.4.1",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.1",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
)