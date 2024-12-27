package com.lxy.studyroom.util

import android.annotation.SuppressLint
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import com.lxy.studyroom.BuildConfig
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jessyan.progressmanager.ProgressManager
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

object DownloadUtil {

    @SuppressLint("CheckResult")
    @JvmStatic
    fun download(url: String, filePath: String, listener: DownloadListener) {

        val loggingInterceptor = LoggingInterceptor.Builder()
            .setLevel(Level.BASIC)
            .log(Platform.WARN)
            .request("Request")
            .response("Response")
            .build()

        val clientBuilder = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)

        val client = ProgressManager.getInstance().with(clientBuilder)
            .build()

        val builder = Retrofit.Builder()
            .baseUrl(BuildConfig.API_HOST)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)

        val downloadApi = builder.build().create(DownloadApi::class.java)

        downloadApi.download(url)
            .subscribeOn(Schedulers.io())
            .map { responseBody ->
                FileUtils.createOrExistsFile(filePath)
                try {
                    FileIOUtils.writeFileFromIS(filePath, responseBody.byteStream())
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ success ->
                if (success) {
                    listener.onSuccess()
                } else {
                    listener.onError()
                }
            }, {
                listener.onError()
            })
    }

    interface DownloadApi {
        @Streaming
        @GET
        fun download(@Url url: String): Observable<ResponseBody>
    }

    interface DownloadListener {
        fun onSuccess()
        fun onError()
    }
}
