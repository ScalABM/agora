import org.scoverage.coveralls.Imports.CoverallsKeys._

lazy val Performance = config("performance") extend Test

lazy val commonSettings = Seq(
  scalaVersion := "2.11.8" ,
  organization := "com.github.ScalABM",
  libraryDependencies ++= Seq(
    "org.scalatest" % "scalatest_2.11" % "2.2.6" % "test",
    "com.storm-enroute" %% "scalameter" % "0.7" % "test"
  ),
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

lazy val core = Project(
  id = "markets-sandbox-core",
  base = file("."),
  settings = Defaults.coreDefaultSettings ++ commonSettings ++ Seq(
    name := "markets-sandbox",
    version := "0.1.0-alpha-SNAPSHOT",
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    parallelExecution in Performance := false
  )
) configs Performance settings(inConfig(Performance)(Defaults.testSettings): _*)
