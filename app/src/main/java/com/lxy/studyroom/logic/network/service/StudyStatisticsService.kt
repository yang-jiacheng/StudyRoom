package com.lxy.studyroom.logic.network.service

import com.lxy.studyroom.logic.model.ResponseResult
import com.lxy.studyroom.logic.model.UserRank
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface StudyStatisticsService {

    /**
     * 提交学习时长
     */
    @POST("studyStatistics/submitStudyDuration")
    fun submitStudyDuration(@Query("recordId") recordId: Int,@Query("duration") duration: Int) : Call<ResponseResult<String?>>

    /**
     * 获取总排行榜
     */
    @POST("studyStatistics/getRankings")
    fun getRankings() : Call<ResponseResult<List<UserRank>?>>

    /**
     * 获取总排行榜更新规则
     */
    @POST("studyStatistics/getRankingsRules")
    fun getRankingsRules() : Call<ResponseResult<String?>>

    /**
     * 获取自己的排名和信息
     */
    @GET("studyStatistics/getSelfRankings")
    fun getSelfRankings() : Call<ResponseResult<UserRank>>

}