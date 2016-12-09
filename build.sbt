import org.scoverage.coveralls.Imports.CoverallsKeys._

lazy val commonSettings = Seq(
  scalaVersion := "2.11.8" ,
  organization := "com.github.EconomicSL",
  name := "agora",
  version := "0.1.0-alpha-SNAPSHOT",
  resolvers ++= Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
  ),
  coverallsTokenFile := Some(".coveralls.token"),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  publishMavenStyle := true,
  scalacOptions ++= Seq("-unchecked", "-deprecation")
)

lazy val Functional = config("functional") extend Test

lazy val Performance = config("performance") extend Test

lazy val core = (project in file(".")).
  settings(commonSettings: _*).
  configs(Functional).
  settings(inConfig(Functional)(Defaults.testSettings): _*).
  settings(
    libraryDependencies ++= Seq(
      "org.scalatest" % "scalatest_2.11" % "2.2.6" % "functional, test",
      "org.apache.commons" % "commons-math3" % "3.6.1" % "functional, test"
    ),
    parallelExecution in Functional := false
  ).
  configs(Performance).
  settings(inConfig(Performance)(Defaults.testSettings): _*).
  settings(
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    libraryDependencies += "com.storm-enroute" %% "scalameter" % "0.7" % "performance",
    parallelExecution in Performance := false
  )

