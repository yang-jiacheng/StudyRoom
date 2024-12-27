package com.lxy.studyroom.logic.network.service

import com.lxy.studyroom.logic.model.ResponseResult
import com.lxy.studyroom.logic.model.User
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface TokenService {

    /**
     * 密码登录
     */
    @POST("token/login")
    fun login(@Query("phone") phone: String,@Query("password") password: String) : Call<ResponseResult<String?>>

    /**
     * 验证码登录|注册
     */
    @POST("token/loginByVerificationCode")
    fun loginByVerificationCode(@Query("phone") phone: String,@Query("verificationCode") verificationCode: String,) : Call<ResponseResult<String?>>

    /**
     * 获取验证码
     */
    @POST("token/getVerificationCode")
    fun getVerificationCode(@Query("phone") phone: String) : Call<ResponseResult<String?>>

    /**
     * 登出
     */
    @POST("token/logout")
    fun logout() : Call<ResponseResult<String?>>

}