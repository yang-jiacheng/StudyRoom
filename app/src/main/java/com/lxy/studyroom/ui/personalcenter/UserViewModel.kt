package com.lxy.studyroom.ui.personalcenter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lxy.studyroom.logic.Repository
import com.lxy.studyroom.logic.dto.UserDTO

class UserViewModel : ViewModel() {

    private val userLiveData = MutableLiveData<String>()

    private val upPasswordLiveData = MutableLiveData<UserDTO>()

    private val userByIdLiveData = MutableLiveData<Int>()

    private val upInfoLiveData = MutableLiveData<UserDTO>()

    val userInfoResponse = Transformations.switchMap(userLiveData) { _ ->
        Repository.getUserInfo()
    }

    val userByIdResponse = Transformations.switchMap(userByIdLiveData) { userId ->
        Repository.getUserInfoById(userId)
    }

    val upPasswordResponse = Transformations.switchMap(upPasswordLiveData) { userDTO ->
        Repository.updatePassword(userDTO.phone,userDTO.verificationCode,userDTO.password)
    }

    val upInfoResponse = Transformations.switchMap(upInfoLiveData) { userDTO ->
        Repository.updateUserInfo(userDTO.name,userDTO.gender,userDTO.address,userDTO.profilePath,userDTO.coverPath)
    }

    fun getUserInfo(query: String){
        userLiveData.value = query
    }

    fun updatePassword(phone: String,verificationCode: String,password: String){
        upPasswordLiveData.value = UserDTO(phone, password, verificationCode,"","","","","")
    }

    fun getUserInfoById(userId: Int){
        userByIdLiveData.value = userId
    }

    fun updateUserInfo(userDTO: UserDTO){
        upInfoLiveData.value = userDTO
    }

}