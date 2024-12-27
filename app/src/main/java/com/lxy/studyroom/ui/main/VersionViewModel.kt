package com.lxy.studyroom.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lxy.studyroom.logic.Repository
import com.lxy.studyroom.logic.model.Library

class VersionViewModel : ViewModel() {

    private val versionLiveData = MutableLiveData<String>()

    val versionResponse = Transformations.switchMap(versionLiveData) { _ ->
        Repository.checkVersion()
    }

    fun checkVersion(query: String){
        versionLiveData.value = query
    }

}