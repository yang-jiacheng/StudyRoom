package com.lxy.studyroom.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lxy.studyroom.R
import com.lxy.studyroom.logic.model.Library
import com.lxy.studyroom.ui.room.LibraryRoomActivity

class LibraryAdapter(val context: Context, private val libraryList: List<Library>) : RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val leftLibraryImg : ImageView = view.findViewById(R.id.leftLibraryImg)
        val leftLibraryName: TextView = view.findViewById(R.id.leftLibraryName)
        val leftLibraryCount: TextView = view.findViewById(R.id.leftLibraryCount)

        val rightLibraryImg : ImageView = view.findViewById(R.id.rightLibraryImg)
        val rightLibraryName: TextView = view.findViewById(R.id.rightLibraryName)
        val rightLibraryCount: TextView = view.findViewById(R.id.rightLibraryCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.library_left_item,parent,false)
        val viewHolder = ViewHolder(view)
        //图片的点击事件
        viewHolder.leftLibraryImg.setOnClickListener{
            val position = viewHolder.bindingAdapterPosition
            val library = libraryList[position]
            val libraryId = library.id
            //图书馆详情页面
            LibraryRoomActivity.actionStart(context,libraryId)
        }
        viewHolder.rightLibraryImg.setOnClickListener{
            val position = viewHolder.bindingAdapterPosition
            val library = libraryList[position]
            val libraryId = library.id
            //图书馆详情页面
            LibraryRoomActivity.actionStart(context,libraryId)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val library = libraryList[position]

        if (library.flag!!){

            //左边数据
            Glide.with(context).load(library.iconPath).into(holder.leftLibraryImg)
            holder.leftLibraryName.text = library.name
            val studyCount = "${library.studyCount}人自习中"
            holder.leftLibraryCount.text = studyCount
            //右边隐藏
            holder.rightLibraryImg.visibility = View.GONE
            holder.rightLibraryName.visibility = View.GONE
            holder.rightLibraryCount.visibility = View.GONE
        }else{
            //右边数据
            Glide.with(context).load(library.iconPath).into(holder.rightLibraryImg)
            holder.rightLibraryName.text = library.name
            val studyCount: String = "${library.studyCount}人自习中"
            holder.rightLibraryCount.text = studyCount
            //左边隐藏
            holder.leftLibraryImg.visibility = View.GONE
            holder.leftLibraryName.visibility = View.GONE
            holder.leftLibraryCount.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = libraryList.size

}