name := "lib-cipher"

organization := "com.mbryzek"

ThisBuild / scalaVersion := "3.7.4"

ThisBuild / javacOptions ++= Seq("-source", "17", "-target", "17")

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
      "org.springframework.security" % "spring-security-crypto" % "6.5.6",
      "org.mindrot" % "jbcrypt" % "0.4",
      "commons-codec" % "commons-codec" % "1.19.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
    ),
  )

