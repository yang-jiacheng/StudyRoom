package com.lxy.studyroom.ui.room

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import cn.hutool.core.util.StrUtil
import com.blankj.utilcode.util.KeyboardUtils
import com.bumptech.glide.Glide
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.lxy.studyroom.BaseActivity
import com.lxy.studyroom.BuildConfig
import com.lxy.studyroom.R
import com.lxy.studyroom.extension.toast
import com.lxy.studyroom.logic.model.StudyRecord
import com.lxy.studyroom.logic.network.ServiceCreator
import com.lxy.studyroom.ui.upload.UploadViewModel
import kotlinx.android.synthetic.main.activity_edit_note.*

/**
 * 写笔记页
 */

class EditNoteActivity : BaseActivity() {

    private var recordId: Int = 0

    /**
     * 学习记录
     */
    private lateinit var noteDetail: StudyRecord

    private var pic: String = ""

    private val studyRecordViewModel by lazy { ViewModelProvider(this).get(StudyRecordViewModel::class.java) }

    private val uploadViewModel by lazy { ViewModelProvider(this).get(UploadViewModel::class.java) }

    companion object{
        @JvmStatic
        fun actionStart(context: Context, recordId: Int){
            val intent = Intent(context, EditNoteActivity::class.java).apply {
                putExtra("record_id", recordId)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)
        recordId = intent.getIntExtra("record_id",0)
        //获取笔记详情
        studyRecordViewModel.getStudyNoteDetail(recordId)
        //监听事件
        setEvent()
        //观察数据
        observeNoteDetail()
        observeUpload()
        observeSaveNote()
    }

    private fun setEvent() {
        //TitleBar点击事件
        acEdNoTitleBar.setOnTitleBarListener(object : OnTitleBarListener{
            override fun onLeftClick(titleBar: TitleBar?) {
                onBackPressed()
            }
            override fun onRightClick(titleBar: TitleBar?) {
                //隐藏输入法
                KeyboardUtils.hideSoftInput(this@EditNoteActivity)
                val content = acEdNoContent.text.toString()
                if (StrUtil.isBlank(content)){
                    "请输入笔记内容".toast()
                    return
                }
                var picPath = ""
                if(StrUtil.isNotEmpty(pic)){
                    picPath = pic.substring(pic.indexOf("/upload"))
                }
                //保存笔记
                studyRecordViewModel.saveStudyNote(recordId, content, picPath)

            }
        })
        //选择图片
        acEdNoPicNo.setOnClickListener {
            pictureSelector { file ->
                "图片上传中...".toast()
                uploadViewModel.upload(file)
                showDianDianLoading()
            }
        }
        //删除图片
        acEdNoRemove.setOnClickListener {
            acEdNoPicNo.visibility = View.VISIBLE
            acEdNoPicHave.visibility = View.GONE
            pic = ""
        }
        //查看大图
        acEdNoPic.setOnClickListener {
            var picPath = ""
            if (StrUtil.isNotBlank(pic)){
                picPath =
                if (pic.indexOf(BuildConfig.OSS_PREFIX) == -1){
                    BuildConfig.OSS_PREFIX + pic
                }else{
                    pic
                }
                getImagePreview(picPath)
            }
        }
    }



    private fun observeNoteDetail(){
        studyRecordViewModel.noteDetailResp.observe(this){ resp ->
            if (resp.isSuccess()){
                noteDetail = resp.data!!

                val tag = "标签：${noteDetail.tag}"
                val duration = "时长：${noteDetail.actualDuration}分钟"
                val content = noteDetail.noteContent
                val picPath = noteDetail.notePath
                acEdNoTag.text = tag
                acEdNoDuration.text = duration
                if (StrUtil.isNotEmpty(content)){
                    acEdNoContent.setText(content)
                }
                if (StrUtil.isNotEmpty(picPath)){
                    acEdNoPicHave.visibility = View.VISIBLE
                    acEdNoPicNo.visibility = View.GONE
                    Glide.with(this).load(picPath).into(acEdNoPic)
                    pic = picPath!!
                }else{
                    acEdNoPicNo.visibility = View.VISIBLE
                    acEdNoPicHave.visibility = View.GONE
                }

            }else{
                "获取笔记失败，请重试".toast()
            }
        }
    }

    private fun observeUpload(){
        uploadViewModel.uploadResp.observe(this){resp ->
            if (resp.isSuccess()){
                acEdNoPicNo.visibility = View.GONE
                acEdNoPicHave.visibility = View.VISIBLE
                val pathList = resp.data!!
                pic = pathList[0]
                Glide.with(this@EditNoteActivity).load(BuildConfig.OSS_PREFIX + pic).into(acEdNoPic)
            }else{
                "图片上传失败，请重试".toast()
                acEdNoPicNo.visibility = View.VISIBLE
                acEdNoPicHave.visibility = View.GONE
                pic = ""
            }
            destroyDianDianLoading()
        }
    }

    private fun observeSaveNote(){
        studyRecordViewModel.saveNoteResp.observe(this){ resp ->
            if (resp.isSuccess()){
                "保存成功".toast()
                finish()
            }else{
                "笔记保存失败，请重试".toast()
            }
        }
    }

}