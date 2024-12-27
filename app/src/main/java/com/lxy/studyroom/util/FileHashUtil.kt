package com.lxy.studyroom.util

import java.io.File
import java.security.MessageDigest

object FileHashUtil {

    /**
     * val file = File("/path/to/file")
     * println("MD5 hash: ${getFileHash(file, "MD5")}")
     * println("SHA1 hash: ${getFileHash(file, "SHA-1")}")
     * println("SHA256 hash: ${getFileHash(file, "SHA-256")}")
     */
    fun getHash(file: File, algorithm: String): String {
        val digest = MessageDigest.getInstance(algorithm)
        file.inputStream().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                digest.update(buffer, 0, read)
            }
        }
        val hashBytes = digest.digest()
        return hashBytes.joinToString("") {
            "%02x".format(it)
        }
    }

}

fun main() {
    val file = File("D:\\AndroidCode\\StudyRoom\\app\\prod\\release\\studyroom-1.0.0-1-20230310.apk")
    println("MD5 hash: ${FileHashUtil.getHash(file, "MD5")}")
    println("SHA1 hash: ${FileHashUtil.getHash(file, "SHA-1")}")
    println("SHA256 hash: ${FileHashUtil.getHash(file, "SHA-256")}")
}