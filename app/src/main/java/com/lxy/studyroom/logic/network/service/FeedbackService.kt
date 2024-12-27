package com.lxy.studyroom.logic.network.service

import com.lxy.studyroom.logic.model.Feedback
import com.lxy.studyroom.logic.model.ResponseResult
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface FeedbackService {

    /**
     * 提交反馈
     */
    @POST("feedBack/submitFeedBack")
    fun submitFeedBack(@Query("content") content: String,@Query("pic") pic: String) : Call<ResponseResult<String?>>

    /**
     * 获取反馈列表
     * mine是否我的反馈 1是
     */
    @POST("feedBack/getFeedBackList")
    fun getFeedBackList(@Query("page") page: Int,@Query("limit") limit: Int,@Query("mine") mine: Int) : Call<ResponseResult<List<Feedback>?>>

    /**
     * 反馈详情
     */
    @POST("feedBack/getFeedBackDetail")
    fun getFeedBackDetail(@Query("id") id: Int) : Call<ResponseResult<Feedback>>

}