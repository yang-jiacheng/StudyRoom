package com.lxy.studyroom.logic.model

import java.io.Serializable

data class Ranking(val rankList: List<UserRank>,val rule: String) : Serializable