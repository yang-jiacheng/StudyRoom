package com.lxy.studyroom.ui.main.statistics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import cn.hutool.core.collection.CollUtil
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.lxy.studyroom.BaseActivity
import com.lxy.studyroom.R
import com.lxy.studyroom.databinding.ActivityAllRecordBinding
import com.lxy.studyroom.extension.toast
import com.lxy.studyroom.logic.model.StudyRecord
import com.lxy.studyroom.ui.room.StudyRecordViewModel
import com.lxy.studyroom.util.LogUtil
import kotlin.concurrent.thread

class AllRecordActivity : BaseActivity() {

    private lateinit var binding: ActivityAllRecordBinding
    private val studyRecordViewModel by lazy { ViewModelProvider(this).get(StudyRecordViewModel::class.java) }
    private var records = ArrayList<StudyRecord>()
    private var page = 1
    private var limit = 12
    private var lastItemPosition = 0

    companion object {
        @JvmStatic
        fun actionStart(context: Context){
            val intent = Intent(context, AllRecordActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        //RecyclerView
        binding.allRecordRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = UserRecordAdapter(this,records)
        binding.allRecordRecyclerView.adapter = adapter
        setScrollNextPage(adapter)

        //监听事件
        setEvent()
        //下拉刷新
        binding.allRecordRefresh.setColorSchemeResources(R.color.colorPrimary)
        binding.allRecordRefresh.setOnRefreshListener {
            thread {
                Thread.sleep(1000)
                runOnUiThread{
                    page = 1
                    studyRecordViewModel.getStudyRecord(page, limit)
                }
            }
        }

        observeData(adapter)

        showDianDianLoading()
        thread {
            Thread.sleep(500)
            runOnUiThread{
                //获取数据
                studyRecordViewModel.getStudyRecord(page, limit)
            }
        }
    }

    private fun setEvent() {
        //TitleBar点击事件
        binding.allRecordTitleBar.setOnTitleBarListener(object : OnTitleBarListener{
            override fun onLeftClick(titleBar: TitleBar?) {
                onBackPressed()
            }

            override fun onTitleClick(titleBar: TitleBar?) {
                binding.allRecordRecyclerView.smoothScrollToPosition(0)
            }
        })
    }

    private fun observeData(adapter: UserRecordAdapter){
        studyRecordViewModel.studyRecordResp.observe(this){resp->
            //旧集合数量
            var oldPos = 0
            if (page == 1){
                records.clear()
            }
            if (resp.isSuccess()){

                val result = resp.data
                if (CollUtil.isNotEmpty(result)){

                    oldPos = records.size
                    records.addAll(result!!)
                }else{
                    "没有更多数据了".toast()
                    if (page>1){
                        page --
                    }
                }
            }else{
                resp.msg!!.toast()
            }
            if (page == 1){
                adapter.notifyDataSetChanged()
            }else{
                adapter.notifyItemRangeChanged(oldPos,records.size)
                if (oldPos > 0 && records.size > oldPos){
                    //scrollToPosition  smoothScrollToPosition
                    binding.allRecordRecyclerView.scrollToPosition(oldPos+1)
                }
            }

            destroyDianDianLoading()
            binding.allRecordRefresh.isRefreshing = false
        }
    }

    private fun setScrollNextPage(adapter: UserRecordAdapter){
        binding.allRecordRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                //未滚动且当前定位是最后一个
//                if (newState == SCROLL_STATE_IDLE && lastItemPosition == adapter.itemCount){
//                    //已经滚动到底部
//                    if (!binding.allRecordRecyclerView.canScrollVertically(1)){
//                        page ++
//                        studyRecordViewModel.getStudyRecord(page, limit)
//                    }
//                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager
                val manager: LinearLayoutManager = layoutManager as LinearLayoutManager
                val firstVisibleItem = manager.findFirstVisibleItemPosition()
                val l = manager.findLastCompletelyVisibleItemPosition()
                lastItemPosition = firstVisibleItem + (l-firstVisibleItem) + 1

                if(l == records.size - 1){
                    page ++
                    studyRecordViewModel.getStudyRecord(page, limit)
                }
            }
        })
    }
}