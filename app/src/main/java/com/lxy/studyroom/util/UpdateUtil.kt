package com.lxy.studyroom.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getExternalFilesDirs
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.FileUtils
import com.lxy.studyroom.R
import com.lxy.studyroom.StudyRoomApplication
import com.lxy.studyroom.logic.model.UpdateInfoBean
import me.jessyan.progressmanager.ProgressListener
import me.jessyan.progressmanager.ProgressManager
import me.jessyan.progressmanager.body.ProgressInfo


class UpdateUtil(private val context: Context) {

    private var downloadUrl: String = ""
    private val filePath: String = getExternalFilesDirs(context, null)[0].absolutePath + "/update.apk"
    private var hasUpdate = false
    private var forceUpdate = false
    private var versionCode = 0
    private var updateText: String = ""
    private var dialogIsShowing = false

    fun checkUpdate(data: UpdateInfoBean) {
        if (data.ignoreUpdate()) {
            return
        }
        if (AppUtils.getAppVersionCode() < data.versionCode) {
            hasUpdate = true
            downloadUrl = data.downloadUrl
            forceUpdate = data.isForceUpdate ?: false
            versionCode = data.versionCode ?: 0
            updateText = ""
            hasUpdate(forceUpdate, versionCode, updateText)
        } else {
            hasUpdate = false
        }
    }

    fun recheck() {
        if (dialogIsShowing) {
            return
        }
        if (hasUpdate && forceUpdate) {
            hasUpdate(true, versionCode, updateText)
        }
    }

    private fun hasUpdate(forceUpdate: Boolean, versionCode: Int, updateText: String?) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("版本升级")
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton("确定") { _, _ ->
                if (FileUtils.isFileExists(filePath)) {
                    val info = AppUtils.getApkInfo(filePath)
                    if (info != null) {
                        val code = info.versionCode
                        if (code >= versionCode) {
                            install()
                            return@setPositiveButton
                        }
                    }
                }
                download()
            }
        val view = View.inflate(context, R.layout.version_update_info, null)
        if (!TextUtils.isEmpty(updateText)) {
            view.findViewById<View>(R.id.contentLayout).visibility = View.VISIBLE
            val textView = view.findViewById<TextView>(R.id.content)
            textView.text = updateText
        }
        builder.setView(view)
        builder.setOnDismissListener { dialogIsShowing = false }
        if (!forceUpdate) {
            builder.setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
        }
        val alertDialog = builder.create()
        alertDialog.show()
        dialogIsShowing = true
        alertDialog.setCancelable(false)
    }

    @SuppressLint("CheckResult")
    private fun download() {
        // 创建进度条对话框
        val pd = ProgressDialog(context).apply {
            setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            setMessage("正在下载更新")
            setCancelable(false)
            show()
        }

        // 监听下载进度
        ProgressManager.getInstance().addResponseListener(downloadUrl, object : ProgressListener {
            override fun onProgress(progressInfo: ProgressInfo) {
                pd.progress = progressInfo.percent
            }

            override fun onError(id: Long, e: Exception?) {
                pd.dismiss()
            }
        })

        // 开始下载
        DownloadUtil.download(downloadUrl, filePath, object : DownloadUtil.DownloadListener {
            override fun onSuccess() {
                install()
                pd.dismiss()
            }

            override fun onError() {
                FileUtils.delete(filePath)
                Toast.makeText(StudyRoomApplication.context, "下载出错，请前往应用市场更新", Toast.LENGTH_SHORT).show()
                pd.dismiss()
            }
        })
    }


    private fun install() = AppUtils.installApp(filePath)

}