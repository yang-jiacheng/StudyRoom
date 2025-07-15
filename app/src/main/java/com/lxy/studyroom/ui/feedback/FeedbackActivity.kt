package com.lxy.studyroom.ui.feedback

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.hutool.core.collection.CollUtil
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.lxy.studyroom.BaseActivity
import com.lxy.studyroom.R
import com.lxy.studyroom.databinding.ActivityFeedbackBinding
import com.lxy.studyroom.extension.toast
import com.lxy.studyroom.logic.model.Feedback
import com.lxy.studyroom.util.LogUtil
import kotlin.concurrent.thread

class FeedbackActivity : BaseActivity() {

    private lateinit var binding: ActivityFeedbackBinding
    private val viewModel by lazy { ViewModelProvider(this).get(FeedbackViewModel::class.java) }
    private val feedList = ArrayList<Feedback>()
    private lateinit var feedbackDetail: Feedback
    private var page = 1
    private var limit = 12
    private var mine = 0

    companion object {
        @JvmStatic
        fun actionStart(context: Context){
            val intent = Intent(context, FeedbackActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mine = 0

        showDianDianLoading()

        //RecyclerView
        binding.feedRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = UserFeedbackAdapter(this,feedList)
        binding.feedRecyclerView.adapter = adapter
        setScrollNextPage(adapter)
        setEvent()
        observeData(adapter)

        thread {
            Thread.sleep(500)
            runOnUiThread{
                //获取反馈列表
                viewModel.getFeedBackList(page,limit,mine)
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        binding.feedAllBtn.setTextColor(ContextCompat.getColor(this@FeedbackActivity,R.color.colorPrimary))
        binding.feedMineBtn.setTextColor(ContextCompat.getColor(this@FeedbackActivity,R.color.primary_text_color))
        page = 1
        mine = 0
        binding.feedRecyclerView.smoothScrollToPosition(0)
        viewModel.getFeedBackList(page,limit, mine)
    }

    private fun setEvent() {
        //标题栏
        binding.feedTitleBar.setOnTitleBarListener(object :OnTitleBarListener{
            override fun onLeftClick(titleBar: TitleBar?) {
                onBackPressed()
            }
            override fun onTitleClick(titleBar: TitleBar?) {
                binding.feedRecyclerView.smoothScrollToPosition(0)
            }
        })
        //全部反馈
        binding.feedAllBtn.setOnClickListener {
            binding.feedAllBtn.setTextColor(ContextCompat.getColor(this@FeedbackActivity,R.color.colorPrimary))
            binding.feedMineBtn.setTextColor(ContextCompat.getColor(this@FeedbackActivity,R.color.primary_text_color))
            page = 1
            mine = 0
            binding.feedRecyclerView.smoothScrollToPosition(0)
            viewModel.getFeedBackList(page,limit,mine)
        }
        //我的反馈
        binding.feedMineBtn.setOnClickListener {
            binding.feedMineBtn.setTextColor(ContextCompat.getColor(this@FeedbackActivity,R.color.colorPrimary))
            binding.feedAllBtn.setTextColor(ContextCompat.getColor(this@FeedbackActivity,R.color.primary_text_color))
            page = 1
            mine = 1
            binding.feedRecyclerView.smoothScrollToPosition(0)
            viewModel.getFeedBackList(page,limit,mine)
        }

        binding.feedAddBtn.setOnClickListener {
            EditFeedbackActivity.actionStart(this)
        }
    }

    private fun setScrollNextPage(adapter: UserFeedbackAdapter) {
        binding.feedRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager
                val manager: LinearLayoutManager = layoutManager as LinearLayoutManager
                val firstVisibleItem = manager.findFirstVisibleItemPosition()
                val l = manager.findLastCompletelyVisibleItemPosition()
                if(l == feedList.size - 1){
                    page ++
                    viewModel.getFeedBackList(page, limit,mine)
                }
            }
        })
    }

    private fun observeData(adapter: UserFeedbackAdapter) {
        viewModel.feedBackListResp.observe(this) { resp ->
            //旧集合数量
            var oldPos = 0
            if (page == 1){
                feedList.clear()
            }
            if (resp.isSuccess()){
                val result = resp.data
                if (CollUtil.isNotEmpty(result)){
                    oldPos = feedList.size
                    feedList.addAll(result!!)
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
                LogUtil.e("page",page.toString())
            }else{
                adapter.notifyItemRangeChanged(oldPos,feedList.size)
                if (oldPos > 0 && feedList.size > oldPos){
                    binding.feedRecyclerView.scrollToPosition(oldPos+1)
                }
            }

            destroyDianDianLoading()
        }
    }
}