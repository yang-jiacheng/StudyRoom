package com.lxy.studyroom.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import cn.hutool.core.util.PhoneUtil
import cn.hutool.core.util.StrUtil
import com.blankj.utilcode.util.KeyboardUtils
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.lxy.studyroom.BaseActivity
import com.lxy.studyroom.R
import com.lxy.studyroom.databinding.ActivityForgetPasswordBinding
import com.lxy.studyroom.extension.toast
import com.lxy.studyroom.logic.Repository
import com.lxy.studyroom.ui.personalcenter.UserViewModel
import com.lxy.studyroom.util.EncryptUtil
import kotlin.concurrent.thread

class ForgetPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityForgetPasswordBinding
    private val userViewModel by lazy { ViewModelProvider(this).get(UserViewModel::class.java) }
    private val tokenViewModel by lazy { ViewModelProvider(this).get(TokenViewModel::class.java) }
    private var timer: CountDownTimer? = null

    companion object {
        @JvmStatic
        fun actionStart(context: Context){
            val intent = Intent(context, ForgetPasswordActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        //用户手机号
        val userPhone = Repository.getUserPhone()
        //设置用户手机号到输入框
        if (StrUtil.isNotEmpty(userPhone)){
            binding.changePassPhone.setText(userPhone)
        }

        //监听事件
        setListener()
        //观察数据
        observeVerCode()
        observeUpdatePassword()
    }

    override fun onBackPressed() {
        finish()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListener() {
        //标题栏监听
        binding.changePassTitleBar.setOnTitleBarListener(object :OnTitleBarListener{
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })
        //监听手机号输入框
        textChanges(binding.changePassPhone,500){phone: String? ->
            binding.changePassPhoneLayout.error = ""
            if (!PhoneUtil.isMobile(phone)){
                binding.changePassPhoneLayout.error = "请输入正确格式的手机号"
            }
        }
        //监听验证码输入框
        textChanges(binding.changePassVerifyCode,500){code: String ->
            binding.changePassVerifyCodeLayout.error = ""
            if (code.length != 6){
                binding.changePassVerifyCodeLayout.error = "请输入6位数字验证码"
            }
        }
        //监听密码输入框
        textChanges(binding.changePassPassword,500){password: String ->
            binding.changePassPasswordLayout.error = ""
            if (StrUtil.isBlank(password)){
                binding.changePassPasswordLayout.error = "请输入密码"
            }else{
                if (password.length !in 6..16){
                    binding.changePassPasswordLayout.error = "密码长度在6-16位之间"
                }
            }
        }

        //获取验证码
        binding.changePassGetCode.setOnClickListener {
            getVerifyCode()
        }

        binding.changePassBtn.setOnClickListener {
            savePassword()
        }
    }

    private fun savePassword() {
        val phone = binding.changePassPhone.text.toString()
        val verifyCode = binding.changePassVerifyCode.text.toString()
        var password = binding.changePassPassword.text.toString()

        if (!PhoneUtil.isMobile(phone)){
            val msg = "请输入正确格式的手机号"
            binding.changePassPhoneLayout.error = msg
            msg.toast()
            return
        }

        if (verifyCode.length != 6){
            val msg = "请输入6位数字验证码"
            binding.changePassVerifyCodeLayout.error = msg
            msg.toast()
            return
        }

        if (StrUtil.isBlank(password)){
            val msg = "请输入密码"
            binding.changePassPasswordLayout.error = msg
            msg.toast()
            return
        }
        if (password.length !in 6..16){
            val msg = "密码长度在6-16位之间"
            binding.changePassPasswordLayout.error = msg
            msg.toast()
            return
        }
        //密码SHA-256加密
        password = EncryptUtil.encryptSha256(password)
        KeyboardUtils.hideSoftInput(this)
        userViewModel.updatePassword(phone,verifyCode,password)
    }

    private fun getVerifyCode() {
        val phone = binding.changePassPhone.text.toString()
        if (StrUtil.isBlank(phone)){
            val msg = "请输入手机号"
            binding.changePassPhoneLayout.error = msg
            msg.toast()
            return
        }
        if (!PhoneUtil.isMobile(phone)){
            val msg = "请输入正确格式的手机号"
            binding.changePassPhoneLayout.error = msg
            msg.toast()
            return
        }
        //隐藏输入法
        KeyboardUtils.hideSoftInput(this)
        binding.changePassGetCode.isEnabled = false
        //此处应该将手机号 进行 3DES加密传给后端，后续补上
        tokenViewModel.getVerificationCode(phone)
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
                resp.msg!!.toast()
                binding.changePassGetCode.isEnabled = true
            }
        }
    }

    private fun observeUpdatePassword(){
        userViewModel.upPasswordResponse.observe(this) {resp ->
            if (resp.isSuccess()){
                "修改成功,请重新登录".toast()
                Repository.saveUserLoginStatus(false)
                Repository.saveToken("")
                thread {
                    Thread.sleep(1000)
                    runOnUiThread{
                        LoginPasswordActivity.actionStart(this@ForgetPasswordActivity)
                        finish()
                    }
                }
            }else{
                resp.msg!!.toast(Toast.LENGTH_LONG)
            }
        }
    }

    /**
     * 验证码计时器
     */
    private fun startCountDown(){
        timer = object : CountDownTimer(60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.changePassGetCode.text = StrUtil.format("{}秒后重新获取", millisUntilFinished / 1000)
            }

            override fun onFinish() {
                binding.changePassGetCode.isEnabled = true
                binding.changePassGetCode.text = "重新获取"
            }
        }
        binding.changePassGetCode.isEnabled = false
        timer?.start()
    }
}