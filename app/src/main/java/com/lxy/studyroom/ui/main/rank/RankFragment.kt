package com.lxy.studyroom.ui.main.rank

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.lxy.studyroom.R
import com.lxy.studyroom.logic.model.RoomDetail
import com.lxy.studyroom.logic.model.UserRank
import com.lxy.studyroom.ui.room.LibraryAreaAdapter
import kotlinx.android.synthetic.main.fragment_library_room.*
import kotlinx.android.synthetic.main.fragment_rank.*

class RankFragment : Fragment() {

    private val ARG_PARAM1 = "param1"

    private lateinit var userList: ArrayList<UserRank>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userList = it.getSerializable(ARG_PARAM1) as ArrayList<UserRank>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rankRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = UserRankAdapter(requireContext(),userList)
        rankRecyclerView.adapter = adapter

        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        rankRecyclerView.addItemDecoration(decoration)
    }

    companion object {
        fun newInstance(userList: ArrayList<UserRank>) =
            RankFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, userList)
                }
            }
    }
}