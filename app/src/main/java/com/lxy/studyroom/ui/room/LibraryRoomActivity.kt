package com.lxy.studyroom.ui.room

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import cn.hutool.core.collection.CollUtil
import com.bumptech.glide.Glide
import com.lxy.studyroom.BaseActivity
import com.lxy.studyroom.R
import com.lxy.studyroom.databinding.ActivityLibraryRoomBinding
import com.lxy.studyroom.databinding.DialogLibraryDetailBinding
import com.lxy.studyroom.enums.ResponseEnum
import com.lxy.studyroom.extension.toast
import com.lxy.studyroom.logic.model.LibraryRoom
import com.lxy.studyroom.logic.model.RoomDetail
import com.lxy.studyroom.util.StatusBarUtil

class LibraryRoomActivity : BaseActivity() {

    private lateinit var binding: ActivityLibraryRoomBinding
    private val roomViewModel by lazy { ViewModelProvider(this).get(LibraryRoomViewModel::class.java) }
    private var libraryId: Int = 0
    private lateinit var detailDialog: AlertDialog
    private var libraryData: LibraryRoom? = null
    private val floorList = ArrayList<RoomDetail>()
    private val roomList = ArrayList<RoomDetail>()
    private val fragmentList = ArrayList<LibraryRoomFragment>()

    companion object {
        @JvmStatic
        fun actionStart(context: Context,  libraryId: Int){
            val intent = Intent(context, LibraryRoomActivity::class.java).apply {
                putExtra("library_id", libraryId)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //沉浸式状态栏setContentView之前调用
        StatusBarUtil.setStatusBarTextColor(this, Color.TRANSPARENT)
        binding = ActivityLibraryRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarLibRary)
        binding.libRoomBar.outlineProvider = null
        binding.collbarRaryRoom.outlineProvider = ViewOutlineProvider.BOUNDS
        libraryId = intent.getIntExtra("library_id", 0)
        //加载
        showDianDianLoading()

        //viewPager
        val fragmentAdapter = LibraryRoomFragmentAdapter(supportFragmentManager,fragmentList)
        binding.vpHomePager.adapter = fragmentAdapter

        //RecyclerView
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.libTab.layoutManager = layoutManager
        val tabAdapter = TabAdapter(floorList)
        binding.libTab.adapter = tabAdapter

        //获取图书馆详情数据
        roomViewModel.getClassifyDetail(libraryId)
        //监听事件
        setListener(tabAdapter,fragmentAdapter)
        //观察数据
        observeLibDetail(tabAdapter,fragmentAdapter)
    }

    override fun onRestart() {
        super.onRestart()
        //获取图书馆详情数据
        roomViewModel.getClassifyDetail(libraryId)
    }

    private fun setListener(tabAdapter: TabAdapter,fragmentAdapter: LibraryRoomFragmentAdapter){
        //返回
        binding.libraryRoomBack.setOnClickListener {
            onBackPressed()
        }
        //图书馆详情
        binding.libraryDetail.setOnClickListener {
            showLibraryDetail()
        }

        //点击RecyclerView子项item时更改viewPager当前定位
        tabAdapter.setOnItemClickListener(object : TabAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                //当前定位
                tabAdapter.curr = position
                tabAdapter.notifyDataSetChanged()
                //更改viewPager当前定位
                binding.vpHomePager.currentItem = position
            }
        })

        //监听更改viewPager当前定位
        binding.vpHomePager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                tabAdapter.curr = position
                tabAdapter.notifyDataSetChanged()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun observeLibDetail(tabAdapter: TabAdapter,fragmentAdapter: LibraryRoomFragmentAdapter){
        roomViewModel.libDetailResponse.observe(this){resp ->
            floorList.clear()
            roomList.clear()
            val flag = resp.isSuccess()
            if (flag){
                libraryData = resp.data
                if (libraryData != null){
                    val name = libraryData!!.name
                    val coverPath = libraryData!!.coverPath
                    val rooms = libraryData!!.rooms
                    binding.libDetailTitle.text = name
                    Glide.with(this).load(coverPath).into(binding.fruitImageView02)
                    //处理自习室层级关系
                    if (CollUtil.isNotEmpty(rooms)){
                        for (room in rooms!!){
                            val level = room.level
                            if (level == 1){
                                floorList.add(room)
                            }else{
                                roomList.add(room)
                            }
                        }
                        //渲染数据
                        handleFragment(fragmentAdapter)
                    }
                }
            }else{
                val msg = resp.msg ?: ResponseEnum.DATA_ERROR.msg
                msg.toast(Toast.LENGTH_LONG)
            }

            tabAdapter.notifyItemRangeChanged(0, floorList.size)
            destroyDianDianLoading()
        }
    }

    private fun handleFragment(fragmentAdapter: LibraryRoomFragmentAdapter) {
        for (floor in floorList) {
            val catalogId = floor.catalogId
            val rooms = ArrayList<RoomDetail>()
            for (room in roomList) {
                val parentId = room.parentId
                if (catalogId == parentId){
                    rooms.add(room)
                }
            }
            fragmentList.add(LibraryRoomFragment.newInstance(rooms))
        }
        fragmentAdapter.notifyDataSetChanged()
    }

    //查看详情AlertDialog
    private fun showLibraryDetail(){
        if (libraryData == null){
            return
        }
        detailDialog = AlertDialog.Builder(this).create()
        detailDialog.setCancelable(true)
        detailDialog.setCanceledOnTouchOutside(false)

        val dialogBinding = DialogLibraryDetailBinding.inflate(layoutInflater)

        detailDialog.setView(dialogBinding.root)
        if (detailDialog.window != null) {
            detailDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialogBinding.libraryDetClose.setOnClickListener {
            hideLibraryDetail()
        }
        Glide.with(this).load(libraryData!!.iconPath).into(dialogBinding.libraryDetIcon)
        dialogBinding.libraryDetDesc.text = libraryData!!.description
        detailDialog.show()
    }

    private fun hideLibraryDetail(){
        if (detailDialog.isShowing) {
            if (detailDialog.window != null) {
                detailDialog.window!!.decorView.post { detailDialog.dismiss() }
            }
        }
    }
}

