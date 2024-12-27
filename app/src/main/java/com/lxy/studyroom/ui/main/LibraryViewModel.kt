package com.lxy.studyroom.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lxy.studyroom.logic.Repository
import com.lxy.studyroom.logic.model.Library

class LibraryViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<String>()

    private val finishLiveData = MutableLiveData<String>()

    val libraryResponse = Transformations.switchMap(searchLiveData) { _ ->
        Repository.getAllLibrary()
    }

    val finishResponse = Transformations.switchMap(finishLiveData) { _ ->
        Repository.updateRecordToFinish()
    }

    fun getAllLibrary(query: String){
        searchLiveData.value = query
    }

    fun updateRecordToFinish(query: String){
        finishLiveData.value = query
    }

}