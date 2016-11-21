import org.scoverage.coveralls.Imports.CoverallsKeys._

lazy val commonSettings = Seq(
  scalaVersion := "2.12.0" ,
  name := "agora",
  version := "0.1.0-alpha-SNAPSHOT",
  organization := "org.economicsl",
  organizationName := "EconomicSL",
  organizationHomepage := Some(url("https://economicsl.github.io/")),
  coverallsTokenFile := Some(".coveralls.token"),
  resolvers ++= Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
  ),
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
      "org.scalactic" %% "scalactic" % "3.0.1" % "functional, test",
      "org.scalatest" %% "scalatest" % "3.0.1" % "functional, test",
      "org.apache.commons" % "commons-math3" % "3.6.1" % "functional, test"
    ),
    parallelExecution in Functional := false
  ).
  configs(Performance).
  settings(inConfig(Performance)(Defaults.testSettings): _*).
  settings(
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    libraryDependencies += "com.storm-enroute" %% "scalameter" % "0.8.2" % "performance",
    parallelExecution in Performance := false
  )

