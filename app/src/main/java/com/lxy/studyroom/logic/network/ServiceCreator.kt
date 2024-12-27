package com.lxy.studyroom.logic.network

import com.ihsanbal.logging.Level
import com.ihsanbal.logging.Logger
import com.ihsanbal.logging.LoggingInterceptor
import com.lxy.studyroom.BuildConfig
import com.lxy.studyroom.constant.CommonConstant
import com.lxy.studyroom.util.DataStoreUtil
import com.lxy.studyroom.util.LogUtil
import me.jessyan.progressmanager.ProgressManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceCreator {

    private const val BASE_URL = BuildConfig.API_HOST

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(this.getOkHttpClient())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)

    /**
     * 设置请求头中的token
     */
    private fun setTokenInterceptor(): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val token = DataStoreUtil.getString(CommonConstant.BASE_TOKEN_NAME,"")
            LogUtil.e("ccc", "ServiceCreator.setTokenInterceptor: ${CommonConstant.BASE_TOKEN_NAME} = $token")
            val builder = chain.request().newBuilder()
            if (token.isNotEmpty()) {
                builder.addHeader(CommonConstant.BASE_TOKEN_NAME, token)
            }
            chain.proceed(builder.build())
        }
    }

    /**
     * 日志拦截器，打印请求与相应具体信息
     */
    private fun getLoggingInterceptor(): LoggingInterceptor {
        return LoggingInterceptor.Builder()
            .setLevel(Level.BASIC)
            .log(Platform.WARN)
            .request("Request")
            .response("Response")
            .logger(
                object : Logger {
                    override fun log(level: Int, tag: String?, msg: String?) {
                        LogUtil.w(tag ?: "getLoggingInterceptor tag 为空",msg?: "msg 为空")
                    }
                }
            )
            .build()
    }

    private fun getOkHttpClient(): OkHttpClient{
//        return ProgressManager.getInstance().with(
//            OkHttpClient.Builder()
//                .readTimeout(30, TimeUnit.SECONDS)
//                .connectTimeout(3, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .hostnameVerifier { hostname, _ -> BASE_URL.contains(hostname) }
//                .addInterceptor(this.setTokenInterceptor())
//                .addInterceptor(this.getLoggingInterceptor())
//        ).build()

        return  OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .hostnameVerifier { hostname, _ -> BASE_URL.contains(hostname) }
                .addInterceptor(this.setTokenInterceptor())
                .addInterceptor(this.getLoggingInterceptor())
        .build()
    }

}