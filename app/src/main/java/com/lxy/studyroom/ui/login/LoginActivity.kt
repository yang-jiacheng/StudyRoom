package com.lxy.studyroom.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import androidx.lifecycle.ViewModelProvider
import cn.hutool.core.util.PhoneUtil
import cn.hutool.core.util.StrUtil
import com.blankj.utilcode.util.KeyboardUtils
import com.lxy.studyroom.ActivityCollector
import com.lxy.studyroom.BaseActivity
import com.lxy.studyroom.R
import com.lxy.studyroom.constant.CommonConstant
import com.lxy.studyroom.enums.ResponseEnum
import com.lxy.studyroom.extension.toast
import com.lxy.studyroom.logic.Repository
import com.lxy.studyroom.ui.main.MainActivity
import com.lxy.studyroom.ui.policy.PolicyWebViewActivity
import com.lxy.studyroom.util.LogUtil
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    private  var timer: CountDownTimer? = null

    private var isExit = false;

    private var mHandler: Handler = getHandler { isExit = false }

    private val tokenViewModel by lazy { ViewModelProvider(this).get(TokenViewModel::class.java) }

    companion object {
        @JvmStatic
        fun actionStart(context: Context){
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //用户协议是否已选中
        val agreePolicy = Repository.getAgreePolicy()
        //用户手机号
        val userPhone = Repository.getUserPhone()
        setContentView(R.layout.activity_login)


        if (StrUtil.isNotEmpty(userPhone)){
            etMobile.setText(userPhone)
        }
        checkboxVer.isChecked = agreePolicy
        //监听事件
        setListener()
        //观察数据事件
        observeVerCode()
        observeVerCodeLogin()
    }

    override fun onStart() {
        super.onStart()
        setTextColor(tvUserProtocol,R.color.colorPrimary)
        setTextColor(tvUserPolicy,R.color.colorPrimary)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    override fun onBackPressed() {
        LogUtil.w("LoginActivity","点击了back,isExit is $isExit")
        if (isExit){
            ActivityCollector.finishAll()
        }else{
            isExit = true
            BACK_MSG.toast()
            mHandler.sendEmptyMessageDelayed(0,2000)
        }
    }

    /**
     * 监听事件
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setListener(){
        //监听手机号输入框
        textChanges(etMobile,500){phone: String? ->
            accountLayout.error = ""
            if (!PhoneUtil.isMobile(phone)){
                accountLayout.error = "请输入正确格式的手机号"
            }
            //changeLoginBtnStatus()
        }
        //监听验证码输入框
        textChanges(etVerifyCode,500){code: String ->
            verifyCodeLayout.error = ""
            if (code.length != 6){
                verifyCodeLayout.error = "请输入6位数字验证码"
            }
            //changeLoginBtnStatus()
        }
        //获取验证码
        btnGetCode.setOnClickListener {
            getVerifyCode()
        }
        //登录
        btnLogin.setOnClickListener {
            login()
        }
        //密码登录
        tvPasswordLogin.setOnClickListener {
            finish()
            LoginPasswordActivity.actionStart(this)
        }
        //用户协议
        tvUserProtocol.setOnClickListener {
            LogUtil.e("LoginActivity 用户协议",CommonConstant.AGREEMENT)
            setTextColor(tvUserProtocol,R.color.colorTheme)
            PolicyWebViewActivity.actionStart(this,"用户协议",CommonConstant.AGREEMENT)
        }
        //隐私政策
        tvUserPolicy.setOnClickListener {
            LogUtil.e("隐私政策 用户协议",CommonConstant.POLICY)
            setTextColor(tvUserPolicy,R.color.colorTheme)
            PolicyWebViewActivity.actionStart(this,"隐私政策",CommonConstant.POLICY)
        }
//        checkboxVer.setOnCheckedChangeListener { _, _ ->
//            changeLoginBtnStatus()
//        }

    }



    /**
     * 获取验证码
     */
    private fun getVerifyCode(){
        val phone = etMobile.text.toString()
        if (StrUtil.isBlank(phone)){
            val msg = "请输入手机号"
            accountLayout.error = msg
            msg.toast()
            return
        }
        if (!PhoneUtil.isMobile(phone)){
            val msg = "请输入正确格式的手机号"
            accountLayout.error = msg
            msg.toast()
            return
        }
        //隐藏输入法
        KeyboardUtils.hideSoftInput(this)
        btnGetCode.isEnabled = false
        //此处应该将手机号 进行 3DES加密传给后端，后续补上
        tokenViewModel.getVerificationCode(phone)
    }

    /**
     * 登录
     */
    private fun login(){
        val phone = etMobile.text.toString()
        val verifyCode = etVerifyCode.text.toString()
        KeyboardUtils.hideSoftInput(this)
        if (!PhoneUtil.isMobile(phone)){
            val msg = "请输入正确格式的手机号"
            accountLayout.error = msg
            msg.toast()
            return
        }

        if (verifyCode.length != 6){
            val msg = "请输入6位数字验证码"
            verifyCodeLayout.error = msg
            msg.toast()
            return
        }

        if (!checkboxVer.isChecked) {
            "请确认已阅读服务协议与隐私政策".toast()
            return
        }



        tokenViewModel.loginByVerificationCode(phone,verifyCode)
    }

    /**
     * 观察验证码数据
     */
    private fun observeVerCode(){
        tokenViewModel.verCodeResponse.observe(this){ resp ->
            val flag = resp.isSuccess()
            if (flag){
                val msg = "验证码已发送"
                msg.toast()
                startCountDown()
            }else{
                val msg = resp.msg ?: ResponseEnum.DATA_ERROR.msg
                msg.toast()
                btnGetCode.isEnabled = true
            }
        }
    }

    /**
     * 观察验证码登录数据
     */
    private fun observeVerCodeLogin(){
        tokenViewModel.codeLoginResponse.observe(this) { resp ->
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

    /**
     * 改变登录按钮状态
     */
    private fun changeLoginBtnStatus(){
        val phone = etMobile.text.toString()
        val code = etVerifyCode.text.toString()
        val enable = PhoneUtil.isMobile(phone) && code.length == 6 && checkboxVer.isChecked
        btnLogin.isEnabled = enable
    }

    /**
     * 验证码计时器
     */
    private fun startCountDown(){
        timer = object : CountDownTimer(60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                btnGetCode.text = StrUtil.format("{}秒后重新获取", millisUntilFinished / 1000)
            }

            override fun onFinish() {
                btnGetCode.isEnabled = true
                btnGetCode.text = "重新获取"
            }
        }
        btnGetCode.isEnabled = false
        timer?.start()
    }


}