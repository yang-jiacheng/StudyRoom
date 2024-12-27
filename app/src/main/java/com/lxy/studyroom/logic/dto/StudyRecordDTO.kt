package com.lxy.studyroom.logic.dto

import java.io.Serializable

/**
 * 学习记录数据传输对象
 */

data class StudyRecordDTO(
    val catalogId: Int,
    var tag: String,
    val seat: Int,
    //计时方式：1正计时 2倒计时
    var timingMode: Int,
    //设置的自习时长，单位分钟（仅在倒计时有）
    var settingDuration: Int?
) : Serializable
