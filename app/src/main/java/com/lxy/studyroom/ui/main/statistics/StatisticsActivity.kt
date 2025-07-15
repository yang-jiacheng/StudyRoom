package com.lxy.studyroom.ui.main.statistics

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.StrUtil
import com.bumptech.glide.Glide
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.lxy.studyroom.BaseActivity
import com.lxy.studyroom.R
import com.lxy.studyroom.constant.CommonConstant
import com.lxy.studyroom.databinding.ActivityStatisticsBinding
import com.lxy.studyroom.extension.toast
import com.lxy.studyroom.logic.model.StudyRecord
import com.lxy.studyroom.logic.model.User
import com.lxy.studyroom.logic.model.UserRank
import com.lxy.studyroom.ui.main.rank.RankViewModel
import com.lxy.studyroom.ui.room.StudyRecordViewModel
import com.lxy.studyroom.util.CommonUtil
import com.lxy.studyroom.util.StatusBarUtil
import kotlin.concurrent.thread

class StatisticsActivity : BaseActivity() {

    private lateinit var binding: ActivityStatisticsBinding
    private val studyRecordViewModel by lazy { ViewModelProvider(this).get(StudyRecordViewModel::class.java) }
    private lateinit var user: UserRank
    private var records = ArrayList<StudyRecord>()

    companion object {
        @JvmStatic
        fun actionStart(context: Context){
            val intent = Intent(context, StatisticsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //沉浸式状态栏
        StatusBarUtil.immersiveStatusBar(this, Color.TRANSPARENT)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showDianDianLoading()
        //下拉刷新
        binding.stSyRefresh.setColorSchemeResources(R.color.colorPrimary)
        binding.stSyRefresh.setOnRefreshListener{
            thread{
                Thread.sleep(1000)
                runOnUiThread{
                    studyRecordViewModel.getUserRecordsAndStatistics(1,3)
                }
            }
        }

        //RecyclerView
        binding.rvRecord.layoutManager = LinearLayoutManager(this)
        val adapter = UserRecordAdapter(this,records)
        binding.rvRecord.adapter = adapter

        //获取数据
        studyRecordViewModel.getUserRecordsAndStatistics(1,3)
        //监听事件
        setEvent()
        //观察数据
        observeData(adapter)
    }

    private fun setEvent() {
        //标题栏点击事件
        binding.stSyTitleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                onBackPressed()
            }
        })
        //更多记录
        binding.stSyMoreRecord.setOnClickListener {
            AllRecordActivity.actionStart(this)
        }
        //查看大图
        binding.stSyMineIcon.setOnClickListener {
            val icon =
                if (StrUtil.isEmpty(user.profilePath)){
                    CommonConstant.def_icon
                }else{
                    user.profilePath!!
                }
            getImagePreview(icon)
        }
    }

    private fun observeData(adapter: UserRecordAdapter){
        studyRecordViewModel.rankAndStatisticsResponse.observe(this){resp ->
            records.clear()
            CommonUtil.handleResp(resp,
                {successFun ->
                    val result = successFun.data!!
                    user = result.userRank
                    if (CollUtil.isNotEmpty(result.records)){
                        records.addAll(result.records!!)
                    }
                    //头像
                    val profilePath = user.profilePath
                    if (StrUtil.isNotEmpty(profilePath)){
                        Glide.with(this@StatisticsActivity).load(profilePath).into(binding.stSyMineIcon)
                    }
                    //今日学习时长
                    if (user.todayDuration != null){
                        val todayDuration = "${user.todayDuration}分钟"
                        binding.stSyToday.text = todayDuration
                    }
                    //总学习时长
                    val totalDuration = "${user.totalDuration}分钟"
                    binding.stSyAllDuration.text = totalDuration
                    //排名
                    binding.stSyRank.text = user.ranking.toString()

                },
                {errorFun ->
                    errorFun.msg!!.toast()
                },
            )
            adapter.notifyItemRangeChanged(0,records.size)
            binding.stSyRefresh.isRefreshing = false
            destroyDianDianLoading()
        }
    }
}