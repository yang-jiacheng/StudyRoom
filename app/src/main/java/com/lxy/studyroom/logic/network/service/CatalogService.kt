package com.lxy.studyroom.logic.network.service

import com.lxy.studyroom.logic.dto.StudyRecordDTO
import com.lxy.studyroom.logic.model.LibraryRoomDetail
import com.lxy.studyroom.logic.model.base.ResponseResult
import com.lxy.studyroom.logic.model.StudyRecord
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface CatalogService {

    /**
     * 自习室详情
     */
    @POST("catalog/getRoomDetail")
    fun getRoomDetail(@Query("roomId") roomId: Long) : Call<ResponseResult<LibraryRoomDetail?>>

    /**
     * 获取自习中的用户记录
     */
    @POST("catalog/getLearningRecords")
    fun getLearningRecords(@Query("catalogId") catalogId: Long) : Call<ResponseResult<List<StudyRecord>?>>

    /**
     * 获取自习中记录详情
     */
    @POST("catalog/getLearningRecordDetail")
    fun getLearningRecordDetail(@Query("recordId") recordId: Long) : Call<ResponseResult<StudyRecord>>

    /**
     * 开始自习 返回记录id
     */
    @POST("catalog/startStudy")
    fun startStudy(@Body studyRecordDTO:StudyRecordDTO) : Call<ResponseResult<Long>>


    /**
     * 结束自习  返回实际学习时间
     */
    @POST("catalog/stopStudy")
    fun stopStudy(@Query("recordId") recordId: Long) : Call<ResponseResult<Int>>

    @POST("catalog/updateRecordToFinish")
    fun updateRecordToFinish() : Call<ResponseResult<String?>>

}