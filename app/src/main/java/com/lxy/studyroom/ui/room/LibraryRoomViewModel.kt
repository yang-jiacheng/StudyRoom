package com.lxy.studyroom.ui.room

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lxy.studyroom.logic.Repository
import com.lxy.studyroom.logic.dto.StudyDurationDTO
import com.lxy.studyroom.logic.dto.StudyRecordDTO
import com.lxy.studyroom.util.LogUtil

class LibraryRoomViewModel : ViewModel() {

    private val libDetailLiveData = MutableLiveData<Int>()

    private val roomRecordLiveData = MutableLiveData<Int>()

    private val startStudyLiveData = MutableLiveData<StudyRecordDTO>()

    private val stopStudyLiveData = MutableLiveData<Int>()

    private val recordDetailLiveData = MutableLiveData<Int>()

    private val submitStudyLiveData = MutableLiveData<StudyDurationDTO>()

    val libDetailResponse = Transformations.switchMap(libDetailLiveData) { classifyId ->
        LogUtil.e("LibraryRoomViewModel switchMap",classifyId.toString())
        Repository.getClassifyDetail(classifyId)
    }

    val roomRecordResponse = Transformations.switchMap(roomRecordLiveData) { roomId ->
        Repository.getRoomDetailAndRecords(roomId)
    }

    val startStudyResponse = Transformations.switchMap(startStudyLiveData) { studyRecordDTO ->
        Repository.startStudy(studyRecordDTO)
    }

    val stopStudyResponse = Transformations.switchMap(stopStudyLiveData){ recordId ->
        Repository.stopStudy(recordId)
    }

    val recordDetailResponse = Transformations.switchMap(recordDetailLiveData){ recordId ->
        Repository.getLearningRecordDetail(recordId)
    }

    val submitStudyResponse = Transformations.switchMap(submitStudyLiveData){ studyDurationDTO ->
        Repository.submitStudyDuration(studyDurationDTO.recordId,studyDurationDTO.duration)
    }

    fun getClassifyDetail(classifyId: Int){
        libDetailLiveData.value = classifyId
    }

    fun getRoomDetailAndRecords(roomId: Int){
        roomRecordLiveData.value = roomId
    }

    fun startStudy(studyRecordDTO: StudyRecordDTO){
        startStudyLiveData.value = studyRecordDTO
    }

    fun stopStudy(recordId: Int){
        stopStudyLiveData.value = recordId
    }

    fun getLearningRecordDetail(recordId: Int){
        recordDetailLiveData.value = recordId
    }

    fun submitStudyDuration(studyDurationDTO: StudyDurationDTO){
        submitStudyLiveData.value = studyDurationDTO
    }

}