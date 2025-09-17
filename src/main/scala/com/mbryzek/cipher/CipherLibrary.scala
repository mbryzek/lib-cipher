package com.mbryzek.cipher

import java.security.SecureRandom

/** @param hash
  *   Base 64 encoded hash of the provided plaintext value
  */
case class HashedValue(
  library: String,
  hash: String,
  salt: Option[String],
  rounds: Int,
  timeToHashMs: Long
)

case class CiphersConfig(rounds: Int)

object Ciphers {
  private[cipher] val random: SecureRandom = new SecureRandom()
  val DefaultConfig: CiphersConfig = CiphersConfig(rounds = 14)
  val Default: Ciphers = Ciphers(DefaultConfig)
}

case class Ciphers(config: CiphersConfig = Ciphers.DefaultConfig) {
  val libraries: Seq[CipherLibrary] = Seq(
    CipherLibraryT3(config),
    CipherLibraryPassword4J(config),
    CipherLibraryMindrot(config)
  )

  def latest: CipherLibrary = CipherLibraryPassword4J(config)

  def instance(library: String): Option[CipherLibrary] = {
    val formatted = library.toLowerCase().trim
    libraries.find(_.key == formatted)
  }
}

sealed trait CipherLibrary {
  def config: CiphersConfig
  def key: String
  def isValid(plaintext: String, hash: String, salt: Option[String] = None): Boolean
  def hash(plaintext: String): HashedValue

  protected final def toHashedValue(salt: Option[String])(doHash: => String): HashedValue = {
    val start = System.currentTimeMillis()
    val enc = doHash
    val duration = System.currentTimeMillis() - start

    HashedValue(
      library = key,
      hash = Base64Util.encode(enc),
      salt = salt,
      rounds = config.rounds,
      timeToHashMs = duration
    )
  }
}

case class CipherLibraryPassword4J(config: CiphersConfig) extends CipherLibrary {
  import com.password4j.types.Bcrypt
  import com.password4j.{BcryptFunction, Password}

  private val bcrypt = BcryptFunction.getInstance(Bcrypt.B, config.rounds)

  override val key: String = "password4j"
  override def isValid(plaintext: String, hash: String, salt: Option[String] = None): Boolean = {
    Password
      .check(plaintext, Base64Util.decode(hash))
      .`with`(bcrypt)
  }

  override def hash(plaintext: String): HashedValue = {
    toHashedValue(None) {
      Password
        .hash(plaintext)
        .`with`(bcrypt)
        .getResult
    }
  }
}

case class CipherLibraryT3(config: CiphersConfig) extends CipherLibrary {
  import org.springframework.security.crypto.bcrypt.BCrypt

  override val key: String = "t3hnar"

  override def isValid(plaintext: String, hash: String, salt: Option[String] = None): Boolean = {
    salt match {
      case None => false
      case Some(s) =>
        BCrypt.checkpw(withSalt(salt = s, plaintext = plaintext), Base64Util.decode(hash))
    }
  }

  override def hash(plaintext: String): HashedValue = {
    val salt = Ciphers.random.nextLong().toString
    toHashedValue(Some(salt)) {
      BCrypt.hashpw(withSalt(salt, plaintext), BCrypt.gensalt(config.rounds))
    }
  }

  private def withSalt(salt: String, plaintext: String): String = {
    s"$salt:$plaintext"
  }
}

case class CipherLibraryMindrot(config: CiphersConfig) extends CipherLibrary {
  import org.mindrot.jbcrypt.BCrypt

  override val key: String = "mindrot"
  override def isValid(plaintext: String, hash: String, salt: Option[String] = None): Boolean = {
    BCrypt.checkpw(plaintext, Base64Util.decode(hash))
  }

  override def hash(plaintext: String): HashedValue = {
    toHashedValue(None) {
      BCrypt.hashpw(plaintext, BCrypt.gensalt(config.rounds))
    }
  }
}
