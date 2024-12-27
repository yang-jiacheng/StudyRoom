package com.lxy.studyroom.logic.model

import java.io.Serializable

data class RoomDetail(
    val catalogId: Int,
    val classifyId: Int,
    val parentId: Int,
    val level: Int,
    val catalogName: String,
    val personCount: Int,
    val currCount: Int? = null,
    val sort: Int? = 0
) : Serializable
