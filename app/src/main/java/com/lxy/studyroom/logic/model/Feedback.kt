package com.lxy.studyroom.logic.model

import java.io.Serializable

data class Feedback(
    val id: Int,
    val userId: Int,
    val name: String,
    val phone: String,
    val profilePath: String?,
    val content: String,
    val pic: String?,
    val reply: String?,
    val adminId: Int?,
    val adminName: String?,
    val createTime: String,
    val replyTime: String?,
    //用户是否可见 1可见 2不可见
    val status: Int,
    //回复状态 0未回复 1已回复
    val replyStatus: Int,

) : Serializable
