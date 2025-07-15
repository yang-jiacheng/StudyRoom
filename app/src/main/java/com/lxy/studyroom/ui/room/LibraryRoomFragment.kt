package com.lxy.studyroom.ui.room

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.lxy.studyroom.BaseFragment
import com.lxy.studyroom.R
import com.lxy.studyroom.databinding.FragmentLibraryRoomBinding
import com.lxy.studyroom.logic.model.RoomDetail


class LibraryRoomFragment : BaseFragment() {

    private val ARG_PARAM1 = "param1"

    private lateinit var roomsList: ArrayList<RoomDetail>
    private var _binding: FragmentLibraryRoomBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            roomsList = it.getSerializable(ARG_PARAM1) as ArrayList<RoomDetail>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLibraryRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.FlroomRecyclerView.layoutManager = GridLayoutManager(requireContext(),2)
        val adapter = LibraryAreaAdapter(requireContext(), roomsList)
        binding.FlroomRecyclerView.adapter = adapter


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(rooms: ArrayList<RoomDetail>) =
            LibraryRoomFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1,rooms)
                }
            }
    }
}