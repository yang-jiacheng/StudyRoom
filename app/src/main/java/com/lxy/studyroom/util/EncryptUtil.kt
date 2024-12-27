package com.lxy.studyroom.util

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object EncryptUtil {

    /**
     * 16位数组
     */
    private val SALT = charArrayOf(
        '1', '3', '5', '7', '9', 'a', 'c', 'e', 'g', 'i', 'k', 'm', 'o', 'q', 's', 'u'
    )

    /**
     * MD5加密
     */
    fun encryptMd5(origin: String): String {
        return try {
            val originBytes = origin.toByteArray(StandardCharsets.UTF_8)
            // 获取md5加密对象
            val md5 = MessageDigest.getInstance("MD5")
            md5.update(originBytes)
            val digest = md5.digest()
            val chars = CharArray(digest.size * 2)
            for (i in digest.indices) {
                val byte0 = digest[i]
                // 加盐
                chars[i * 2] = SALT[byte0.toInt() ushr 4 and 0xf]
                chars[i * 2 + 1] = SALT[byte0.toInt() and 0xf]
            }
            String(chars)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * SHA-256加密
     */
    fun encryptSha256(str: String): String {
        return try {
            val originBytes = str.toByteArray(StandardCharsets.UTF_8)
            val sha256 = MessageDigest.getInstance("SHA-256")
            sha256.update(originBytes)
            val bytes = sha256.digest()
            val stringBuilder = StringBuilder()
            var temp: String?
            for (aByte in bytes) {
                temp = Integer.toHexString(aByte.toInt() and 0xFF)
                if (temp.length == 1) {
                    // 1得到一位的进行补0操作
                    stringBuilder.append("0")
                }
                stringBuilder.append(temp)
            }
            stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun encryptSha256Salt(str: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(str.toByteArray(StandardCharsets.UTF_8))
            md.update(String(SALT).toByteArray(StandardCharsets.UTF_8))
            val bytes = md.digest()
            val sb = StringBuilder()
            for (b in bytes) {
                sb.append(String.format("%02x", b))
            }
            sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            ""
        }
    }

}
