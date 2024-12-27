package com.lxy.studyroom.ui.room

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import cn.hutool.core.date.DatePattern
import cn.hutool.core.date.DateUtil
import com.blankj.utilcode.util.KeyboardUtils
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.lxy.studyroom.BaseActivity
import com.lxy.studyroom.R
import com.lxy.studyroom.extension.toast
import com.lxy.studyroom.logic.dto.StudyDurationDTO
import com.lxy.studyroom.logic.model.StudyRecord
import com.lxy.studyroom.ui.main.rank.OtherUserActivity
import com.lxy.studyroom.util.CommonUtil
import com.lxy.studyroom.util.LogUtil
import com.lxy.studyroom.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_study.*
import java.util.*
import kotlin.concurrent.thread

class StudyActivity : BaseActivity() {

    private val roomViewModel by lazy { ViewModelProvider(this).get(LibraryRoomViewModel::class.java) }

    private var timer: CountDownTimer? = null

    private lateinit var record: StudyRecord

    private var recordId: Int = 0

    private lateinit var roomName: String

    private var flag = false

    private var lightFlag = false

    private var curr: Int = 0

    companion object {
        @JvmStatic
        fun actionStart(context: Context, recordId: Int,roomName: String,flag: Boolean){
            val intent = Intent(context, StudyActivity::class.java).apply {
                putExtra("record_id", recordId)
                putExtra("room_name",roomName)
                putExtra("flag",flag)
            }
            context.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.immersiveStatusBar(this, Color.TRANSPARENT)
        setContentView(R.layout.activity_study)
        showDianDianLoading()
        recordId = intent.getIntExtra("record_id",0)
        roomName = intent.getStringExtra("room_name").toString()
        flag = intent.getBooleanExtra("flag",false)
        //获取记录详情
        roomViewModel.getLearningRecordDetail(recordId)
        //正计时格式
        acStChronometer.format = "%s"
        //监听事件
        setEvent()

        observeData()
        observeStopStudy()
        observeDuration()
    }

    override fun onBackPressed() {
        if (flag){
            stopStudy()
        }else{
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        acStChronometer.start()
    }

    override fun onPause() {
        super.onPause()
        acStChronometer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()

        KeyboardUtils.unregisterSoftInputChangedListener(window)
    }

    private fun observeData(){
        roomViewModel.recordDetailResponse.observe(this){resp ->
            if (resp.isSuccess()){
                record = resp.data!!
                handleData()
            }else{
                resp.msg!!.toast()
            }
            destroyDianDianLoading()
        }
    }

    private fun observeStopStudy(){
        roomViewModel.stopStudyResponse.observe(this){resp ->
            if (resp.isSuccess()){
                val duration = resp.data!!
                //提交学习时长
                roomViewModel.submitStudyDuration(StudyDurationDTO(duration,recordId))
                //编辑笔记页面
                EditNoteActivity.actionStart(this@StudyActivity,recordId)
                finish()
            }
        }
    }

    private fun observeDuration(){
        roomViewModel.submitStudyResponse.observe(this){resp ->
            LogUtil.e("submitStudyDuration",resp.toString())
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setEvent() {
        acStTitleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                if (flag){
                    stopStudy()
                }else{
                    finish()
                }
            }

            override fun onRightClick(titleBar: TitleBar?) {
                if (flag){
                    //开启了屏幕常亮
                    if (lightFlag){
                        acStTitleBar.rightIcon = resources.getDrawable(R.drawable.ic_lighting_no)
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }else{
                        acStTitleBar.rightIcon = resources.getDrawable(R.drawable.ic_lighting)
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        "屏幕常亮已开启".toast()
                    }
                    lightFlag = !lightFlag
                }
            }
        })

        //
        acStStudyPerson.setOnClickListener {
            if (::record.isInitialized){
                OtherUserActivity.actionStart(this@StudyActivity,record.userId)
            }
        }
    }

    private fun stopStudy(){
        AlertDialog.Builder(this@StudyActivity).apply {
            setTitle("提示")
            setMessage("确认结束自习？")
            setCancelable(false)
            setPositiveButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            setNegativeButton("确定") { dialog, _ ->
                roomViewModel.stopStudy(recordId)
                dialog.dismiss()
            }
            show()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun handleData(){
        //座位号
        val seat = record.seat
        acStTitleBar.title = "${roomName}-${seat}号"
        //标签
        acStTag.text = record.tag
        if(flag){
            acStTitleBar.rightIcon = resources.getDrawable(R.drawable.ic_lighting_no)
            //音乐盒展示
            acStMusic.visibility = View.VISIBLE
        }
        //开始时间
        val startTime = record.startTime

        //计时方式：1正计时 2倒计时
        val timingMode = record.timingMode
        if (timingMode == 1){
            acStChronometer.visibility = View.VISIBLE
            //初始化正计时
            initTimer()
        }else{
            acStTimeCount.visibility = View.VISIBLE
            acStTimeCountLayoutTwo.visibility = View.VISIBLE
            acStImgLine1.visibility = View.VISIBLE
            acStImgLine2.visibility = View.VISIBLE
            val duration = "自习时长：${record.settingDuration}分钟"
            acStTimeCountTwo.text = duration
            //倒计时
            startCountDown()
        }


    }

    //倒计时
    private fun startCountDown(){
        val startTime = DateUtil.parse(record.startTime,DatePattern.NORM_DATETIME_PATTERN)
        val now = Date()
        val duration = (record.settingDuration!! * 60 * 1000 ) - (now.time - startTime.time)
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                acStTimeCount.text = CommonUtil.formatMiss(millisUntilFinished / 1000)
            }

            override fun onFinish() {
                if (flag){
                    "自习时间已到".toast(Toast.LENGTH_LONG)
                    thread{
                        Thread.sleep(1000)
                        runOnUiThread{
                            roomViewModel.stopStudy(recordId)
                        }
                    }
                }else{
                    finish()
                }
            }
        }
        timer?.start()
    }

    //正计时
    private fun initTimer() {
        val seconds = getBaseTime()
        acStChronometer.base = seconds
        acStChronometer.setOnChronometerTickListener {
            setTimeClockFormat(seconds)
        }
    }

    private fun getBaseTime(): Long{
        val startTime = DateUtil.parse(record.startTime,DatePattern.NORM_DATETIME_PATTERN)
        val now = Date()
        return SystemClock.elapsedRealtime() - (now.time - startTime.time)
    }

    private fun setTimeClockFormat(seconds: Long) {
        if (seconds >= 35999) {
            acStChronometer.format = "%s"
        } else if (seconds >= 3599) {
            acStChronometer.format = "0%s"
        }
    }

}