package com.lxy.studyroom.ui.feedback

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
import com.lxy.studyroom.logic.network.ServiceCreator
import com.lxy.studyroom.ui.upload.UploadViewModel
import kotlinx.android.synthetic.main.activity_edit_feedback.*
import kotlinx.android.synthetic.main.activity_edit_note.*

class EditFeedbackActivity : BaseActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(FeedbackViewModel::class.java) }

    private val uploadViewModel by lazy { ViewModelProvider(this).get(UploadViewModel::class.java) }

    private var pic: String = ""

    companion object {
        @JvmStatic
        fun actionStart(context: Context){
            val intent = Intent(context, EditFeedbackActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_feedback)

        setEvent()
        observeUpload()
        observeFeedback()
    }

    private fun setEvent() {
        edFeTitleBar.setOnTitleBarListener(object :OnTitleBarListener{
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })
        edFePicNo.setOnClickListener {
            pictureSelector { file ->
                "图片上传中...".toast()
                uploadViewModel.upload(file)
                showDianDianLoading()
            }
        }
        edFeNoRemove.setOnClickListener {
            edFePicNo.visibility = View.VISIBLE
            edFeNoPicHave.visibility = View.GONE
            pic = ""
        }
        edFeNoPic.setOnClickListener {
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
        edFeBtn.setOnClickListener {
            //隐藏输入法
            KeyboardUtils.hideSoftInput(this@EditFeedbackActivity)
            val content = edFeContent.text.toString()
            if (StrUtil.isBlank(content)){
                "请输入反馈内容".toast()
                return@setOnClickListener
            }
            var picPath = ""
            if(StrUtil.isNotEmpty(pic)){
                picPath = pic.substring(pic.indexOf("/upload"))
            }
            //
            viewModel.submitFeedBack(content, pic)

        }

    }


    private fun observeUpload(){
        uploadViewModel.uploadResp.observe(this){resp ->
            if (resp.isSuccess()){
                edFePicNo.visibility = View.GONE
                edFeNoPicHave.visibility = View.VISIBLE
                val pathList = resp.data!!
                pic = pathList[0]
                Glide.with(this@EditFeedbackActivity).load(BuildConfig.OSS_PREFIX + pic).into(edFeNoPic)
            }else{
                "图片上传失败，请重试".toast()
                edFePicNo.visibility = View.VISIBLE
                edFeNoPicHave.visibility = View.GONE
                pic = ""
            }
            destroyDianDianLoading()
        }
    }

    private fun observeFeedback(){
        viewModel.submitFeedBackResp.observe(this){resp ->
            if (resp.isSuccess()){
                "反馈成功".toast()
                finish()
            }else{
                "提交反馈失败，请重试".toast()
            }
        }
    }


}