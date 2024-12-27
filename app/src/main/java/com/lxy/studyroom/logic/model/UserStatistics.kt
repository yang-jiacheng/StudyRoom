package com.lxy.studyroom.logic.model

import java.io.Serializable

data class UserStatistics(val userRank: UserRank,val records: List<StudyRecord>?) : Serializable