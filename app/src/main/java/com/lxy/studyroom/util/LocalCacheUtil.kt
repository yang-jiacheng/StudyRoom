package com.lxy.studyroom.util

import android.content.Context
import java.io.File

class LocalCacheUtil(private val context: Context) {

    /**
     * 计算缓存的大小
     */
    fun getCacheSize(): String {
        var fileSize = 0L
        var cacheSize = "0KB"
        val filesDir = context.applicationContext.filesDir // /data/data/package_name/files
        val cacheDir = context.cacheDir // /data/data/package_name/cache
        fileSize += getDirSize(filesDir)
        fileSize += getDirSize(cacheDir)
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            val externalCacheDir =
                getExternalCacheDir(context) // "<sdcard>/Android/data/<package_name>/cache/"
            fileSize += getDirSize(externalCacheDir)
        }
        if (fileSize > 0) {
            cacheSize = formatFileSize(fileSize)
        }
        return cacheSize
    }

    /**
     * 清除app缓存
     */
    fun clearAppCache() {
        // 清除数据缓存
        clearCacheFolder(context.filesDir, System.currentTimeMillis())
        clearCacheFolder(context.cacheDir, System.currentTimeMillis())
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            clearCacheFolder(
                getExternalCacheDir(context),
                System.currentTimeMillis()
            )
        }
    }

    /**
     * 清除缓存目录
     *
     * @param dir 目录
     * @param curTime 当前系统时间
     * @return
     */
    private fun clearCacheFolder(dir: File?, curTime: Long): Int {
        var deletedFiles = 0
        if (dir != null && dir.isDirectory) {
            try {
                dir.listFiles()?.forEach { child ->
                    if (child.isDirectory) {
                        deletedFiles += clearCacheFolder(child, curTime)
                    }
                    if (child.lastModified() < curTime) {
                        if (child.delete()) {
                            deletedFiles++
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return deletedFiles
    }

    /**
     * 获取目录文件大小
     *
     * @param dir
     * @return
     */
    private fun getDirSize(dir: File?): Long {
        if (dir == null) {
            return 0
        }
        if (!dir.isDirectory) {
            return 0
        }
        var dirSize = 0L
        dir.listFiles()?.forEach { file ->
            if (file.isFile) {
                dirSize += file.length()
            } else if (file.isDirectory) {
                dirSize += file.length()
                dirSize += getDirSize(file) // 递归调用继续统计
            }
        }
        return dirSize
    }

    /**
     * 将二进制长度转换成文件大小
     *
     * @param length
     * @return
     */
    fun formatFileSize(length: Long): String {
        return when {
            length >= 1073741824 -> String.format("%.2fGB", length.toFloat() / 1073741824)
            length >= 1048576 -> String.format("%.2fMB", length.toFloat() / 1048576)
            length >= 1024 -> String.format("%.2fKB", length.toFloat() / 1024)
            else -> "$length B"
        }
    }



    fun isMethodsCompat(versionCode: Int): Boolean {
        val currentVersion = android.os.Build.VERSION.SDK_INT
        return currentVersion >= versionCode
    }

    fun getExternalCacheDir(context: Context): File? {
        // return context.getExternalCacheDir(); API level 8
        // e.g. "<sdcard>/Android/data/<package_name>/cache/"
        return context.externalCacheDir
    }
}
