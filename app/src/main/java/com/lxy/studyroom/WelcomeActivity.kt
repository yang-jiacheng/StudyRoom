package com.lxy.studyroom

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import com.lxy.studyroom.logic.Repository
import com.lxy.studyroom.ui.login.LoginActivity
import com.lxy.studyroom.ui.main.MainActivity
import com.lxy.studyroom.util.LogUtil
import com.lxy.studyroom.util.StatusBarUtil

/**
 * 欢迎页
 */
class WelcomeActivity : BaseActivity() {

    private var mHandler: Handler =getHandler { actionActivity() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setStatusBarTextColor(this, Color.TRANSPARENT)
        setContentView(R.layout.activity_welcome)
        //1.2秒后执行actionActivity
        mHandler.sendEmptyMessageDelayed(1,1200)

    }

    private fun actionActivity(){
        //根据登录状态判断去主页还是登录页
        val loginStatus = Repository.getUserLoginStatus()
        LogUtil.e("WelcomeActivity loginStatus",loginStatus.toString())
        if (loginStatus){
            MainActivity.actionStart(this)
        }else{
            LoginActivity.actionStart(this)
        }
        finish()
    }

}