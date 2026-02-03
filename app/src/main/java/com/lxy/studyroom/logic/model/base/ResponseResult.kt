package com.lxy.studyroom.logic.model.base

import com.google.gson.annotations.SerializedName
import com.lxy.studyroom.enums.ResponseEnum

class ResponseResult<T> {
    var code = 0
    var msg: String? = null

    @SerializedName("result")
    var data: T? = null

    fun isSuccess(): Boolean {
        return code == ResponseEnum.SUCCESS.code
    }

}