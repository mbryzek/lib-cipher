name := "lib-cipher"

version := "0.0.29"

ThisBuild / javacOptions ++= Seq("-source", "17", "-target", "17")

// The published groupId. Do not remove: nothing else sets it, and without it the
// artifact publishes under a default groupId that no consumer resolves (lib-util 0.0.34).
ThisBuild / organization := "com.bryzek"
ThisBuild / homepage := Some(url("https://github.com/mbryzek/lib-cipher"))
ThisBuild / licenses := Seq("MIT" -> url("https://github.com/mbryzek/lib-cipher/blob/main/LICENSE"))
ThisBuild / developers := List(
  Developer("mbryzek", "Michael Bryzek", "mbryzek@alum.mit.edu", url("https://github.com/mbryzek"))
)
ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/mbryzek/lib-cipher"), "scm:git@github.com:mbryzek/lib-cipher.git")
)

ThisBuild / publishTo := sonatypePublishToBundle.value
ThisBuild / sonatypeCredentialHost := "central.sonatype.com"
ThisBuild / sonatypeRepository := "https://central.sonatype.com/api/v1/publisher"
ThisBuild / publishMavenStyle := true

ThisBuild / scalaVersion := "3.8.4"

lazy val allScalacOptions = Seq(
  "-feature",
  "-Werror",
  "-Wunused:locals",
  "-Wunused:params",
  "-Wimplausible-patterns",
  "-Wunused:linted",
  "-Wunused:imports",
  "-Wunused:privates",
)

lazy val root = project
  .in(file("."))
  .settings(
    resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
    scalafmtOnCompile := true,
    Compile / packageDoc / mappings := Seq(),
    Compile / packageDoc / publishArtifact := true,
    testOptions += Tests.Argument("-oDF"),
    scalacOptions ++= allScalacOptions,
    libraryDependencies ++= Seq(
      "com.bryzek" %% "lib-util" % "0.0.39",
      "com.password4j" % "password4j" % "1.8.4",
      "org.springframework.security" % "spring-security-crypto" % "7.1.0",
      "org.mindrot" % "jbcrypt" % "0.4",
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.2" % Test,
    ),
  )

