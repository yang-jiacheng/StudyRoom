package com.lxy.studyroom.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.lxy.studyroom.logic.model.ResponseResult
import java.util.*
import kotlin.math.ceil

object CommonUtil {

    fun getStatusBarHeight(context: Context): Float {
        return ceil(25 * context.resources.displayMetrics.density)
    }

    fun getRandomCode(): String {
        val random = Random()
        val ranNum = (random.nextDouble() * (999999 - 100000 + 1)).toInt() + 100000
        return ranNum.toString()
    }

    //计时显示格式
    fun formatMiss(time: Long): String {
        val hh = if (time / 3600 > 9) "${time / 3600}" else "0${time / 3600}"
        val mm = if ((time % 3600) / 60 > 9) "${(time % 3600) / 60}" else "0${(time % 3600) / 60}"
        val ss = if ((time % 3600) % 60 > 9) "${(time % 3600) % 60}" else "0${(time % 3600) % 60}"
        val format =
            if ("00" == hh){
                "$mm:$ss"
            }else{
                "$hh:$mm:$ss"
            }
        return format
    }

    fun getPath(context: Context,uri: Uri): String{
        var path = ""
        val projection = arrayOf("_data")
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, projection, null, null, null);
            val columnIndex = cursor!!.getColumnIndexOrThrow("_data")
            if (cursor.moveToFirst()){
                path = cursor.getString(columnIndex)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            cursor?.close()
        }

        return path
    }

    fun <T> handleResp(resp: ResponseResult<T>,
                       successFun: (successResult: ResponseResult<T>) -> Unit,
                       errorFun: (errorResult: ResponseResult<T>) -> Unit){
        if (resp.isSuccess()){
            successFun(resp)
        }else{
            errorFun(resp)
        }
    }


}
