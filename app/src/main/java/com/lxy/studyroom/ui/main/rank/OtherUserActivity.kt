package com.lxy.studyroom.ui.main.rank

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import cn.hutool.core.util.StrUtil
import com.bumptech.glide.Glide
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.lxy.studyroom.BaseActivity
import com.lxy.studyroom.R
import com.lxy.studyroom.constant.CommonConstant
import com.lxy.studyroom.databinding.ActivityOtherUserBinding
import com.lxy.studyroom.extension.toast
import com.lxy.studyroom.logic.model.User
import com.lxy.studyroom.ui.personalcenter.UserViewModel
import com.lxy.studyroom.util.StatusBarUtil
import kotlin.concurrent.thread

/**
 * 用户资料页
 */

class OtherUserActivity : BaseActivity() {

    private lateinit var binding: ActivityOtherUserBinding
    private val userViewModel by lazy { ViewModelProvider(this).get(UserViewModel::class.java) }
    private lateinit var user: User

    companion object {
        @JvmStatic
        fun actionStart(context: Context,userId: Int){
            val intent = Intent(context, OtherUserActivity::class.java).apply {
                putExtra("user_id",userId)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //沉浸式状态栏setContentView之前调用
        StatusBarUtil.immersiveStatusBar(this, Color.TRANSPARENT)
        binding = ActivityOtherUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        //获取上个页面传来的用户id
        val userId = intent.getIntExtra("user_id", -1)
        //根据用户id获取用户信息
        userViewModel.getUserInfoById(userId)
        //观察数据
        userViewModel.userByIdResponse.observe(this){resp ->
            if (resp.isSuccess()){

                user = resp.data!!
                val profilePath = user.profilePath
                val coverPath = user.coverPath
                val address = user.address ?: "未设置地址"

                if (StrUtil.isEmpty(coverPath)){
                    binding.otherCover.setImageResource(R.drawable.def_cover)
                }else{
                    Glide.with(this).load(coverPath).into(binding.otherCover)
                }

                if (StrUtil.isEmpty(profilePath)){
                    binding.otherIcon.setImageResource(R.drawable.def_path)
                }else{
                    Glide.with(this).load(profilePath).into(binding.otherIcon)
                }

                binding.otherName.text = user.name
                binding.otherPhone.text = user.phone
                binding.otherAddress.text = address
                binding.otherGender.text = user.gender
                val time = "${user.createTime.substring(0,10)} 加入"
                binding.otherCreateTime.text = time

            }else{
                "获取用户信息失败，请重试".toast()
            }
            binding.otherRefresh.isRefreshing = false
        }

        //设置刷新组件的颜色
        binding.otherRefresh.setColorSchemeResources(R.color.colorPrimary)
        //下拉刷新监听
        binding.otherRefresh.setOnRefreshListener{
            thread{
                //线程睡眠1秒给用户加载数据的视觉体验
                Thread.sleep(1000)
                //Android是不允许在子线程中进行UI操作的,这里返回UI线程请求用户信息
                runOnUiThread{
                    userViewModel.getUserInfoById(userId)
                }
            }
        }

        //TitleBar点击事件
        binding.otherTitleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                onBackPressed()
            }
        })

        //头像点击事件
        binding.otherCover.setOnClickListener {
            //判断user初始化
            if (!::user.isInitialized){
                return@setOnClickListener
            }
            val cover =
            if (StrUtil.isEmpty(user.coverPath)){
                CommonConstant.def_cover
            }else{
                user.coverPath!!
            }
            //查看大图
            getImagePreview(cover)
        }

        binding.otherIcon.setOnClickListener {
            if (!::user.isInitialized){
                return@setOnClickListener
            }
            val icon =
            if (StrUtil.isEmpty(user.profilePath)){
                CommonConstant.def_icon
            }else{
                user.profilePath
            }
            getImagePreview(icon)
        }
    }
}