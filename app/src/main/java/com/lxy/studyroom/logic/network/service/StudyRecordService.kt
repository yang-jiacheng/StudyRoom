package com.lxy.studyroom.logic.network.service

import com.lxy.studyroom.logic.model.ResponseResult
import com.lxy.studyroom.logic.model.StudyRecord
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface StudyRecordService {

    /**
     * 获取自习笔记列表
     */
    @POST("studyRecord/getStudyNotes")
    fun getStudyNotes(@Query("page") page: Int,@Query("limit") limit: Int) : Call<ResponseResult<List<StudyRecord>?>>

    /**
     * 获取自习笔记详情
     */
    @POST("studyRecord/getStudyNoteDetail")
    fun getStudyNoteDetail(@Query("recordId") recordId: Int) : Call<ResponseResult<StudyRecord?>>

    /**
     * 编辑自习笔记
     */
    @POST("studyRecord/saveStudyNote")
    fun saveStudyNote(@Query("recordId") recordId: Int,@Query("content") content: String,@Query("pic") pic: String) : Call<ResponseResult<String?>>

    /**
     * 删除自习笔记
     */
    @POST("studyRecord/removeStudyNote")
    fun removeStudyNote(@Query("recordId") recordId: Int) : Call<ResponseResult<String?>>

    /**
     * 获取自习记录
     */
    @POST("studyRecord/getStudyRecord")
    fun getStudyRecord(@Query("page") page: Int,@Query("limit") limit: Int) : Call<ResponseResult<List<StudyRecord>?>>

}