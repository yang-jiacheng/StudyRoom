package com.lxy.studyroom.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import cn.hutool.core.util.PhoneUtil
import cn.hutool.core.util.StrUtil
import com.blankj.utilcode.util.KeyboardUtils
import com.lxy.studyroom.BaseActivity
import com.lxy.studyroom.R
import com.lxy.studyroom.constant.CommonConstant
import com.lxy.studyroom.enums.ResponseEnum
import com.lxy.studyroom.extension.toast
import com.lxy.studyroom.logic.Repository
import com.lxy.studyroom.ui.main.MainActivity
import com.lxy.studyroom.ui.policy.PolicyWebViewActivity
import com.lxy.studyroom.util.EncryptUtil
import com.lxy.studyroom.util.LogUtil
import kotlinx.android.synthetic.main.activity_login_password.*

class LoginPasswordActivity : BaseActivity() {

    private val tokenViewModel by lazy { ViewModelProvider(this).get(TokenViewModel::class.java) }

    companion object {
        @JvmStatic
        fun actionStart(context: Context){
            val intent = Intent(context, LoginPasswordActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //用户协议是否已选中
        val agreePolicy = Repository.getAgreePolicy()
        //用户手机号
        val userPhone = Repository.getUserPhone()
        setContentView(R.layout.activity_login_password)
        if (StrUtil.isNotEmpty(userPhone)){
            tpMobile.setText(userPhone)
        }
        checkboxTp.isChecked = agreePolicy
        //监听事件
        setListener()
        //观察数据事件
        observeLoginPassword()
    }

    override fun onStart() {
        super.onStart()
        setTextColor(tpUserProtocol,R.color.colorPrimary)
        setTextColor(tpUserPolicy,R.color.colorPrimary)
    }

    override fun onBackPressed(){
        goBack()
    }

    /**
     * 监听事件
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setListener(){
        //监听手机号输入框
        textChanges(tpMobile,500){phone: String? ->
            accountTpLayout.error = ""
            if (!PhoneUtil.isMobile(phone)){
                accountTpLayout.error = "请输入正确格式的手机号"
            }
        }
        //监听密码输入框
        textChanges(tpPassword,500){password: String ->
            tpPasswordLayout.error = ""
            if (StrUtil.isBlank(password)){
                tpPasswordLayout.error = "请输入密码"
            }else{
                if (password.length !in 6..16){
                    tpPasswordLayout.error = "密码长度在6-16位之间"
                }
            }
        }
        //返回
        tpBack.setOnClickListener {
            goBack()
        }
        //忘记密码
        tpForgetPassword.setOnClickListener {
            ForgetPasswordActivity.actionStart(this)
        }
        //登录
        tpbtnLogin.setOnClickListener {
            loginByPassword()
        }
        //用户协议
        tpUserProtocol.setOnClickListener {
            LogUtil.e("LoginPasswordActivity 用户协议", CommonConstant.AGREEMENT)
            setTextColor(tpUserProtocol,R.color.colorTheme)
            PolicyWebViewActivity.actionStart(this,"用户协议", CommonConstant.AGREEMENT)
        }
        //隐私政策
        tpUserPolicy.setOnClickListener {
            LogUtil.e("LoginPasswordActivity 用户协议", CommonConstant.POLICY)
            setTextColor(tpUserPolicy,R.color.colorTheme)
            PolicyWebViewActivity.actionStart(this,"隐私政策", CommonConstant.POLICY)
        }

    }

    private fun goBack(){
        LoginActivity.actionStart(this)
        finish()
    }

    private fun loginByPassword(){
        val phone = tpMobile.text.toString()
        var password = tpPassword.text.toString()
        KeyboardUtils.hideSoftInput(this)
        if (!PhoneUtil.isMobile(phone)){
            val msg = "请输入正确格式的手机号"
            accountTpLayout.error = msg
            msg.toast()
            return
        }
        if (StrUtil.isBlank(password)){
            val msg = "请输入密码"
            tpPasswordLayout.error = msg
            msg.toast()
            return
        }
        if (password.length !in 6..16){
            val msg = "密码长度在6-16位之间"
            tpPasswordLayout.error = msg
            msg.toast()
            return
        }
        if (!checkboxTp.isChecked) {
            "请确认已阅读服务协议与隐私政策".toast()
            return
        }
        //密码SHA-256加密
        password = EncryptUtil.encryptSha256(password)

        tokenViewModel.login(phone, password)

    }

    private fun observeLoginPassword(){
        tokenViewModel.passLoginResponse.observe(this){ resp ->
            val flag = resp.isSuccess()
            if (flag){
                val token = resp.data
                "登录成功".toast()
                //保存用户协议选择状态、返回的token、登录状态
                Repository.saveAgreePolicy(true)
                Repository.saveUserLoginStatus(true)
                if (StrUtil.isNotEmpty(token)){
                    Repository.saveToken(token!!)
                }

                //跳转首页
                MainActivity.actionStart(this)
                finish()
            }else{
                val msg = resp.msg ?: ResponseEnum.DATA_ERROR.msg
                msg.toast()
            }
        }
    }

}