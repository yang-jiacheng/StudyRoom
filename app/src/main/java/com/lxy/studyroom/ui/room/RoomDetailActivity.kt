package com.lxy.studyroom.ui.room

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.StrUtil
import com.blankj.utilcode.util.KeyboardUtils
import com.lxy.studyroom.BaseActivity
import com.lxy.studyroom.R
import com.lxy.studyroom.databinding.ActivityRoomDetailBinding
import com.lxy.studyroom.databinding.DialogStartStudyBinding
import com.lxy.studyroom.extension.toast
import com.lxy.studyroom.logic.dto.StudyRecordDTO
import com.lxy.studyroom.logic.model.RoomRecord
import com.lxy.studyroom.logic.model.StudyRecord
import com.lxy.studyroom.util.LogUtil

class RoomDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityRoomDetailBinding
    private val roomViewModel by lazy { ViewModelProvider(this).get(LibraryRoomViewModel::class.java) }
    private lateinit var roomRecord: RoomRecord
    private lateinit var roomName: String
    private var roomId: Int = 0
    private var libraryId: Int = 0
    private val records = ArrayList<StudyRecord>()
    private lateinit var detailDialog: AlertDialog

    companion object {
        @JvmStatic
        fun actionStart(context: Context, roomId: Int,classifyId: Int){
            val intent = Intent(context, RoomDetailActivity::class.java).apply {
                putExtra("room_id", roomId)
                putExtra("library_id", classifyId)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        roomId = intent.getIntExtra("room_id",0)
        libraryId = intent.getIntExtra("library_id",0)

        //加载
        showDianDianLoading()
        //RecyclerView
        binding.roomDetRecyclerView.layoutManager = GridLayoutManager(this,2)
        val adapter = StudyRecordAdapter(this, records)
        binding.roomDetRecyclerView.adapter = adapter
        //RecyclerView分割线
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        decoration.setDrawable(ContextCompat.getDrawable(this,R.drawable.ic_h_line)!!)
        val decoration2 = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        decoration2.setDrawable(ContextCompat.getDrawable(this,R.drawable.ic_v_line)!!)
        binding.roomDetRecyclerView.addItemDecoration(decoration2)
        binding.roomDetRecyclerView.addItemDecoration(decoration)

        //监听事件
        setListener(adapter)
        //获取自习室数据
        roomViewModel.getRoomDetailAndRecords(roomId)
        //观察数据
        observeData(adapter)
        observeStartStudy()
    }

    override fun onRestart() {
        super.onRestart()
        roomViewModel.getRoomDetailAndRecords(roomId)
    }

    private fun setListener(adapter: StudyRecordAdapter) {
        binding.roomDetBack.setOnClickListener {
            onBackPressed()
        }
        //RecyclerView点击事件
        adapter.setOnItemClickListener(object :StudyRecordAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val record = adapter.getCurrItem(position)
                if (StrUtil.isEmpty(record.name)){
                    //空位
                    showLibraryDetail(record)
                }else{
                    //有人在
                    StudyActivity.actionStart(this@RoomDetailActivity,record.id,roomName,false)
                }
            }
        })
    }

    private fun observeData(adapter: StudyRecordAdapter){
        roomViewModel.roomRecordResponse.observe(this) {resp ->
            if (resp.isSuccess()){
                roomRecord = resp.data!!
                handleData(adapter)
            }else{
                resp.msg!!.toast()
            }
            destroyDianDianLoading()
        }
    }

    private fun observeStartStudy(){
        roomViewModel.startStudyResponse.observe(this){resp ->
            if (resp.isSuccess()){
                hideLibraryDetail()
                StudyActivity.actionStart(this@RoomDetailActivity,resp.data!!,roomName,true)
            }else{
                resp.msg!!.toast()
            }
        }
    }

    //处理数据
    private fun handleData(adapter: StudyRecordAdapter) {
        if (!::roomRecord.isInitialized){
            return
        }
        records.clear()

        val room = roomRecord.room
        roomName = room.name
        val title = room.libraryName + room.parentName + room.name
        val count = room.personCount
        binding.roomDetTitle.text = title
        if (CollUtil.isNotEmpty(roomRecord.records)){
            val haveRecords = roomRecord.records!!
            //所有记录的map
            val haveMap = HashMap<Int,StudyRecord>(haveRecords.size)
            for (haveReco in haveRecords){
                haveMap[haveReco.seat] = haveReco
            }
            //根据容纳人数生成
            for (i in 1..count){

                val seatPerson = haveMap[i]
                if (seatPerson != null){
                    //座位有人
                    records.add(seatPerson)
                }else{
                    //座位没人
                    records.add(StudyRecord(
                        0,0,libraryId,roomId,"",i,0,
                        0,0,0,"",0,
                        "","",null,null,null,null
                    ))
                }
            }

        }else{
            //根据容纳人数生成
            for (i in 1..count){
                records.add(StudyRecord(
                    0,0,libraryId,roomId,"",i,0,
                    0,0,0,"",0,
                    "","",null,null,null,null
                ))
            }
        }
        adapter.notifyItemRangeChanged(0, records.size)
    }

    //开始学习AlertDialog
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showLibraryDetail(record: StudyRecord){
        if (!::roomRecord.isInitialized){
            return
        }
        detailDialog = AlertDialog.Builder(this).create()
        detailDialog.setCancelable(true)
        detailDialog.setCanceledOnTouchOutside(false)

        val dialogBinding = DialogStartStudyBinding.inflate(layoutInflater)

        detailDialog.setView(dialogBinding.root)
        if (detailDialog.window != null) {
            detailDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        dialogBinding.diaStStClose.setOnClickListener {
            hideLibraryDetail()
        }

        dialogBinding.diaStStCountdown.setOnClickListener {
            dialogBinding.diaStStCountdown.background = resources.getDrawable(R.drawable.bg_countdown_type)
            dialogBinding.diaStStCountdown.setTextColor(resources.getColor(R.color.white))

            dialogBinding.diaStStPositiveTime.background = resources.getDrawable(R.color.colorBody)
            dialogBinding.diaStStPositiveTime.setTextColor(resources.getColor(R.color.colorPrimary))

            dialogBinding.imgPositiveTime.visibility = View.GONE
            dialogBinding.studyTime.visibility = View.VISIBLE
            dialogBinding.diaStStCount.visibility = View.VISIBLE
        }

        dialogBinding.diaStStPositiveTime.setOnClickListener {
            dialogBinding.diaStStPositiveTime.background = resources.getDrawable(R.drawable.bg_countdown_type)
            dialogBinding.diaStStPositiveTime.setTextColor(resources.getColor(R.color.white))

            dialogBinding.diaStStCountdown.background = resources.getDrawable(R.color.colorBody)
            dialogBinding.diaStStCountdown.setTextColor(resources.getColor(R.color.colorPrimary))

            dialogBinding.imgPositiveTime.visibility = View.VISIBLE
            dialogBinding.studyTime.visibility = View.GONE
            dialogBinding.diaStStCount.visibility = View.GONE
        }

        dialogBinding.diaStStStart.setOnClickListener {
            KeyboardUtils.hideSoftInput(this)
            val studyRecordDTO = StudyRecordDTO(roomId,"",record.seat,0,null)

            //自习标签
            val tag = dialogBinding.diaStStStudyTag.text.toString()
            if (StrUtil.isBlank(tag)){
                "请输入自习标签".toast()
                return@setOnClickListener
            }
            studyRecordDTO.tag = tag

            val flag = dialogBinding.diaStStCountdown.currentTextColor == resources.getColor(R.color.white)
            //计时方式
            var timingMode: Int = 1
            if (flag){
                timingMode = 2
            }
            studyRecordDTO.timingMode = timingMode
            if (timingMode == 2){
                val diaStStCount = dialogBinding.diaStStCount.editableText.toString()
                if (StrUtil.isBlank(diaStStCount)){
                    "请输入自习时长".toast()
                    return@setOnClickListener
                }
                studyRecordDTO.settingDuration  = diaStStCount.toInt()
            }
            //开始自习
            roomViewModel.startStudy(studyRecordDTO)
        }

        detailDialog.show()
    }

    private fun hideLibraryDetail(){
        if (detailDialog.isShowing) {
            if (detailDialog.window != null) {
                detailDialog.window!!.decorView.post { detailDialog.dismiss() }
            }
        }
    }
}