package com.lxy.studyroom.ui.room

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class LibraryRoomFragmentAdapter(fm: FragmentManager, val rooms: List<Fragment>) : FragmentPagerAdapter(fm){

    override fun getCount(): Int  = rooms.size

    override fun getItem(position: Int): Fragment = rooms[position]

}