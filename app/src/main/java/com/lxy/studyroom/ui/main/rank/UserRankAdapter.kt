package com.lxy.studyroom.ui.main.rank

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.hutool.core.util.StrUtil
import com.bumptech.glide.Glide
import com.lxy.studyroom.R
import com.lxy.studyroom.extension.toast
import com.lxy.studyroom.logic.Repository
import com.lxy.studyroom.logic.model.UserRank
import com.lxy.studyroom.util.LogUtil

class UserRankAdapter(val context: Context, private val ranks: List<UserRank>) : RecyclerView.Adapter<UserRankAdapter.ViewHolder>(){

    private val selfId = Repository.getUserId()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val itemRank: TextView = view.findViewById(R.id.itemRank)
        val itemProfile: ImageView = view.findViewById(R.id.itemProfile)
        val itemName: TextView = view.findViewById(R.id.itemName)
        val itemTime: TextView = view.findViewById(R.id.itemTime)
        val rankItemLayout: LinearLayout = view.findViewById(R.id.rankItemLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_tab_rank,parent,false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.absoluteAdapterPosition
            val userRank = ranks[position]
            OtherUserActivity.actionStart(context,userRank.id)
        }

        return viewHolder
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userRank = ranks[position]
        val count = "${userRank.totalDuration}分钟"
        holder.itemTime.text = count
        holder.itemRank.text = userRank.ranking.toString()
        holder.itemName.text = userRank.name

        val id = userRank.id
        if(id == selfId){
            LogUtil.e("aaa",selfId.toString())
            holder.rankItemLayout.background = context.resources.getDrawable(R.color.colorBody)
        }else{
            holder.rankItemLayout.background = context.resources.getDrawable(R.color.white)
        }

        if (StrUtil.isEmpty(userRank.profilePath)){
            holder.itemProfile.setImageResource(R.drawable.def_path)
        }else{
            Glide.with(context).load(userRank.profilePath).into(holder.itemProfile)
        }
    }

    override fun getItemCount() = ranks.size

}