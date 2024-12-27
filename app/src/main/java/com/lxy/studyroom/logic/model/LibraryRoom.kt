package com.lxy.studyroom.logic.model

import java.io.Serializable

data class LibraryRoom (
    val id: Int,
    val name: String,
    val description: String? = null,
    val iconPath: String? = null,
    val coverPath: String? = null,
    val rooms: List<RoomDetail>?

) : Serializable
