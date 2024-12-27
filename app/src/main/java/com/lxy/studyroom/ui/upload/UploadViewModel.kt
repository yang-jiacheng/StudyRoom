package com.lxy.studyroom.ui.upload

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lxy.studyroom.logic.Repository
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * 上传服务器的ViewModel
 */

class UploadViewModel : ViewModel() {

    private val uploadLiveData = MutableLiveData<File>()

    private val uploadIconLiveData = MutableLiveData<File>()

    private val uploadCoverLiveData = MutableLiveData<File>()

    val uploadResp = Transformations.switchMap(uploadLiveData) { file ->
        Repository.upload(getMultipart(file))
    }

    val uploadIconResp = Transformations.switchMap(uploadIconLiveData) { file ->
        Repository.upload(getMultipart(file))
    }

    val uploadCoverResp = Transformations.switchMap(uploadCoverLiveData) { file ->
        Repository.upload(getMultipart(file))
    }

    fun upload(file: File){
        uploadLiveData.value = file
    }

    fun uploadIcon(file: File){
        uploadIconLiveData.value = file
    }

    fun uploadCover(file: File){
        uploadCoverLiveData.value = file
    }

    private fun getMultipart(file: File): List<MultipartBody.Part>{
        //创建MultipartBody.Builder对象，设置表单类型
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        val body = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        //添加表单数据，body创建的请求体
        builder.addFormDataPart("file",file.name,body)
        return builder.build().parts
    }

}