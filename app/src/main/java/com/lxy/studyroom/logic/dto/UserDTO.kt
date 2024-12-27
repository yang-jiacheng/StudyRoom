package com.lxy.studyroom.logic.dto

import java.io.Serializable

/**
 * 用户信息数据传输对象
 */

data class UserDTO(
    val phone: String,
    val password: String,
    val verificationCode: String,
    var name: String,
    var gender: String,
    var address: String,
    var profilePath: String,
    var coverPath: String
) : Serializable
