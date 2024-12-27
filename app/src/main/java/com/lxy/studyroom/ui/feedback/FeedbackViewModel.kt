package com.lxy.studyroom.ui.feedback

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lxy.studyroom.logic.Repository

class FeedbackViewModel : ViewModel() {

    //提交反馈
    private val submitFeedBackLiveData = MutableLiveData<Pair<String,String>>()

    val submitFeedBackResp = Transformations.switchMap(submitFeedBackLiveData) { pair ->
        Repository.submitFeedBack(pair.first,pair.second)
    }

    fun submitFeedBack(content: String,pic: String) {
        submitFeedBackLiveData.value = content to pic
    }

    //反馈列表
    private val feedBackListLiveData = MutableLiveData<Triple<Int,Int,Int>>()

    val feedBackListResp = Transformations.switchMap(feedBackListLiveData) { pair ->
        Repository.getFeedBackList(pair.first,pair.second,pair.third)
    }

    fun getFeedBackList(page: Int,limit: Int,mine: Int) {
        feedBackListLiveData.value = Triple(page,limit,mine)
    }

    //反馈详情
    private val feedBackDetailLiveData = MutableLiveData<Int>()

    val feedBackDetailResp = Transformations.switchMap(feedBackDetailLiveData) { id ->
        Repository.getFeedBackDetail(id)
    }

    fun getFeedBackDetail(id: Int) {
        feedBackDetailLiveData.value = id
    }


}