package com.lxy.studyroom.ui.personalcenter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.AppUtils
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.lxy.studyroom.BaseActivity
import com.lxy.studyroom.R
import com.lxy.studyroom.constant.CommonConstant
import com.lxy.studyroom.databinding.ActivityMySettingsBinding
import com.lxy.studyroom.extension.toast
import com.lxy.studyroom.logic.Repository
import com.lxy.studyroom.ui.login.ForgetPasswordActivity
import com.lxy.studyroom.ui.login.LoginActivity
import com.lxy.studyroom.ui.login.TokenViewModel
import com.lxy.studyroom.ui.policy.PolicyWebViewActivity
import com.lxy.studyroom.util.CommonUtil
import com.lxy.studyroom.util.LocalCacheUtil

class MySettingsActivity : BaseActivity() {

    private lateinit var binding: ActivityMySettingsBinding
    private lateinit var cache: LocalCacheUtil
    private val tokenViewModel by lazy { ViewModelProvider(this).get(TokenViewModel::class.java) }

    companion object {
        @JvmStatic
        fun actionStart(context: Context){
            val intent = Intent(context, MySettingsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        //缓存大小
        cache = LocalCacheUtil(this@MySettingsActivity)
        binding.mineSetCacheSize.text = cache.getCacheSize()
        //版本号
        val version = "v${AppUtils.getAppVersionName()}"
        binding.mineSetVersionText.text = version
        //监听事件
        setListener()

        tokenViewModel.loginOutResponse.observe(this){resp ->
            if (resp.isSuccess()){
                Repository.saveUserLoginStatus(false)
                Repository.saveToken("")
                LoginActivity.actionStart(this@MySettingsActivity)
                finish()
            }else{
                resp.msg!!.toast()
            }
        }
    }

    private fun setListener() {
        //TitleBar点击事件
        binding.mineSetTitleBar.setOnTitleBarListener(object : OnTitleBarListener{
            override fun onLeftClick(titleBar: TitleBar?) {
                onBackPressed()
            }
        })
        //修改手机号
        binding.mineSetUpdatePhone.setOnClickListener {
            "暂未开放此功能".toast()
        }
        //修改密码
        binding.mineSetUpdatePassword.setOnClickListener {
            ForgetPasswordActivity.actionStart(this@MySettingsActivity)
        }
        //用户协议
        binding.mineSetAgreement.setOnClickListener {
            PolicyWebViewActivity.actionStart(this,"用户协议", CommonConstant.AGREEMENT)
        }
        //隐私政策
        binding.mineSetPolicy.setOnClickListener {
            PolicyWebViewActivity.actionStart(this,"隐私政策", CommonConstant.POLICY)
        }
        //清除缓存
        binding.mineSetClearCache.setOnClickListener {
            if (::cache.isInitialized){
                cache.clearAppCache()
                binding.mineSetCacheSize.text = cache.getCacheSize()
                "缓存已清除".toast()
            }
        }
        //注销账号
        binding.mineSetWriteOff.setOnClickListener {
            "暂未开放此功能".toast()
        }
        //退出登录
        binding.mineSetLogoutBtn.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("提示")
                setMessage("确认退出登录？")
                setCancelable(false)
                setPositiveButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
                setNegativeButton("确定") { dialog, _ ->
                    tokenViewModel.logout(CommonUtil.getRandomCode())
                    dialog.dismiss()
                }
                show()
            }
        }
    }
}