package com.lxy.studyroom.extension

import android.widget.Toast
import com.lxy.studyroom.StudyRoomApplication

/**
 * String 的 扩展函数
 */

fun String.toast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(StudyRoomApplication.context, this, duration).show()
}