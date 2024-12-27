package com.lxy.studyroom.ui.room

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lxy.studyroom.R
import com.lxy.studyroom.logic.model.RoomDetail

class TabAdapter(private val des: List<RoomDetail>) : RecyclerView.Adapter<TabAdapter.ViewHolder>() {

    var curr = 0

    lateinit var listener: OnItemClickListener

    fun getCurrDetail() = des[curr]

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val desTitle: TextView = view.findViewById(R.id.lib_tab_design_title)
        val designLine: View = view.findViewById(R.id.lib_tab_design_line)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lib_tab_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val detail = des[position]
        holder.desTitle.text = detail.catalogName

        if (curr == position){
            holder.designLine.visibility = View.VISIBLE
        }else{
            holder.designLine.visibility = View.GONE
        }



        holder.itemView.setOnClickListener {
            if (::listener.isInitialized){
                listener.onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int  = des.size

}