package com.mbryzek.cipher

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.util.UUID
import scala.util.{Failure, Success, Try}

class CipherLibrarySpec extends AnyWordSpec with Matchers {

  "readme" in {
    val cipher = com.mbryzek.cipher.Ciphers().latest
    val enc = cipher.hash("plaintext")
    println(
      s"Encrypted hash, base 64 encoded is: ${enc.hash}. Config rounds[${enc.rounds}], timeMs[${enc.timeToHashMs}]"
    )
    if (cipher.isValid(plaintext = "test", hash = enc.hash, salt = enc.salt)) {
      println("Plaintext matches hash")
    } else {
      println("Plaintext does not match hash")
    }
  }

  "hash" in {
    Ciphers().libraries.flatMap { lib =>
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

  "latest" in {
    Ciphers().latest.key mustBe "password4j"
  }

  "instance" in {
    Ciphers().instance("password4j").get.key mustBe "password4j"
    Ciphers().instance("mindrot").get.key mustBe "mindrot"
    Ciphers().instance("t3hnar").get.key mustBe "t3hnar"
    Ciphers().instance(UUID.randomUUID().toString) mustBe None
  }

  "all library keys are distinct" in {
    Ciphers().libraries.map(_.key).distinct.length mustBe Ciphers().libraries.length
  }

  "supports long passwords" in {
    val plaintext = "a" * 100
    Ciphers().libraries
      .filterNot(_.key == "t3hnar")
      .flatMap { lib =>
        Try {
          lib.hash(plaintext)
        } match {
          case Success(enc) => None
          case Failure(ex) => Some(s"Lib[${lib.key}] failed to hash long password: ${ex.getMessage}")
        }
      } mustBe Nil
  }
}
