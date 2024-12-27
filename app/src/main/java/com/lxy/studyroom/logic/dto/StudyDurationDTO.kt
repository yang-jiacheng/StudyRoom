package com.lxy.studyroom.logic.dto

import java.io.Serializable

/**
 * 学习时长数据传输对象
 */

data class StudyDurationDTO(val duration: Int, val recordId: Int):Serializable
