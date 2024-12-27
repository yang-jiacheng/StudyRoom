package com.lxy.studyroom

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lxy.studyroom.util.LogUtil

open class BaseFragment : Fragment() {

    companion object {
        const val TAG = "BaseFragment"
    }

    /**
     * 当Fragment和Activity建立关联时
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        LogUtil.e(TAG, "onAttach  ${javaClass.simpleName}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.e(TAG, "onCreate  ${javaClass.simpleName}")
    }

    /**
     * 为Fragment创建视图（加载布局）时调用
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LogUtil.e(TAG, "onCreateView  ${javaClass.simpleName}")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     * 确保与Fragment相关联的Activity已经创建完毕时调用
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LogUtil.e(TAG, "onViewCreated  ${javaClass.simpleName}")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        LogUtil.e(TAG, "onStart  ${javaClass.simpleName}")
    }

    override fun onResume() {
        super.onResume()
        LogUtil.e(TAG, "onResume  ${javaClass.simpleName}")
    }

    override fun onPause() {
        super.onPause()
        LogUtil.e(TAG, "onPause  ${javaClass.simpleName}")
    }

    override fun onStop() {
        super.onStop()
        LogUtil.e(TAG, "onStop  ${javaClass.simpleName}")
    }

    /**
     * 当与Fragment关联的视图被移除时调用
     */
    override fun onDestroyView() {
        super.onDestroyView()
        LogUtil.e(TAG, "onDestroyView  ${javaClass.simpleName}")
    }


    override fun onDestroy() {
        super.onDestroy()
        LogUtil.e(TAG, "onDestroy  ${javaClass.simpleName}")
    }

    /**
     * 当Fragment和Activity解除关联时调用
     */
    override fun onDetach() {
        super.onDetach()
        LogUtil.e(TAG, "onDetach  ${javaClass.simpleName}")
    }


}