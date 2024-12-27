package com.lxy.studyroom.ui.feedback

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cc.shinichi.library.ImagePreview
import cn.hutool.core.date.DatePattern
import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.StrUtil
import com.bumptech.glide.Glide
import com.lxy.studyroom.R
import com.lxy.studyroom.constant.CommonConstant
import com.lxy.studyroom.logic.model.Feedback
import com.lxy.studyroom.ui.main.rank.OtherUserActivity
import java.util.*

class UserFeedbackAdapter(val context: Context,private val feedList: List<Feedback>) : RecyclerView.Adapter<UserFeedbackAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val feedItemPro: ImageView = view.findViewById(R.id.feedItemPro)
        val feedItemName: TextView = view.findViewById(R.id.feedItemName)
        val feedItemContent: TextView = view.findViewById(R.id.feedItemContent)
        val feedItemPic: ImageView = view.findViewById(R.id.feedItemPic)
        val feedItemTime: TextView = view.findViewById(R.id.feedItemTime)
        val feedItemReply: TextView = view.findViewById(R.id.feedItemReply)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feedback, parent, false)
        val holder = ViewHolder(view)


        //反馈图片点击
        holder.feedItemPic.setOnClickListener {
            val position = holder.bindingAdapterPosition
            val detail = feedList[position]
            val pic = detail.pic
            if (StrUtil.isNotEmpty(pic)){
                ImagePreview.instance
                    // 上下文，必须是activity，不需要担心内存泄漏，本框架已经处理好；
                    .setContext(context)
                    // 设置从第几张开始看（索引从0开始）
                    .setIndex(0)
                    // 只有一张图片的情况，可以直接传入这张图片的url
                    .setImage(pic!!)
                    // 开启预览
                    .start()
            }
        }

        //头像点击
        holder.feedItemPro.setOnClickListener {
            val position = holder.bindingAdapterPosition
            val detail = feedList[position]
            OtherUserActivity.actionStart(context,detail.userId)
        }

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val detail = feedList[position]
        //头像
        val profilePath = detail.profilePath
        //反馈图片
        val pic = detail.pic
        //反馈时间
        var createTime = detail.createTime
        val date: Date = DateUtil.parse(createTime, DatePattern.NORM_DATETIME_PATTERN)
        createTime = DateUtil.format(date,"yyyy年MM月dd日 HH:mm")
        //回复
        var reply = detail.reply

        if (StrUtil.isNotEmpty(profilePath)){
            Glide.with(context).load(profilePath).into(holder.feedItemPro)
        }

        holder.feedItemName.text = detail.name
        holder.feedItemContent.text = detail.content

        if (StrUtil.isEmpty(pic)){
            holder.feedItemPic.visibility = View.GONE
        }else{
            holder.feedItemPic.visibility = View.VISIBLE
            Glide.with(context).load(pic).into(holder.feedItemPic)
        }
        holder.feedItemTime.text = createTime

        if (StrUtil.isEmpty(reply)){
            holder.feedItemReply.visibility = View.GONE
        }else{
            holder.feedItemReply.visibility = View.VISIBLE
            reply = "官方回复：${reply}"
            holder.feedItemReply.text = reply
        }

    }

    override fun getItemCount(): Int  = feedList.size

}