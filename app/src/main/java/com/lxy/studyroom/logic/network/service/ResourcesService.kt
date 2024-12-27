package com.lxy.studyroom.logic.network.service

import com.lxy.studyroom.logic.model.ResponseResult
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ResourcesService {

    @Multipart
    @POST("resources/upload")
    fun upload(@Part file :List<MultipartBody.Part>): Call<ResponseResult<List<String>?>>

}