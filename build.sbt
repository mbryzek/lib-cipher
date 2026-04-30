name := "lib-cipher"

organization := "com.bryzek"

version := "0.0.24"

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

ThisBuild / scalaVersion := "3.8.3"

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
      "com.bryzek" %% "lib-util" % "0.0.31",
      "com.password4j" % "password4j" % "1.8.4",
      "org.springframework.security" % "spring-security-crypto" % "7.0.5",
      "org.mindrot" % "jbcrypt" % "0.4",
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
    ),
  )

