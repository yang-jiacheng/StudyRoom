package com.lxy.studyroom.logic.network.service

import com.lxy.studyroom.logic.model.ResponseResult
import com.lxy.studyroom.logic.model.User
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface PersonalCenterService {

    /**
     * 获取个人信息
     */
    @POST("personalCenter/getUserInfo")
    fun getUserInfo() : Call<ResponseResult<User>>

    /**
     * 获取个人信息根据id
     */
    @POST("personalCenter/getUserInfoById")
    fun getUserInfoById(@Query("userId") userId: Int) : Call<ResponseResult<User>>

    /**
     * 修改个人信息
     */
    @POST("personalCenter/updateUserInfo")
    fun updateUserInfo(@Query("name") name: String, @Query("gender") gender: String, @Query("address") address: String,
                       @Query("profilePath") profilePath: String, @Query("coverPath") coverPath: String) : Call<ResponseResult<String?>>

    /**
     * 修改密码 ，密码是加密后的密码 用 EncryptUtil 的 SHA-256 加密方式
     */
    @POST("personalCenter/updatePassword")
    fun updatePassword(@Query("phone") phone: String, @Query("verificationCode") verificationCode: String,
                       @Query("password") password: String) : Call<ResponseResult<String?>>



}