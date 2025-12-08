package com.bryzek.cipher

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlaintextSpec extends AnyWordSpec with Matchers {

  "hash" in {
    val cipher = com.bryzek.cipher.Ciphers().instance("plaintext").get
    val hash = cipher.hash("text").hash
    hash mustBe Base64Util.encode("text")
    cipher.isValid("text", hash) mustBe true
    cipher.isValid("text2", hash) mustBe false
  }

}
