#!/usr/bin/env bash
#
# lib-cipher's build (ISS-2173). READ devops/templates/ci/build.sh AND
# devops/docs/ci.md FIRST: the three load-bearing properties — landing this file
# IS the enrolment, `set -euo pipefail` is not style, and the fleet posts the
# `ci` context — are explained there and are unchanged here.
#
# WHAT A GREEN HERE VOUCHES FOR: this library's own sources compile under
# `-Werror` with the full `-Wunused` set and its own ScalaTest suite passes, on
# the exact commit about to be squashed.
#
# WHAT IT DOES NOT VOUCH FOR, and this is the important half: THAT ANY CONSUMER
# STILL COMPILES. Nothing in the fleet builds `platform` or `acumen` against a
# lib PR's tree. A consumer resolves a RELEASED version out of Maven Central, and
# no released version exists until the library's rollout publishes one
# (`dev rollout --app lib-cipher`, which tags main and has a runner build and
# upload the signed bundle), so a merge here publishes nothing. So a PR that
# changes a signature is green here and breaks platform at the moment somebody
# bumps the pin. That break surfaces in the CONSUMER's own version-bump PR,
# verified by the consumer's own build. Enrolling this repo narrows nothing about
# that and is not meant to.
#
# THIS REPO IS ITSELF A CONSUMER — it pins `"com.bryzek" %% "lib-util"` — and the
# paragraph above applies to that edge unchanged: this build resolves the
# RELEASED lib-util out of Maven Central, so a green here says nothing about an
# unreleased lib-util PR, and a lib-util change reaches this suite only once
# lib-util's rollout has released it and a PR here has bumped the pin.
#
# NO DATABASE BLOCK, and that is the whole difference from
# devops/templates/ci/build-scala.sh. That template exists for platform and
# acumen, which reach a Postgres through `CONF_DB_DEV_URL`; this suite is pure
# in-process ScalaTest over hashing and encryption helpers and opens no
# connection to anything. Copying the block would start a container, hold a host
# port from the range `dev db session` allocates out of, and depend on the
# DigitalOcean registry credential — three ways to go red that have nothing to do
# with the code under test, and the session-database port range is the single
# biggest producer of false red in the fleet (ISS-2178).
#
# FORMATTING IS REPORTED, NEVER ENFORCED (ISS-19427). A build fails only for a
# functional problem, and an unformatted file behaves exactly as its formatted
# copy does. `scalafmtOnCompile := true` formats the sources, and
# `scripts/check-format` (a copy of devops templates/ci/check-format, which runs
# `dev fmt report`: the native scalafmt CLI over the sources AND `build.sbt`,
# which nothing else formats) prints every unformatted file and exits 0. The
# hygiene sweep runs it over `main` and fixes what it finds.
#
# `-Xmx4G` in `.jvmopts` — MEASURED, not copied (ISS-1171's method, run
# 2026-08-11). Cold checkout, `SBT_OPTS="-Xmx4G -Xss4M"`, process-tree RSS
# sampled with `/bin/ps -axo pid=,ppid=,rss=`: peak 1.1 GB, exit 0, 7 tests
# passed. 4G is `Agent::Heap::MIN_GB` — the least any runner hands a build, so
# nothing below it is worth measuring. The file is here rather than omitted
# because an omission is indistinguishable from nobody having looked.
# `Test / fork` is false in this build, so `SBT_OPTS` is the whole ceiling and
# there is no forked test JVM outside it to pin.
#
# TEST HEALTH READS THE NEXT TWO LINES (ISS-19887, design DES-18577 "Every repository
# with CI"). The first is where sbt leaves a JUnit report per suite, which is what the
# fleet writes the test ledger from: `TEST-<suite>.xml` is the build's run of a suite,
# `FIRST-<suite>.xml` the first run of one the build reran. The second says this build
# runs with the lists platform holds: whatever starts it fetches them first and exports
# the file as `CI_TEST_LISTS`. ci/test-health.sh (a copy of devops
# templates/sbt-test-health) and lib-build-sbt's CiTestListsPlugin and CiTestRerunPlugin
# are the runner half.
#
# ci-test-reports: target/test-reports/*.xml
# ci-test-lists: required
set -euo pipefail

echo "building ${CI_REPO:-?} @ ${CI_SHA:-?} (${CI_EVENT:-?}, clean=${CI_CLEAN_BUILD:-?})"

# ci-advisory: scripts/check-format
scripts/check-format || echo "check-format: could not measure this tree — advisory, nothing fails on it"

# THE SUITE, WITH TEST HEALTH (ISS-19887). `ci_test_health_sbt` prints the `ci-lists:` line for
# the lists `CI_TEST_LISTS` names (exiting 75 on a file that is not one, or on `CI_SLOW_SPECS=1`
# with none), and `ciTest` runs every spec the lists leave in -- the slow ones alone under
# `CI_SLOW_SPECS=1` -- then reruns each spec that failed once, printing `ci-flake:` per spec that
# passed the second time and `ci-rerun: done`. A spec that fails twice fails the build.
source ci/test-health.sh
ci_test_health_sbt
sbt -Dsbt.log.noformat=true "${ci_sbt_test_commands[@]}"
