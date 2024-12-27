package com.lxy.studyroom.util

import org.apache.commons.codec.binary.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESedeKeySpec
import javax.crypto.spec.IvParameterSpec

object DESUtil {

    //算法
    private const val ALGORITHM = "DESede"
    //参数：第一段是加密算法的名称，第二段是分组加密的模式，第三段是指最后一个分组的填充方式
    private const val PARAM = "DESede/CBC/PKCS5Padding"
    // 密钥 长度不得小于24
    private const val SECRET_KEY = "lanshan-edu-ycyk-2022-java"
    // 向量 可有可无 终端后台也要约定
    private const val IV = "20220802"
    // 加解密统一使用的编码方式
    private const val ENCODING = "utf-8"
    // 密钥种子
    private val KEY: SecretKey

    init {
        try {
            // DES算法策略
            val spec = DESedeKeySpec(SECRET_KEY.toByteArray())
            // 密钥工厂
            val keyfactory = SecretKeyFactory.getInstance(ALGORITHM)
            // 获取密钥种子
            KEY = keyfactory.generateSecret(spec)
        } catch (e: Exception) {
            throw RuntimeException("生成密钥种子失败", e)
        }
    }

    /**
     * 加密
     * @param plainText 普通文本
     */
    fun encode(plainText: String): String {
        try {
            val cipher = Cipher.getInstance(PARAM)
            val ips = IvParameterSpec(IV.toByteArray())
            cipher.init(Cipher.ENCRYPT_MODE, KEY, ips)
            val encryptData = cipher.doFinal(plainText.toByteArray(charset(ENCODING)))
            return Base64.encodeBase64String(encryptData)
        } catch (e: Exception) {
            throw RuntimeException("加密失败", e)
        }
    }

    /**
     * 解密
     * @param encryptText 加密文本
     */
    fun decode(encryptText: String): String {
        try {
            val cipher = Cipher.getInstance(PARAM)
            val ips = IvParameterSpec(IV.toByteArray())
            cipher.init(Cipher.DECRYPT_MODE, KEY, ips)
            val decryptData = cipher.doFinal(Base64.decodeBase64(encryptText))
            return String(decryptData, charset(ENCODING))
        } catch (e: Exception) {
            throw RuntimeException("解密失败", e)
        }
    }


}

fun main() {
    val text = "4f1499ea2f,Cu3KsAxAOv,c4ce45bb-32a5-4dcc-82c1-4dd61a74c133,84420889-902d-425e-b938-99290ff1c4cd"
    val textP = "yJZoKH2SHz4G08O1itQ3cfxHtB21/1jQ0AZGNMxib8k/p7bp5bYnBFUIdi26Q8W7xKRgLKaVOYniySbT7QrZrOd7XKkL8ut1eGgqSW3D9gxgT8HNjJozlAeRXMWeLAoz"
    val a = DESUtil.decode("yJZoKH2SHz4G08O1itQ3cfxHtB21/1jQ0AZGNMxib8k/p7bp5bYnBFUIdi26Q8W7xKRgLKaVOYniySbT7QrZrOd7XKkL8ut1eGgqSW3D9gxgT8HNjJozlAeRXMWeLAoz")
    val b = DESUtil.encode("4f1499ea2f,Cu3KsAxAOv,c4ce45bb-32a5-4dcc-82c1-4dd61a74c133,84420889-902d-425e-b938-99290ff1c4cd")
    println(a)
    println(b)

    println(text == a)
    println(textP == b)
}


