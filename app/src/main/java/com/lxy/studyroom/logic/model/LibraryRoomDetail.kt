package com.lxy.studyroom.logic.model

import java.io.Serializable

data class LibraryRoomDetail(
    val id: Long,
    val parentId: Long,
    val name: String,
    val personCount: Int,
    val parentName: String,
    val libraryName: String
) : Serializable
