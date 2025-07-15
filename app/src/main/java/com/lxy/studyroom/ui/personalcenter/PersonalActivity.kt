package com.lxy.studyroom.ui.personalcenter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import cn.hutool.core.util.StrUtil
import com.bumptech.glide.Glide
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.lxy.studyroom.BaseActivity
import com.lxy.studyroom.R
import com.lxy.studyroom.constant.CommonConstant
import com.lxy.studyroom.databinding.ActivityPersonalBinding
import com.lxy.studyroom.enums.ResponseEnum
import com.lxy.studyroom.extension.toast
import com.lxy.studyroom.logic.model.User
import com.lxy.studyroom.ui.feedback.FeedbackActivity
import com.lxy.studyroom.ui.main.MainActivity
import com.lxy.studyroom.ui.policy.PolicyWebViewActivity
import com.lxy.studyroom.util.CommonUtil
import com.lxy.studyroom.util.LogUtil
import com.lxy.studyroom.util.StatusBarUtil
import kotlin.concurrent.thread

class PersonalActivity : BaseActivity() {

    private lateinit var binding: ActivityPersonalBinding
    val userViewModel by lazy { ViewModelProvider(this).get(UserViewModel::class.java) }
    private lateinit var userInfo: User

    companion object {
        @JvmStatic
        fun actionStart(context: Context,userInfo: User){
            val intent = Intent(context, PersonalActivity::class.java).apply {
                putExtra("user_info",userInfo)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.immersiveStatusBar(this, Color.TRANSPARENT)
        binding = ActivityPersonalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //监听事件
        setListener()

        userInfo = intent.getSerializableExtra("user_info") as User
        //处理用户信息
        handleUserInfo()
        //观察用户数据
        observeUserData()
        //下拉刷新
        binding.mineRefresh.setColorSchemeResources(R.color.colorPrimary)
        binding.mineRefresh.setOnRefreshListener{
            refreshUserInfo()
        }
    }

    override fun onRestart() {
        super.onRestart()
        userViewModel.getUserInfo(CommonUtil.getRandomCode())
    }

    private fun setListener() {
        //TitleBar点击事件
        binding.mineTitleBar.setOnTitleBarListener(object : OnTitleBarListener{
            override fun onLeftClick(titleBar: TitleBar?) {
                onBackPressed()
            }
        })
        //关于软件
        binding.mineAbout.setOnClickListener {
            LogUtil.e("PersonalActivity 关于软件", CommonConstant.ABOUT_US)
            PolicyWebViewActivity.actionStart(this,"关于软件", CommonConstant.ABOUT_US)
        }
        //我的设置
        binding.mineSettings.setOnClickListener {
            MySettingsActivity.actionStart(this)
        }

        binding.mineUpdateBtn.setOnClickListener {
            EditUserInfoActivity.actionStart(this@PersonalActivity,userInfo)
        }

        binding.mineFeedback.setOnClickListener {
            FeedbackActivity.actionStart(this)
        }

        binding.mineCover.setOnClickListener {
            if (!::userInfo.isInitialized){
                return@setOnClickListener
            }
            val cover =
                if (StrUtil.isEmpty(userInfo.coverPath)){
                    CommonConstant.def_cover
                }else{
                    userInfo.coverPath!!
                }
            getImagePreview(cover)
        }

        binding.mineIcon.setOnClickListener {
            if (!::userInfo.isInitialized){
                return@setOnClickListener
            }
            val icon =
                if (StrUtil.isEmpty(userInfo.profilePath)){
                    CommonConstant.def_icon
                }else{
                    userInfo.profilePath
                }
            getImagePreview(icon)
        }
    }

    private fun observeUserData(){
        userViewModel.userInfoResponse.observe(this){ resp ->
            val flag = resp.isSuccess()
            if (flag){
                val user = resp.data
                if (user != null){
                    userInfo = user
                    handleUserInfo()
                }
            }else{
                val msg = resp.msg ?: ResponseEnum.DATA_ERROR.msg
                msg.toast(Toast.LENGTH_LONG)
            }
            binding.mineRefresh.isRefreshing = false
        }
    }

    private fun handleUserInfo(){
        val coverPath = userInfo.coverPath
        val profilePath = userInfo.profilePath
        val name = userInfo.name
        val phone = userInfo.phone
        var address = userInfo.address
        val gender = userInfo.gender

        if (StrUtil.isEmpty(coverPath)){
            binding.mineCover.setImageResource(R.drawable.def_cover)
        }else{
            Glide.with(this).load(coverPath).into(binding.mineCover)
        }

        if (StrUtil.isEmpty(profilePath)){
            binding.mineIcon.setImageResource(R.drawable.def_path)
        }else{
            Glide.with(this).load(profilePath).into(binding.mineIcon)
        }
        binding.mineName.text = name
        binding.minePhone.text = phone

        if (StrUtil.isEmpty(address)){
            address = "未设置地址"
        }
        binding.mineAddress.text = address

        binding.mineGender.text = gender

        val createTime = "${userInfo.createTime.substring(0,10)} 加入"
        binding.mineCreateTime.text = createTime
    }

    private fun refreshUserInfo(){
        thread{
            Thread.sleep(1000)
            runOnUiThread{
                userViewModel.getUserInfo(CommonUtil.getRandomCode())
            }
        }
    }
}