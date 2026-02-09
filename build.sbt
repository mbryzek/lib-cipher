name := "lib-cipher"

organization := "com.bryzek"

version := "0.0.18"

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
val scala2Version = "2.13.18"
val scala3Version = "3.8.1"

ThisBuild / scalaVersion := scala3Version
ThisBuild / crossScalaVersions := Seq(scala2Version, scala3Version)

lazy val scala2Options = Seq(
  "-feature",
  "-Xfatal-warnings",
  "-Wunused:locals",
  "-Wunused:params",
  "-Wunused:imports",
  "-Wunused:privates",
  "-deprecation",
)

lazy val scala3Options = Seq(
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
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, _)) => scala2Options
        case _ => scala3Options
      }
    },
    libraryDependencies ++= Seq(
      "com.password4j" % "password4j" % "1.8.4",
      "org.springframework.security" % "spring-security-crypto" % "7.0.0",
      "org.mindrot" % "jbcrypt" % "0.4",
      "commons-codec" % "commons-codec" % "1.21.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
    ),
  )

