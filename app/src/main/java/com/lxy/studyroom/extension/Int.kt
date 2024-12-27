package com.lxy.studyroom.extension

import android.widget.Toast
import com.lxy.studyroom.StudyRoomApplication

fun Int.toast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(StudyRoomApplication.context, this.toString(), duration).show()
}