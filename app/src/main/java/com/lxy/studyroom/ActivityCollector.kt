package com.lxy.studyroom

import android.app.Activity

/**
 * 管理Activity的容器
 */
object ActivityCollector {
    private val activities = ArrayList<Activity>()

    fun addActivity(activity: Activity) {
        activities.add(activity)
    }
    fun removeActivity(activity: Activity) {
        activities.remove(activity)
    }
    fun finishAll() {
        for (activity in activities) {
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
        activities.clear()
        //exitProcess(0)
        //杀死当前进程
        //android.os.Process.killProcess(android.os.Process.myPid())
    }


}