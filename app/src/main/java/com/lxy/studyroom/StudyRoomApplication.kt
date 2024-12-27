package com.lxy.studyroom

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class StudyRoomApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    //style="@style/Widget.MaterialComponents.Button.OutlinedButton"
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}