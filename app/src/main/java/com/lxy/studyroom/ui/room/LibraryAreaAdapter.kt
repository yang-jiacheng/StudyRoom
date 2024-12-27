package com.lxy.studyroom.ui.room

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lxy.studyroom.R
import com.lxy.studyroom.logic.model.RoomDetail

class LibraryAreaAdapter(val context: Context, private val rooms: List<RoomDetail>) : RecyclerView.Adapter<LibraryAreaAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val areaName: TextView = view.findViewById(R.id.ilAreaName)
        val areaCount: TextView = view.findViewById(R.id.ilAreaCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_library_area,parent,false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.absoluteAdapterPosition
            val roomDetail = rooms[position]
            RoomDetailActivity.actionStart(context,roomDetail.catalogId,roomDetail.classifyId)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room = rooms[position]
        val count =
        if (room.currCount == null){
            "0 人"
        }else{
            "${room.currCount} 人"
        }
        holder.areaName.text = room.catalogName
        holder.areaCount.text = count
    }

    override fun getItemCount(): Int = rooms.size

}