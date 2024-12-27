package com.lxy.studyroom.ui.personalcenter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import cn.hutool.core.util.StrUtil
import com.bumptech.glide.Glide
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.lxy.studyroom.BaseActivity
import com.lxy.studyroom.BuildConfig
import com.lxy.studyroom.R
import com.lxy.studyroom.extension.toast
import com.lxy.studyroom.logic.dto.UserDTO
import com.lxy.studyroom.logic.model.User
import com.lxy.studyroom.logic.network.ServiceCreator
import com.lxy.studyroom.ui.upload.UploadViewModel
import com.lxy.studyroom.util.LogUtil
import com.lxy.studyroom.util.StatusBarUtil
import com.lxy.studyroom.widget.view.GlideEngine
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropImageEngine
import kotlinx.android.synthetic.main.activity_edit_user_info.*
import java.io.File

class EditUserInfoActivity : BaseActivity() {

    val userViewModel by lazy { ViewModelProvider(this).get(UserViewModel::class.java) }

    val uploadViewModel by lazy { ViewModelProvider(this).get(UploadViewModel::class.java) }

    private lateinit var userInfo: User

    private var userDTO = UserDTO("","","","","","","","")

    companion object {
        @JvmStatic
        fun actionStart(context: Context, userInfo: User){
            val intent = Intent(context, EditUserInfoActivity::class.java).apply {
                putExtra("user_info",userInfo)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.immersiveStatusBar(this, Color.TRANSPARENT)
        setContentView(R.layout.activity_edit_user_info)

        userInfo = intent.getSerializableExtra("user_info") as User

        //回显用户信息
        handleUserInfo()

        //点击事件
        setEvent()


        observeCoverPath()
        observeIconPath()
        observeUpdateUserInfo()
    }


    private fun setEvent() {
        //TitleBar点击事件
        upInfoTitleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                onBackPressed()
            }
        })

        //选择地址
        upInfoAddressLayout.setOnClickListener {
            addressSelector{ province, city, county ->
                val address = province.name + city.name + county.name
                this@EditUserInfoActivity.upInfoAddress.text = address
            }
        }

        //选择背景
        upInfoCover.setOnClickListener {
            pictureSelectorCrop(
                {file ->
                    "图片上传中...".toast()
                    uploadViewModel.uploadCover(file)
                    showDianDianLoading()
                },(16).toFloat(),(9).toFloat(),false
            )
        }

        //选择头像
        upInfoIcon.setOnClickListener {
            pictureSelectorCrop(
                {file ->
                    "图片上传中...".toast()
                    uploadViewModel.uploadIcon(file)
                    showDianDianLoading()
                },(1).toFloat(),(1).toFloat(),true
            )

        }

        upInfoBtn.setOnClickListener {
            val name = upInfoName.text.toString()
            val address = upInfoAddress.text.toString()
            val gender = findViewById<RadioButton>(upInfoGroup.checkedRadioButtonId).text.toString()
            userDTO.name = name
            userDTO.address = address
            userDTO.gender = gender

            userViewModel.updateUserInfo(userDTO)

        }

    }

    private fun observeCoverPath(){
        uploadViewModel.uploadCoverResp.observe(this){resp ->
            if (resp.isSuccess()){
                val data = resp.data!![0]
                userDTO.coverPath = data
                Glide.with(this@EditUserInfoActivity).load(BuildConfig.OSS_PREFIX + data).into(upInfoCover)
            }else{
                "背景图片上传失败，请重试".toast()
            }
            destroyDianDianLoading()
        }
    }

    private fun observeIconPath(){
        uploadViewModel.uploadIconResp.observe(this){resp ->
            if (resp.isSuccess()){
                val data = resp.data!![0]
                userDTO.profilePath = data
                Glide.with(this@EditUserInfoActivity).load(BuildConfig.OSS_PREFIX + data).into(upInfoIcon)
            }else{
                "头像上传失败，请重试".toast()
            }
            destroyDianDianLoading()
        }
    }

    private fun observeUpdateUserInfo(){
        userViewModel.upInfoResponse.observe(this){resp ->
            if (resp.isSuccess()){
                "修改个人资料成功".toast()
                finish()
            }else{
                "修改个人资料失败,请重试".toast()
                finish()
            }
        }
    }

    private fun handleUserInfo() {
        //报错了
        //BeanUtil.copyProperties(userInfo,userDTO,true)
        userDTO.name = userInfo.name
        userDTO.gender = userInfo.gender
        userDTO.address = userInfo.address ?: ""
        val profilePath = userInfo.profilePath
        val coverPath = userInfo.coverPath ?: ""

        if (StrUtil.isNotBlank(userDTO.name)){
            upInfoName.setText(userDTO.name)
        }
        if (StrUtil.isNotBlank(userDTO.address)){
            upInfoAddress.text = userDTO.address
        }
        if (StrUtil.isNotEmpty(profilePath)){
            Glide.with(this@EditUserInfoActivity).load(profilePath).into(upInfoIcon)
            userDTO.profilePath = profilePath.substring(profilePath.indexOf("/upload"))
        }
        if (StrUtil.isNotEmpty(coverPath)){
            Glide.with(this@EditUserInfoActivity).load(coverPath).into(upInfoCover)
            userDTO.coverPath = coverPath.substring(coverPath.indexOf("/upload"))
        }
        if (StrUtil.isNotEmpty(userDTO.gender)){
            if ("男" == userDTO.gender){
                upInfoNan.isChecked = true
            }else{
                upInfoNv.isChecked = true
            }
        }

    }




}