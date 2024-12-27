package com.lxy.studyroom.logic.network.service

import com.lxy.studyroom.logic.model.ResponseResult
import com.lxy.studyroom.logic.model.UpdateInfoBean
import retrofit2.Call
import retrofit2.http.POST

interface VersionControlService {

    @POST("version/checkVersion")
    fun checkVersion() : Call<ResponseResult<UpdateInfoBean>>

}