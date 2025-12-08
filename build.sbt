name := "lib-cipher"

organization := "com.mbryzek"

version := "0.0.15"
scalaVersion := "3.7.4"

ThisBuild / scalaVersion := scalaVersion.value

ThisBuild / javacOptions ++= Seq("-source", "17", "-target", "17")

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

// Cross-build for multiple Scala versions
ThisBuild / crossScalaVersions := Seq("2.13.18", scalaVersion.value)

lazy val allScalacOptions = Seq(
  "-feature",
  "-Xfatal-warnings",
  "-Wunused:locals",
  "-Wunused:params",
  "-Wimplausible-patterns",
  "-Wunused:linted",
  "-Wunused:unsafe-warn-patvars",
  "-Wunused:imports",
  "-Wunused:privates",
)

lazy val root = project
  .in(file("."))
  .settings(
    resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
    scalafmtOnCompile := true,
    Compile / doc / sources := Seq.empty,
    Compile / packageDoc / publishArtifact := false,
    testOptions += Tests.Argument("-oDF"),
    scalacOptions ++= allScalacOptions,
    libraryDependencies ++= Seq(
      "com.password4j" % "password4j" % "1.8.4",
      "org.springframework.security" % "spring-security-crypto" % "7.0.0",
      "org.mindrot" % "jbcrypt" % "0.4",
      "commons-codec" % "commons-codec" % "1.20.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
    ),
  )

