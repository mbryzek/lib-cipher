name := "lib-cipher"

version := "0.0.69"

// Publishing metadata, the guava pin, the browser-stack exclusions, the compiler gate and the
// root project's test reporting are shared by every lib-* repository and DECLARED in
// project/BryzekLibSettings.scala, a copy of devops's
// templates/scala-libs/project/BryzekLibSettings.scala that ci/build.sh holds byte-identical to
// it. That file carries why each setting is what it is; a change is edited there and copied here.
BryzekLibSettings.buildSettings("lib-cipher")

ThisBuild / scalaVersion := "3.8.4"

// The Jackson 2, Jackson 3 and logback security pins are DECLARED in project/BryzekPins.scala, a
// copy of devops's templates/scala-libs/project/BryzekPins.scala that ci/build.sh holds
// byte-identical to it, together with the pin specs under src/test/scala/com/bryzek/pins. That
// file carries why each floor is what it is; an advisory bump is edited there and copied here.
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
      "com.bryzek" %% "lib-util" % "0.0.74",
      "com.password4j" % "password4j" % "1.8.4",
      "org.springframework.security" % "spring-security-crypto" % "7.1.2",
      "org.mindrot" % "jbcrypt" % "0.4",
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
