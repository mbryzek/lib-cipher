package com.bryzek.cipher

import com.bryzek.util.Base64Util
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.util.UUID
import scala.util.{Failure, Success, Try}

class CipherLibrarySpec extends AnyWordSpec with Matchers {

  // bcrypt's cost is exponential: the default 14 rounds is ~1s per hash or check, so the
  // round-trip assertions run at bcrypt's minimum cost. The cost is a parameter each library
  // passes straight through, which "hashes at the configured cost" asserts, and the default
  // itself is asserted on the config without hashing at it.
  private val TestConfig: CiphersConfig = CiphersConfig(rounds = 4)
  private val ciphers: Ciphers = Ciphers(TestConfig)

  "readme" in {
    val cipher = ciphers.latest
    val enc = cipher.hash("plaintext")
    enc.rounds mustBe TestConfig.rounds
    cipher.isValid(plaintext = "plaintext", hash = enc.hash, salt = enc.salt) mustBe true
    cipher.isValid(plaintext = "test", hash = enc.hash, salt = enc.salt) mustBe false
  }

  "default config" in {
    Ciphers.DefaultConfig.rounds mustBe 14
    Ciphers().config mustBe Ciphers.DefaultConfig
  }

  "hash" in {
    ciphers.libraries.flatMap { lib =>
      val plaintext = UUID.randomUUID().toString
      val enc = lib.hash(plaintext)
      if (!lib.isValid(plaintext = plaintext, hash = enc.hash, salt = enc.salt)) {
        Some(s"Lib[${lib.key}] failed to validate")
      } else if (lib.isValid(plaintext = UUID.randomUUID().toString, hash = enc.hash, salt = enc.salt)) {
        Some(s"Lib[${lib.key} validated a random plaintext")
      } else {
        None
      }
    } mustBe Nil
  }

  "hashes at the configured cost" in {
    ciphers.libraries
      .filterNot(_.key == "plaintext")
      .flatMap { lib =>
        val decoded = Base64Util.decode(lib.hash("plaintext").hash)
        if (decoded.startsWith("$2") && decoded.split('$').lift(2).contains("04")) {
          None
        } else {
          Some(s"Lib[${lib.key}] did not hash at cost 04")
        }
      } mustBe Nil
  }

  "latest" in {
    Ciphers.Default.latest.key mustBe "password4j"
  }

  "instance" in {
    Ciphers.Default.instance("password4j").get.key mustBe "password4j"
    Ciphers.Default.instance("mindrot").get.key mustBe "mindrot"
    Ciphers.Default.instance("t3hnar").get.key mustBe "t3hnar"
    Ciphers.Default.instance(UUID.randomUUID().toString) mustBe None
  }

  "all library keys are distinct" in {
    Ciphers.Default.libraries.map(_.key).distinct.length mustBe Ciphers.Default.libraries.length
  }

  "supports long passwords" in {
    val plaintext = "a" * 100
    ciphers.libraries
      .filterNot(_.key == "t3hnar")
      .flatMap { lib =>
        Try {
          lib.hash(plaintext)
        } match {
          case Success(_) => None
          case Failure(ex) => Some(s"Lib[${lib.key}] failed to hash long password: ${ex.getMessage}")
        }
      } mustBe Nil
  }
}
