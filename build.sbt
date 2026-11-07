name := "lib-cipher"

version := "0.0.76"

// Publishing metadata, the guava pin, the browser-stack exclusions, the compiler gate and the
// root project's test reporting are shared by every lib-* repository and DECLARED in lib-build's
// BryzekLibSettings, which project/plugins.sbt brings in through lib-build-sbt. That file carries
// why each setting is what it is; a change is released there and taken here by a version bump.
BryzekLibSettings.buildSettings("lib-cipher")

ThisBuild / scalaVersion := "3.10.0"

// Every com.bryzek library version from the move off Maven Central on is published to the Maven
// repository at maven.bryzek.com (DES-35972); the versions published before it keep resolving from
// Maven Central, which sbt lists by default.
ThisBuild / resolvers += "Bryzek Maven repository" at "https://maven.bryzek.com"

// The Jackson 2, Jackson 3 and logback security pins are DECLARED in lib-build's BryzekPins
// (lib-build-sbt, through project/plugins.sbt), and asserted by the pin specs under
// src/test/scala/com/bryzek/pins, each a subclass of a lib-build-testkit suite. That file carries
// why each floor is what it is; an advisory bump is released there and taken here by a version bump.
//
// Applied as `dependencyOverrides`, which governs THIS build's resolution only -- sbt writes none
// of it into the published POM -- so it decides what this repo compiles and tests against and
// imposes no floor on a consumer. Jackson 3 is on this classpath through
// lib-util -> logstash-logback-encoder 9; logback arrives only through `scalatestplus-play % Test`.
ThisBuild / dependencyOverrides ++= BryzekPins.jackson2 ++ BryzekPins.jackson3 ++ BryzekPins.logback

lazy val root = project
  .in(file("."))
  .settings(BryzekLibSettings.rootSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.bryzek" %% "lib-util" % "0.0.80",
      "com.password4j" % "password4j" % "1.8.4",
      "org.springframework.security" % "spring-security-crypto" % "7.1.2",
      "org.mindrot" % "jbcrypt" % "0.4",
      // The pin suites the specs under src/test/scala/com/bryzek/pins subclass. Every dependency of
      // the testkit is Provided, so each suite asserts this build's own resolution.
      "com.bryzek" %% "lib-build-testkit" % "0.0.1" % Test,
      // org.lz4:lz4-java reaches the test classpath only here, transitively:
      // scalatestplus-play -> play-ws -> play -> pekko-serialization-jackson -> lz4-java.
      // Nothing on that classpath can call it. Pekko loads an LZ4 codec reflectively only when
      // pekko.serialization.jackson.compression.algorithm is set to lz4 (it ships `off`, and this
      // repo has no application.conf to override it), and the JacksonSerializer that would read
      // the setting is reached only through pekko remoting/clustering/persistence, none of which
      // resolves here. org.lz4 is dead upstream -- its newest version is a relocation POM, so
      // there is no version to bump to -- which makes dropping the jar the only way to keep
      // advisories against it off this build. Excluded on this dependency rather than through a
      // build-wide `excludeDependencies`, which writes an <exclusions> block onto every
      // compile-scope dependency in the published POM; this one is test scope, so it reaches no
      // consumer. ISS-4734
      ("org.scalatestplus.play" %% "scalatestplus-play" % "7.0.2" % Test)
        .exclude("org.lz4", "lz4-java")
    )
  )
