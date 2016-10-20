import org.scoverage.coveralls.Imports.CoverallsKeys._

lazy val Performance = config("performance") extend Test

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

lazy val core = (project in file(".")).
  configs(IntegrationTest).
  settings(commonSettings: _*).
  settings(Defaults.itSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "org.scalatest" % "scalatest_2.11" % "2.2.6" % "it, test",
      "org.apache.commons" % "commons-math3" % "3.6.1" % "it, test"
    )
  ).
  configs(Performance).
  settings(inConfig(Performance)(Defaults.testSettings): _*).
  settings(
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    parallelExecution in Performance := false,
    libraryDependencies += "com.storm-enroute" %% "scalameter" % "0.7" % "performance"
  )

