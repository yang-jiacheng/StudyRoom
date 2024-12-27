package com.lxy.studyroom.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lxy.studyroom.logic.Repository
import com.lxy.studyroom.logic.dto.UserDTO

class TokenViewModel : ViewModel() {

    private val verCodeLiveData = MutableLiveData<String>()

    private val codeLoginLiveData = MutableLiveData<UserDTO>()

    private val passLoginLiveData = MutableLiveData<UserDTO>()

    private val loginOutLiveData = MutableLiveData<String>()

    val verCodeResponse = Transformations.switchMap(verCodeLiveData) { phone ->
        Repository.getVerificationCode(phone)
    }

    val codeLoginResponse = Transformations.switchMap(codeLoginLiveData){ userDTO ->
        Repository.loginByVerificationCode(userDTO.phone,userDTO.verificationCode)
    }

    val passLoginResponse = Transformations.switchMap(passLoginLiveData){ userDTO ->
        Repository.login(userDTO.phone,userDTO.password)
    }

    val loginOutResponse = Transformations.switchMap(loginOutLiveData){
        Repository.logout()
    }

    fun getVerificationCode(phone: String){
        verCodeLiveData.value = phone
    }

    fun loginByVerificationCode(phone: String,verificationCode: String){
        codeLoginLiveData.value = UserDTO(phone,"",verificationCode,"","","","","")
    }

    fun login(phone: String,password: String){
        passLoginLiveData.value = UserDTO(phone,password,"","","","","","")
    }

    fun logout(query: String){
        loginOutLiveData.value = query
    }



}