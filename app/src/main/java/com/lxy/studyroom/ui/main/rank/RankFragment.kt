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
import com.lxy.studyroom.databinding.FragmentRankBinding
import com.lxy.studyroom.logic.model.RoomDetail
import com.lxy.studyroom.logic.model.UserRank
import com.lxy.studyroom.ui.room.LibraryAreaAdapter

class RankFragment : Fragment() {

    private val ARG_PARAM1 = "param1"

    private lateinit var userList: ArrayList<UserRank>
    private var _binding: FragmentRankBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentRankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rankRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = UserRankAdapter(requireContext(),userList)
        binding.rankRecyclerView.adapter = adapter

        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.rankRecyclerView.addItemDecoration(decoration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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