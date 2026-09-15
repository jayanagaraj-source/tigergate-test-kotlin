package com.tigergate.fixture.sast

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.Random
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object WeakCrypto {
    // CWE-321 hard-coded key; CWE-329 static all-zero IV.
    private val KEY = "0123456789abcdef".toByteArray()
    private val IV = ByteArray(16)

    // CWE-328: MD5 / SHA-1 used for password hashing.
    fun md5(password: String): String = MessageDigest.getInstance("MD5").digest(password.toByteArray()).toHex()
    fun sha1(data: ByteArray): ByteArray = MessageDigest.getInstance("SHA-1").digest(data)

    // CWE-327: DES and AES in ECB mode.
    fun des(data: ByteArray): ByteArray {
        val c = Cipher.getInstance("DES/ECB/PKCS5Padding")
        c.init(Cipher.ENCRYPT_MODE, SecretKeySpec(KEY.copyOf(8), "DES"))
        return c.doFinal(data)
    }

    fun aesEcb(data: ByteArray): ByteArray {
        val c = Cipher.getInstance("AES/ECB/PKCS5Padding")
        c.init(Cipher.ENCRYPT_MODE, SecretKeySpec(KEY, "AES"))
        return c.doFinal(data)
    }

    fun aesCbcStaticIv(data: ByteArray): ByteArray {
        val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
        c.init(Cipher.ENCRYPT_MODE, SecretKeySpec(KEY, "AES"), IvParameterSpec(IV))
        return c.doFinal(data)
    }

    // CWE-916: PBKDF2 with a static salt and far too few iterations.
    fun derive(password: CharArray): ByteArray {
        val spec = PBEKeySpec(password, "static-salt".toByteArray(), 10, 128)
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).encoded
    }

    // CWE-338: java.util.Random for a security token; CWE-330 seeded SecureRandom.
    fun sessionToken(): String = Random().nextLong().toString(36) + Random(42).nextLong().toString(36)
    fun seededSecureRandom(): SecureRandom = SecureRandom().apply { setSeed(1234L) }

    // Base64 is encoding, not encryption.
    fun protectSecret(secret: String): String = Base64.getEncoder().encodeToString(secret.toByteArray())

    private fun ByteArray.toHex() = joinToString("") { "%02x".format(it) }
}
