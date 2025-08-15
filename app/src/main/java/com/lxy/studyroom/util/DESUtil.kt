package com.lxy.studyroom.util

import com.lxy.studyroom.BuildConfig
import org.apache.commons.codec.binary.Base64
import java.nio.charset.StandardCharsets
import java.security.Key
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESedeKeySpec
import javax.crypto.spec.IvParameterSpec


object DESUtil {

    private const val ALGORITHM = "DESede"
    private const val PARAM = "DESede/CBC/PKCS5Padding"
    private const val ENCODING = "utf-8"

    const val SECRET_KEY_CONFIG = BuildConfig.SECRET_KEY_CONFIG

    private val key: Key

    init {
        try {
            require(SECRET_KEY_CONFIG.length >= 24) { "3DES 密钥长度不得小于 24 位" }
            val spec = DESedeKeySpec(SECRET_KEY_CONFIG.toByteArray(StandardCharsets.UTF_8))
            val keyFactory = SecretKeyFactory.getInstance(ALGORITHM)
            key = keyFactory.generateSecret(spec)
        } catch (e: Exception) {
            throw RuntimeException("生成密钥失败", e)
        }
    }

    /**
     * 加密
     * @param plainText 普通文本
     */
    fun encode(plainText: String): String {
        try {
            // 随机生成IV
            val ivBytes = ByteArray(8)
            SecureRandom().nextBytes(ivBytes)
            val iv = IvParameterSpec(ivBytes)
            val cipher = Cipher.getInstance(PARAM)
            cipher.init(Cipher.ENCRYPT_MODE, key, iv)
            val encryptData = cipher.doFinal(plainText.toByteArray(charset(ENCODING)))
            // 拼接 IV + 密文
            val ivAndCipher = ByteArray(ivBytes.size + encryptData.size)
            System.arraycopy(ivBytes, 0, ivAndCipher, 0, ivBytes.size)
            System.arraycopy(encryptData, 0, ivAndCipher, ivBytes.size, encryptData.size)
            return Base64.encodeBase64String(ivAndCipher)
        } catch (e: Exception) {
            LogUtil.e("encode","加密失败")
            return ""
        }
    }

    /**
     * 解密
     * @param encryptText 加密文本
     */
    fun decode(encryptText: String): String {
        try {
            val ivAndCipher = Base64.decodeBase64(encryptText)
            // 提取 IV
            val ivBytes = ByteArray(8)
            System.arraycopy(ivAndCipher, 0, ivBytes, 0, ivBytes.size)
            val iv = IvParameterSpec(ivBytes)
            // 提取密文
            val cipherBytes = ByteArray(ivAndCipher.size - ivBytes.size)
            System.arraycopy(ivAndCipher, ivBytes.size, cipherBytes, 0, cipherBytes.size)
            val cipher = Cipher.getInstance(PARAM)
            cipher.init(Cipher.DECRYPT_MODE, key, iv)
            val decryptData = cipher.doFinal(cipherBytes)
            return String(decryptData, charset(ENCODING))
        } catch (e: Exception) {
            LogUtil.e("decode","解密失败")
            return ""
        }
    }


}


