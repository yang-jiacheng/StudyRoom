package com.lxy.studyroom.logic.model

import java.io.Serializable

data class StudyRecord(
    val id: Int,
    val userId: Int,
    val classifyId: Int,
    val catalogId: Int,
    val tag: String,
    val seat: Int,
    //计时方式：1正计时 2倒计时
    val timingMode: Int,
    //设置的自习时长，单位分钟（仅在倒计时有）
    val settingDuration: Int?,
    //实际学习时长，单位分钟
    val actualDuration: Int?,
    //状态（1自习中 2离开 3已完成）
    val status: Int,
    //学习笔记文字
    val noteContent: String?,
    //是否有笔记 0否 1是
    val noteStatus: Int?,
    //学习笔记图片
    val notePath: String?,
    //学习时间
    val startTime: String,
    //用户昵称头像
    val name: String?,
    val profilePath: String?,
    //图书馆名称
    val classifyName: String?,
    //自习室名称
    val catalogName: String?
) : Serializable
