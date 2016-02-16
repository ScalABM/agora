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

// Generate required project metadata in order to publish to Maven.
publishMavenStyle := true

// Set up the repositories to which we will publish our artifacts.
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

// Don't publish test artifacts
publishArtifact in Test := false

// POM metadata for Maven.
pomExtra :=
  <url>https://github.com/ScalABM/markets-sandbox</url>
    <licenses>
      <license>
        <name>The Apache Software License, Version 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:https:ScalABM/markets-sandbox.git</url>
      <connection>scm:git:git@github.com:ScalABM/markets-sandbox.git</connection>
    </scm>
    <developers>
      <developer>
        <id>davidrpugh</id>
        <name>David R. Pugh</name>
        <url>http://davidrpugh.github.io/</url>
      </developer>
    </developers>
