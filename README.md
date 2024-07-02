# lib-cipher

Simple access to hash and verify values with bcrypt


## Example

```scala
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

```