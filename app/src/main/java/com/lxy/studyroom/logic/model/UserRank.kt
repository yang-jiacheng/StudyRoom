package com.lxy.studyroom.logic.model

import java.io.Serializable

data class UserRank(
    val id: Int,
    val name: String,
    val profilePath: String?,
    val todayDuration: Int? = 0,
    val totalDuration: Int,
    val ranking: Int
) : Serializable


