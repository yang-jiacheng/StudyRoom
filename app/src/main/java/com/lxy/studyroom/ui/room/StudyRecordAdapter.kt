package com.lxy.studyroom.ui.room

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import cn.hutool.core.util.StrUtil
import com.bumptech.glide.Glide
import com.lxy.studyroom.R
import com.lxy.studyroom.logic.model.StudyRecord

class StudyRecordAdapter(val context: Context, private val records: List<StudyRecord>) : RecyclerView.Adapter<StudyRecordAdapter.ViewHolder>()  {

    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val itStuRoSeatHave: ConstraintLayout = view.findViewById(R.id.itStuRoSeatHave)
        val itStuRoSeat: FrameLayout = view.findViewById(R.id.itStuRoSeat)
        val itStuRoName: TextView = view.findViewById(R.id.itStuRoName)
        val itStuRoTag: TextView = view.findViewById(R.id.itStuRoTag)
        val itStuRoUserPro: ImageView = view.findViewById(R.id.itStuRoUserPro)
    }

    fun getCurrItem(position: Int) = records[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_study_room_seat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            if (::listener.isInitialized){
                listener.onItemClick(position)
            }
        }

        val record = records[position]
        if (StrUtil.isEmpty(record.name)){
            holder.itStuRoSeatHave.visibility = View.GONE
            holder.itStuRoSeat.visibility = View.VISIBLE
        }else{
            holder.itStuRoSeatHave.visibility = View.VISIBLE
            holder.itStuRoSeat.visibility = View.GONE

            holder.itStuRoName.text = record.name
            holder.itStuRoTag.text = record.tag
            Glide.with(context).load(record.profilePath).into(holder.itStuRoUserPro)
        }
    }

    override fun getItemCount(): Int = records.size


}