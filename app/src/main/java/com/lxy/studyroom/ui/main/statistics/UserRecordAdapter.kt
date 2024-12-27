package com.lxy.studyroom.ui.main.statistics

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lxy.studyroom.R
import com.lxy.studyroom.logic.model.StudyRecord
import com.lxy.studyroom.ui.room.EditNoteActivity

class UserRecordAdapter(val context: Context,private val records: List<StudyRecord>) : RecyclerView.Adapter<UserRecordAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvCreateTime: TextView = view.findViewById(R.id.tvCreateTime)
        val tvDiary: TextView = view.findViewById(R.id.tvDiary)
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_study_record,parent,false)
        val viewHolder = ViewHolder(view)
        //笔记点击事件
        viewHolder.tvDiary.setOnClickListener {
            val position = viewHolder.bindingAdapterPosition
            val studyRecord = records[position]
            EditNoteActivity.actionStart(context,studyRecord.id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val studyRecord = records[position]
        holder.tvCreateTime.text = studyRecord.startTime
        //是否有笔记 0否 1是
        val noteStatus = studyRecord.noteStatus!!
        val noteDiary =
            if (noteStatus == 0){
                "添加自习笔记"
            }else{
                "编辑自习笔记"
            }
        holder.tvDiary.text = noteDiary

        val content = "在${studyRecord.classifyName}自习"
        holder.tvContent.text = content

        val time = "${studyRecord.actualDuration}分钟"
        holder.tvTime.text = time

    }

    override fun getItemCount(): Int = records.size

}