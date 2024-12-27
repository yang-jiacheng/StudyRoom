package com.lxy.studyroom.ui.main.rank

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.lxy.studyroom.BaseActivity
import com.lxy.studyroom.R
import com.lxy.studyroom.enums.ResponseEnum
import com.lxy.studyroom.extension.toast
import com.lxy.studyroom.logic.model.UserRank
import com.lxy.studyroom.util.CommonUtil
import com.lxy.studyroom.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_total_rank.*
import kotlinx.android.synthetic.main.dialog_rule.view.*
import kotlinx.android.synthetic.main.fragment_rank.*

class TotalRankActivity : BaseActivity() {

    private val rankViewModel by lazy { ViewModelProvider(this).get(RankViewModel::class.java) }

    private lateinit var ruleDialog: AlertDialog

    private val tabList = listOf("总榜")

    private val fragmentList = ArrayList<RankFragment>()

    private var rule: String = ""

    companion object {
        @JvmStatic
        fun actionStart(context: Context){
            val intent = Intent(context, TotalRankActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //沉浸式状态栏
        StatusBarUtil.setStatusBarTextColor(this, Color.TRANSPARENT)
        setContentView(R.layout.activity_total_rank)

        setSupportActionBar(aToRaToolbar)
        aToRaBar.outlineProvider = null
        aToRaCollBar.outlineProvider = ViewOutlineProvider.BOUNDS
        //加载
        showDianDianLoading()

        //RecyclerView
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        aToRa_tab.layoutManager = layoutManager
        val tabAdapter = RankTabAdapter(tabList)
        aToRa_tab.adapter = tabAdapter
        //RecyclerView分割线
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        aToRa_tab.addItemDecoration(decoration)

        //viewPager
        val fragmentAdapter = RankFragmentAdapter(supportFragmentManager,fragmentList)
        aToRa_pager.adapter = fragmentAdapter

        //获取排行榜和规则
        rankViewModel.getRankAndRule(CommonUtil.getRandomCode())
        //监听事件
        setEvent(tabAdapter)
        //观察排行榜数据
        observeRankData(tabAdapter,fragmentAdapter)
    }

    private fun setEvent(tabAdapter: RankTabAdapter) {
        aToRaBack.setOnClickListener {
            finish()
        }

        //排行榜更新规则 AlertDialog
        aToRaQues.setOnClickListener {
            showRuleDialog()
        }

        //点击RecyclerView子项item时更改viewPager当前定位
        tabAdapter.setOnItemClickListener(object : RankTabAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                //当前定位
                tabAdapter.curr = position
                tabAdapter.notifyDataSetChanged()
                //更改viewPager当前定位
                aToRa_pager.currentItem = position
            }
        })

        //监听更改viewPager当前定位
        aToRa_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                tabAdapter.curr = position
                tabAdapter.notifyDataSetChanged()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })


    }

    private fun observeRankData(tabAdapter: RankTabAdapter,fragmentAdapter: RankFragmentAdapter){
        rankViewModel.rankResponse.observe(this) {resp ->
            //处理排行榜数据
            if (resp.isSuccess()){
                val data = resp.data!!
                val rankList = data.rankList
                //更新规则
                rule = data.rule
                //添加viewPager子项fragment
                fragmentList.add(RankFragment.newInstance(rankList as ArrayList<UserRank>))
                //通知viewPager刷新数据
                fragmentAdapter.notifyDataSetChanged()
            }else{
                //接口报错给出提示信息
                val msg = resp.msg ?: ResponseEnum.DATA_ERROR.msg
                msg.toast(Toast.LENGTH_LONG)
            }
            //销毁加载AlertDialog
            destroyDianDianLoading()
        }
    }

    /**
     * 排行榜更新规则 AlertDialog
     */
    private fun showRuleDialog(){
        ruleDialog = AlertDialog.Builder(this@TotalRankActivity).create().apply {
            setCancelable(true)
            setCanceledOnTouchOutside(false)
        }
        val view = View.inflate(this@TotalRankActivity,R.layout.dialog_rule,null)
        view.dia_rule.setText(rule)

        view.dia_btn.setOnClickListener {
            destroyRuleDialog()
        }

        ruleDialog.setView(view)
        //设置透明背景
        ruleDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        ruleDialog.show()


    }

    /**
     * 销毁排行榜更新规则 AlertDialog
     */
    private fun destroyRuleDialog(){
        if (::ruleDialog.isInitialized) {
            if (ruleDialog.window != null) {
                ruleDialog.window!!.decorView.post { ruleDialog.dismiss() }
            }
        }
    }


}