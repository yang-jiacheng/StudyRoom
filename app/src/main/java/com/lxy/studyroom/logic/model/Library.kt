package com.lxy.studyroom.logic.model

import java.io.Serializable

data class Library (
    val id: Int,
    val name: String,
    val description: String? = null,
    val iconPath: String? = null,
    val coverPath: String? = null,
    val studyCount: Int? = null,
    val sort: Int? = null,
    val createTime: String? = null,
    var flag: Boolean? = null
) : Serializable
