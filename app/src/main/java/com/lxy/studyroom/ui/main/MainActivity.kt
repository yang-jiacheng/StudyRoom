package com.lxy.studyroom.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.StrUtil
import com.bumptech.glide.Glide
import com.luck.picture.lib.utils.SdkVersionUtils.TIRAMISU
import com.lxy.studyroom.ActivityCollector
import com.lxy.studyroom.BaseActivity
import com.lxy.studyroom.BuildConfig
import com.lxy.studyroom.R
import com.lxy.studyroom.enums.ResponseEnum
import com.lxy.studyroom.extension.toast
import com.lxy.studyroom.logic.Repository
import com.lxy.studyroom.logic.model.Library
import com.lxy.studyroom.logic.model.User
import com.lxy.studyroom.ui.login.LoginActivity
import com.lxy.studyroom.ui.main.rank.TotalRankActivity
import com.lxy.studyroom.ui.main.statistics.StatisticsActivity
import com.lxy.studyroom.ui.personalcenter.PersonalActivity
import com.lxy.studyroom.ui.personalcenter.UserViewModel
import com.lxy.studyroom.util.CommonUtil
import com.lxy.studyroom.util.LogUtil
import com.lxy.studyroom.util.StatusBarUtil
import com.lxy.studyroom.util.UpdateUtil
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 主页
 */
class MainActivity : BaseActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(LibraryViewModel::class.java) }

    private val userViewModel by lazy { ViewModelProvider(this).get(UserViewModel::class.java) }

    private val versionViewModel by lazy { ViewModelProvider(this).get(VersionViewModel::class.java) }

    private var isExit = false;

    private var mHandler: Handler =getHandler { isExit = false }

    private var isFirst = true

    //图书馆数据
    private val libraryList = ArrayList<Library>()

    //用户信息
    private lateinit var userInfo: User

    private var updateUtil: UpdateUtil? = null

    companion object {
        @JvmStatic
        fun actionStart(context: Context){
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //沉浸式状态栏setContentView之前调用
        StatusBarUtil.immersiveStatusBar(this, Color.TRANSPARENT)
        setContentView(R.layout.activity_main)
        updateUtil = UpdateUtil(this)
        //设置默认头像
        Glide.with(this).load(R.drawable.def_path).into(allUserPro)

        //加载中AlertDialog
        showDianDianLoading()
        //监听事件
        setEvent()

        //RecyclerView
        allLibrary.layoutManager = LinearLayoutManager(this)
        val adapter = LibraryAdapter(this,libraryList)
        allLibrary.adapter = adapter

        viewModel.updateRecordToFinish(CommonUtil.getRandomCode())
        versionViewModel.checkVersion(CommonUtil.getRandomCode())
        //观察数据变化
        observeLibraryData(adapter)
        observeUserData()
        observeFinishData()
        observeVersionData()


    }

    override fun onStart() {
        super.onStart()
        //请求用户信息
        userViewModel.getUserInfo(CommonUtil.getRandomCode())
        //请求图书馆数据
        viewModel.getAllLibrary(CommonUtil.getRandomCode())
    }

    override fun onResume() {
        super.onResume()
        if (isFirst) {
            isFirst = false
        } else {
            updateUtil?.recheck()
        }
    }

    override fun onBackPressed() {
        LogUtil.w("MainActivity","点击了back,isExit is $isExit")
        //2秒内按两次返回才退出APP
        if (isExit){
            ActivityCollector.finishAll()
        }else{
            isExit = true
            BACK_MSG.toast()
            mHandler.sendEmptyMessageDelayed(0,2000)
        }
    }

    private fun setEvent() {
        //头像点击跳转
        allUserPro.setOnClickListener {
            //判断userInfo是否已初始化
            if(!::userInfo.isInitialized){
                "用户信息还未获取到".toast()
            }else{
                PersonalActivity.actionStart(this,userInfo)
            }
        }

        //排行榜Activity
        imgStudyRank.setOnClickListener {
            TotalRankActivity.actionStart(this)
        }
        //统计
        imgStudyDiary.setOnClickListener {
            StatisticsActivity.actionStart(this)
        }
    }


    /**
     * 观察图书馆数据变化
     */
    private fun observeLibraryData(adapter : LibraryAdapter){
        viewModel.libraryResponse.observe(this){ resp ->
            libraryList.clear()
            val flag = resp.isSuccess()
            if (flag){
                //处理图书馆数据
                val list = resp.data
                if (CollUtil.isNotEmpty(list)) {
                    libraryList.addAll(list!!)
                    //true左边，false右边
                    var onLeft = true
                    for (library in libraryList) {
                        library.flag = onLeft
                        onLeft = !onLeft
                    }
                }
            }else{
                //服务端报错
                val msg = resp.msg ?: ResponseEnum.DATA_ERROR.msg
                msg.toast(Toast.LENGTH_LONG)
//                //需要登录
//                if (resp.code == ResponseEnum.NEED_LOGIN.code){
//                    Repository.saveUserLoginStatus(false)
//                    LoginActivity.actionStart(this)
//                    finish()
//                }
            }
            //通知RecyclerView刷新图书馆数据
            adapter.notifyItemRangeChanged(0, libraryList.size)
            //销毁加载AlertDialog
            destroyDianDianLoading()
        }
    }

    /**
     * 观察用户数据变化
     */
    private fun observeUserData(){
        userViewModel.userInfoResponse.observe(this) {resp ->
            val flag = resp.isSuccess()
            if (flag){
                val user = resp.data
                if (user != null){
                    userInfo = user
                    //没设置头像就用默认的
                    val profilePath = userInfo.profilePath
                    if (StrUtil.isEmpty(profilePath)){
                        allUserPro.setImageResource(R.drawable.def_path)
                    }else{
                        Glide.with(this).load(profilePath).into(allUserPro)
                    }
                    //存储手机号和userid
                    Repository.saveUserPhone(userInfo.phone)
                    Repository.saveUserId(userInfo.id)
                }
            }
        }
    }

    private fun observeFinishData(){
        viewModel.finishResponse.observe(this){resp ->

        }
    }

    private fun observeVersionData() {
        versionViewModel.versionResponse.observe(this){resp ->
            LogUtil.e("sss","sdsdsdsds")
            if (resp.isSuccess()){

                updateUtil?.checkUpdate(resp.data!!)
            }
        }
    }


}