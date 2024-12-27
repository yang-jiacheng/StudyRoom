package com.lxy.studyroom.ui.room

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lxy.studyroom.logic.Repository

class StudyRecordViewModel : ViewModel() {

    //获取学习笔记详情
    private val noteDetailLiveData = MutableLiveData<Int>()

    val noteDetailResp = Transformations.switchMap(noteDetailLiveData) { recordId ->
        Repository.getStudyNoteDetail(recordId)
    }

    fun getStudyNoteDetail(recordId: Int){
        noteDetailLiveData.value = recordId
    }

    //获取学习笔记
    private val getNotesLiveData = MutableLiveData<Pair<Int,Int>>()

    val getNotesResp = Transformations.switchMap(getNotesLiveData) { pagePair ->
        Repository.getStudyNotes(pagePair.first,pagePair.second)
    }

    fun getStudyNotes(page: Int,limit: Int){
        getNotesLiveData.value = page to limit
    }

    //保存学习笔记
    private val saveNoteLiveData = MutableLiveData<Triple<Int,String,String>>()

    val saveNoteResp = Transformations.switchMap(saveNoteLiveData) { triple ->
        Repository.saveStudyNote(triple.first,triple.second,triple.third)
    }

    fun saveStudyNote(recordId: Int,content: String,pic: String) {
        saveNoteLiveData.value = Triple(recordId,content,pic)
    }

    //删除学习笔记
    private val removeNoteLiveData = MutableLiveData<Int>()

    val removeNoteResp =  Transformations.switchMap(removeNoteLiveData) { recordId ->
        Repository.removeStudyNote(recordId)
    }

    fun removeStudyNote(recordId: Int){
        removeNoteLiveData.value = recordId
    }

    //获取学习记录
    private val studyRecordLiveData = MutableLiveData<Pair<Int,Int>>()

    val studyRecordResp = Transformations.switchMap(studyRecordLiveData) { pagePair ->
        Repository.getStudyRecord(pagePair.first,pagePair.second)
    }

    fun getStudyRecord(page: Int,limit: Int){
        studyRecordLiveData.value = page to limit
    }

    //获取用户信息和排名
    private val rankInfoLiveData = MutableLiveData<String>()

    val rankInfoResponse = Transformations.switchMap(rankInfoLiveData) {
        Repository.getSelfRankings()
    }

    fun getSelfRankings(str: String){
        rankInfoLiveData.value = str
    }

    //获取记录和用户信息
    private val rankAndStatisticsLiveData = MutableLiveData<Pair<Int,Int>>()

    val rankAndStatisticsResponse = Transformations.switchMap(rankAndStatisticsLiveData) { pagePair ->
        Repository.getUserRecordsAndStatistics(pagePair.first,pagePair.second)
    }

    fun getUserRecordsAndStatistics(page: Int,limit: Int){
        rankAndStatisticsLiveData.value = page to limit
    }

}