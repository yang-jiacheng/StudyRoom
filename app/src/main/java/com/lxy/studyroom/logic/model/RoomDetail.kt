package com.lxy.studyroom.logic.model

import java.io.Serializable

data class RoomDetail(
    val catalogId: Long,
    val classifyId: Long,
    val parentId: Long,
    val level: Int,
    val catalogName: String,
    val personCount: Int,
    val currCount: Int? = null,
    val sort: Int? = 0
) : Serializable
