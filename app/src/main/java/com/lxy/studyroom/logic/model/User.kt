package com.lxy.studyroom.logic.model

import java.io.Serializable

data class User(
    val id: Int,
    val name: String,
    val phone: String,
    val profilePath: String,
    val coverPath: String? = null,
    val gender: String,
    val createTime: String,
    val address: String? = null,
    val totalDuration: Int? = null
) : Serializable
