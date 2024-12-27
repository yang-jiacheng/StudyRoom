package com.lxy.studyroom.ui.main.rank

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class RankFragmentAdapter(fm: FragmentManager, val ranks: List<Fragment>) : FragmentPagerAdapter(fm){

    override fun getCount(): Int  = ranks.size

    override fun getItem(position: Int): Fragment = ranks[position]
}