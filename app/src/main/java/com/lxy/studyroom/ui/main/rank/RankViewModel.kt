package com.lxy.studyroom.ui.main.rank

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lxy.studyroom.logic.Repository
import com.lxy.studyroom.util.LogUtil

class RankViewModel : ViewModel() {

    private val rankLiveData = MutableLiveData<String>()

    val rankResponse = Transformations.switchMap(rankLiveData) {
        Repository.getRankAndRule()
    }

    fun getRankAndRule(str: String){
        rankLiveData.value = str
    }

}