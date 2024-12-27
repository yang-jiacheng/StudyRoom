package com.lxy.studyroom.logic.network.service

import com.lxy.studyroom.logic.model.Library
import com.lxy.studyroom.logic.model.LibraryRoom
import com.lxy.studyroom.logic.model.ResponseResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LibraryService {

    /**
     * 获取所有图书馆
     */
    @POST("home/getClassify")
    fun getAllLibrary() : Call<ResponseResult<List<Library>>>

    /**
     * 图书馆详情
     */
    @POST("catalog/getClassifyDetail")
    fun getClassifyDetail(@Query("classifyId") classifyId: Int) : Call<ResponseResult<LibraryRoom>>



}